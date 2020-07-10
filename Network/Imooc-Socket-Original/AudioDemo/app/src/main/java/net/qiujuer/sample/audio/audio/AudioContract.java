package net.qiujuer.sample.audio.audio;

import android.media.AudioFormat;

import net.qiujuer.opus.OpusConstant;

/**
 * 基础参数定义
 */
public interface AudioContract {
    // 采样率
    int SAMPLE_RATE = 8000;

    // 每次用以压缩的样本数量
    int FRAME_SIZE = 480;

    // 通道 1 or 2
    int NUM_CHANNELS = 1;

    // OPUS 压缩PCM数据结构对应Byte比例
    int OPUS_PCM_STRUCT_SIZE = OpusConstant.OPUS_PCM_STRUCT_SIZE_OF_BYTE;

    // 音频输入通道
    int AUDIO_CHANNEL_IN = NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;

    // 音频输出通道
    int AUDIO_CHANNEL_OUT = NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
}
