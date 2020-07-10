#include <libavformat/avformat.h>
#include <libavformat/avio.h>
#include <libavutil/log.h>
#include <stdio.h>

#ifndef AV_WB32
#define AV_WB32(p, val)                  \
    do {                                 \
        uint32_t d = (val);              \
        ((uint8_t *)(p))[3] = (d);       \
        ((uint8_t *)(p))[2] = (d) >> 8;  \
        ((uint8_t *)(p))[1] = (d) >> 16; \
        ((uint8_t *)(p))[0] = (d) >> 24; \
    } while (0)
#endif

#ifndef AV_RB16
#define AV_RB16(x)                      \
    ((((const uint8_t *)(x))[0] << 8) | \
     ((const uint8_t *)(x))[1])
#endif

/**
 * 添加特征玛 start code：
 * 
 * sps_pps：spspps_pkt.data
 * ssps_pps_size：spspps_pkt.size
 */
static int alloc_and_copy(AVPacket *out, const uint8_t *sps_pps, uint32_t sps_pps_size, const uint8_t *in, uint32_t in_size) {
    uint32_t offset = out->size;
    int err;

    //SPS/PPS的特征玛是四个字节：00 00 00 01
    //非SPS/PPS的特征玛是三个字节：00 00 01
    //但是都设置为四个字节也可以
    //offset = 0 则 nal_header_size = 4
    uint8_t nal_header_size = offset ? 3 : 4;

    av_log(NULL, AV_LOG_INFO, "------------添加 Start code 处理，offset = %d, nal_header_size = %d, sps_pps_size = %d\n",offset,nal_header_size,sps_pps_size);

    err = av_grow_packet(out, sps_pps_size + in_size + nal_header_size);

    if (err < 0){
        return err;
    }

    //如果传进来的数据带 sps/pps，则进行拷贝 sps/pps 数据
    if (sps_pps) {
        memcpy(out->data + offset, sps_pps, sps_pps_size);
    }

    memcpy(out->data + sps_pps_size + nal_header_size + offset, in, in_size);

    if (!offset) {//设置 Start Code 为 00 00 00 01
        AV_WB32(out->data + sps_pps_size, 1);
    } else {//设置  Start Code  为 00 00 01
        (out->data + offset + sps_pps_size)[0] =
            (out->data + offset + sps_pps_size)[1] = 0;
        (out->data + offset + sps_pps_size)[2] = 1;
    }

    return 0;
}

//读取 sps/pps 到 out_extradata 中
int h264_extradata_to_annexb(const uint8_t *codec_extradata, const int codec_extradata_size, AVPacket *out_extradata, int padding) {
    av_log(NULL, AV_LOG_INFO, "------------新的 sps/pps 处理\n");

    uint16_t unit_size;
    uint64_t total_size = 0;
    uint8_t *out = NULL, unit_nb, sps_done = 0, sps_seen = 0, pps_seen = 0, sps_offset = 0, pps_offset = 0;

    //扩增数据的前四个字节没用，跳过
    const uint8_t *extradata = codec_extradata + 4;

    static const uint8_t nalu_header[4] = {0, 0, 0, 1};

    //然后下一个字节用于表示后面每一个 sps/pps 所需字节数
    int length_size = (*extradata++ & 0x3) + 1;  // retrieve length coded size, 用于指示表示编码数据长度所需字节数
    av_log(NULL, AV_LOG_INFO, "------------length_size = %d\n", length_size);

    sps_offset = pps_offset = -1;

    /* sps/pps 的 个数，一般只有一个*/
    /* retrieve sps and pps unit(s) */

    unit_nb = *extradata++ & 0x1f; /* number of sps unit(s) */
    av_log(NULL, AV_LOG_INFO, "------------unit_nb = %d\n", unit_nb);

    if (!unit_nb) {
        goto pps;
    } else {
        sps_offset = 0;
        sps_seen = 1;
    }

    //遍历每个 ssp/pps
    while (unit_nb--) {
        int err;

        unit_size = AV_RB16(extradata);  //读两个字节，取   sps/pps 的长度
        total_size += unit_size + 4;     // ssp/pps 的前面也要 start code，所以又加了四个字节

        if (total_size > INT_MAX - padding) {
            av_log(NULL, AV_LOG_ERROR, "Too big extradata size, corrupted stream or invalid MP4/AVCC bitstream\n");
            av_free(out);
            return AVERROR(EINVAL);
        }

        if (extradata + 2 + unit_size > codec_extradata + codec_extradata_size) {
            av_log(NULL, AV_LOG_ERROR,
                   "Packet header is not contained in global extradata, "
                   "corrupted stream or invalid MP4/AVCC bitstream\n");
            av_free(out);
            return AVERROR(EINVAL);
        }

        if ((err = av_reallocp(&out, total_size + padding)) < 0) {  //防止输出数据长度不够
            return err;
        }

        memcpy(out + total_size - unit_size - 4, nalu_header, 4);        //拷贝nal header
        memcpy(out + total_size - unit_size, extradata + 2, unit_size);  //拷贝  sps/pps
        extradata += 2 + unit_size;

    pps:
        if (!unit_nb && !sps_done++) {
            unit_nb = *extradata++; /* number of pps unit(s) */
            if (unit_nb) {
                pps_offset = total_size;
                pps_seen = 1;
            }
        }
    }

    if (out) {
        memset(out + total_size, 0, padding);
    }

    if (!sps_seen) {
        av_log(NULL, AV_LOG_WARNING,
               "Warning: SPS NALU missing or invalid. "
               "The resulting stream may not play.\n");
    }

    if (!pps_seen) {
        av_log(NULL, AV_LOG_WARNING,
               "Warning: PPS NALU missing or invalid. "
               "The resulting stream may not play.\n");
    }

    out_extradata->data = out;
    out_extradata->size = total_size;

    return length_size;
}

int h264_mp4toannexb(AVFormatContext *fmt_ctx, AVPacket *in, FILE *dst_fd) {
    av_log(NULL, AV_LOG_INFO, "-新的Packet------------------------------------------------------------------------------------\n");
    //一个 AVPacket 存储一个帧或多个帧，一个帧包含多个片，一个NALU对应一个片

    AVPacket *out = NULL;  //用于输出
    AVPacket spspps_pkt;   //用于存储 spspps

    int len;                  //fwrite 返回值
    uint8_t unit_type;        //nal 单元类型
    int32_t nal_size;         //nal 单元的大小
    uint32_t cumul_size = 0;  //总 size
    const uint8_t *buf;       //指向 in.data
    const uint8_t *buf_end;   //指向 data 的结尾，用于做边界判断
    int buf_size;             //data.size
    int ret = 0, i;

    //创建并初始化 out
    out = av_packet_alloc();

    buf = in->data;                 //包的实际数据
    buf_size = in->size;            //包的大小，即多少个 uint8_t
    buf_end = in->data + in->size;  //指针移到最后，用于标识指针末尾

    av_log(NULL, AV_LOG_INFO, "in->size = %d \n", in->size);

    do {
        ret = AVERROR(EINVAL);
        if (buf + 4 /*s->length_size*/ > buf_end) {
            av_log(NULL, AV_LOG_INFO, "buf + 4 > buf_end, goto fail\n");
            goto fail;
        }

        //packet的每个h246帧的前4个字节存储的是该帧的size，这里拿到帧的size
        for (nal_size = 0, i = 0; i < 4 /*s->length_size*/; i++) {
            nal_size = (nal_size << 8) | buf[i];
        }
        av_log(NULL, AV_LOG_INFO, "nal_size = %d \n", nal_size);

        //NAL Header 读取
        buf += 4;                 /*s->length_size;，上面读取了4个字节，这里移位，后面是帧的实际数据*/
        unit_type = *buf & 0x1f;  //帧的第一个字节的后 5 位，存储的是 nal 单元类型，1F-->0001 1111

        av_log(NULL, AV_LOG_INFO, "unit_type = %d \n", unit_type);
        if (unit_type == 5) {
            av_log(NULL, AV_LOG_ERROR, "find unit type =5\n");
        }

        if (nal_size > buf_end - buf || nal_size < 0) {
            av_log(NULL, AV_LOG_INFO, "nal_size > buf_end - buf || nal_size < 0, goto fail\n");
            goto fail;
        }

        /*
        if (unit_type == 7)
            s->idr_sps_seen = s->new_idr = 1;
        else if (unit_type == 8) {
            s->idr_pps_seen = s->new_idr = 1;
            */
        /* if SPS has not been seen yet, prepend the AVCC one to PPS */
        /*
            if (!s->idr_sps_seen) {
                if (s->sps_offset == -1)
                    av_log(ctx, AV_LOG_WARNING, "SPS not present in the stream, nor in AVCC, stream may be unreadable\n");
                else {
                    if ((ret = alloc_and_copy(out,
                                         ctx->par_out->extradata + s->sps_offset,
                                         s->pps_offset != -1 ? s->pps_offset : ctx->par_out->extradata_size - s->sps_offset,
                                         buf, nal_size)) < 0)
                        goto fail;
                    s->idr_sps_seen = 1;
                    goto next_nal;
                }
            }
        }
        */

        /* if this is a new IDR picture following an IDR picture, reset the idr flag.
         * Just check first_mb_in_slice to be 0 as this is the simplest solution.
         * This could be checking idr_pic_id instead, but would complexify the parsing. */
        /*
        if (!s->new_idr && unit_type == 5 && (buf[1] & 0x80))
            s->new_idr = 1;

        */

        //处理关键帧
        /* prepend only to the first type 5 NAL unit of an IDR picture, if no sps/pps are already present */
        if (/*s->new_idr && */ unit_type == 5 /*&& !s->idr_sps_seen && !s->idr_pps_seen*/) {
            //如果是关键帧，其前面要有SPS/PPS，从扩展数据中获取 SPS/PPS
            h264_extradata_to_annexb(
                fmt_ctx->streams[in->stream_index]->codec->extradata,
                fmt_ctx->streams[in->stream_index]->codec->extradata_size,
                &spspps_pkt,
                AV_INPUT_BUFFER_PADDING_SIZE);

            //为数据添加特征码
            if ((ret = alloc_and_copy(out, spspps_pkt.data, spspps_pkt.size, buf, nal_size)) < 0) {
                av_log(NULL, AV_LOG_INFO, "unit_type = 5, alloc_and_copy goto fail, ret = %d, error = %s\n", ret, av_err2str(ret));
                goto fail;
            }
            /*s->new_idr = 0;*/
            /* if only SPS has been seen, also insert PPS */
        }
        /*else if (s->new_idr && unit_type == 5 && s->idr_sps_seen && !s->idr_pps_seen) {
            if (s->pps_offset == -1) {
                av_log(ctx, AV_LOG_WARNING, "PPS not present in the stream, nor in AVCC, stream may be unreadable\n");
                if ((ret = alloc_and_copy(out, NULL, 0, buf, nal_size)) < 0)
                    goto fail;
            } else if ((ret = alloc_and_copy(out,
                                        ctx->par_out->extradata + s->pps_offset, ctx->par_out->extradata_size - s->pps_offset,
                                        buf, nal_size)) < 0)
                goto fail;
        }*/
        else {  //非关键帧，直接拷贝
            if ((ret = alloc_and_copy(out, NULL, 0, buf, nal_size)) < 0) {
                av_log(NULL, AV_LOG_INFO, "unit_type = other, alloc_and_copy goto fail, ret = %d, error = %s\n", ret, av_err2str(ret));
                goto fail;
            }
            /*
            if (!s->new_idr && unit_type == 1) {
                s->new_idr = 1;
                s->idr_sps_seen = 0;
                s->idr_pps_seen = 0;
            }
            */
        }

        len = fwrite(out->data, 1, out->size, dst_fd);
        if (len != out->size) {
            av_log(NULL, AV_LOG_DEBUG, "warning, length of writed data isn't equal pkt.size(%d, %d)\n", len, out->size);
        }
        fflush(dst_fd);

    next_nal:
        buf += nal_size;
        cumul_size += nal_size + 4;  //s->length_size;
        av_log(NULL, AV_LOG_INFO, "处理完一个 NAL\n");
    } while (cumul_size < buf_size); /*整个流程就是一帧一帧地处理*/

    /*
    ret = av_packet_copy_props(out, in);
    if (ret < 0)
        goto fail;
    */

fail:
    av_packet_free(&out);

    av_log(NULL, AV_LOG_INFO, "处理完一个 AVPacket，cumul_size = %d\n\n", cumul_size);
    return ret;
}

// gcc -g extr_video.c -o extr_video.out `pkg-config --libs libavutil libavformat`
// ./extr_video.out ~/code/leaning/1-crop.mp4 out.h264
int main(int argc, char *argv[]) {
    int err_code;
    char errors[1024];

    char *src_filename = NULL;
    char *dst_filename = NULL;

    FILE *dst_fd = NULL;

    int video_stream_index = -1;

    //AVFormatContext *ofmt_ctx = NULL;
    //AVOutputFormat *output_fmt = NULL;
    //AVStream *out_stream = NULL;

    AVFormatContext *fmt_ctx = NULL;
    AVPacket pkt;

    //AVFrame *frame = NULL;

    av_log_set_level(AV_LOG_DEBUG);

    if (argc < 3) {
        av_log(NULL, AV_LOG_DEBUG, "the count of parameters should be more than three!\n");
        return -1;
    }

    src_filename = argv[1];
    dst_filename = argv[2];

    if (src_filename == NULL || dst_filename == NULL) {
        av_log(NULL, AV_LOG_ERROR, "src or dts file is null, plz check them!\n");
        return -1;
    }

    /*register all formats and codec*/
    av_register_all();

    dst_fd = fopen(dst_filename, "wb");
    if (!dst_fd) {
        av_log(NULL, AV_LOG_DEBUG, "Could not open destination file %s\n", dst_filename);
        return -1;
    }

    /*open input media file, and allocate format context*/
    if ((err_code = avformat_open_input(&fmt_ctx, src_filename, NULL, NULL)) < 0) {
        av_strerror(err_code, errors, 1024);
        av_log(NULL, AV_LOG_DEBUG, "Could not open source file: %s, %d(%s)\n", src_filename, err_code, errors);
        return -1;
    }

    /*dump input information*/
    av_dump_format(fmt_ctx, 0, src_filename, 0);

    /*initialize packet*/
    av_init_packet(&pkt);
    pkt.data = NULL;
    pkt.size = 0;

    /*find best video stream*/
    video_stream_index = av_find_best_stream(fmt_ctx, AVMEDIA_TYPE_VIDEO, -1, -1, NULL, 0);
    if (video_stream_index < 0) {
        av_log(NULL, AV_LOG_DEBUG, "Could not find %s stream in input file %s\n", av_get_media_type_string(AVMEDIA_TYPE_VIDEO), src_filename);
        return AVERROR(EINVAL);
    }

    /*
    if (avformat_write_header(ofmt_ctx, NULL) < 0) {
        av_log(NULL, AV_LOG_DEBUG, "Error occurred when opening output file");
        exit(1);
    }
    */

    /*read frames from media file*/
    while (av_read_frame(fmt_ctx, &pkt) >= 0) {
        if (pkt.stream_index == video_stream_index) {
            /*
            pkt.stream_index = 0;
            av_write_frame(ofmt_ctx, &pkt);
            av_free_packet(&pkt);
            */

            //设置 start code 和 SPS/PPS
            h264_mp4toannexb(fmt_ctx, &pkt, dst_fd);
        }

        //release pkt->data
        av_packet_unref(&pkt);
    }

    //av_write_trailer(ofmt_ctx);

    /*close input media file*/
    avformat_close_input(&fmt_ctx);
    if (dst_fd) {
        fclose(dst_fd);
    }

    //avio_close(ofmt_ctx->pb);

    return 0;
}
