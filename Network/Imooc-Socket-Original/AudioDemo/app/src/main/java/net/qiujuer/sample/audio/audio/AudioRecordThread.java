package net.qiujuer.sample.audio.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;

import net.qiujuer.opus.OpusEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * 声音收集线程
 */
public class AudioRecordThread extends Thread {
    private final OutputStream mOutputStream;
    private final AudioRecord mAudioRecord;

    public AudioRecordThread(OutputStream outputStream) {
        mOutputStream = outputStream;

        // 麦克风内部缓冲区大小
        final int minBufferSize = AudioRecord.getMinBufferSize(AudioContract.SAMPLE_RATE, AudioContract.AUDIO_CHANNEL_IN, AudioFormat.ENCODING_PCM_16BIT);

        // 初始化录音器
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AudioContract.SAMPLE_RATE,
                AudioContract.AUDIO_CHANNEL_IN, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
    }

    public AudioRecord getAudioRecord() {
        return mAudioRecord;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_MORE_FAVORABLE);

        final AudioRecord audioRecord = mAudioRecord;

        // 原始PCM数据
        final byte[] pcmBuffer = new byte[AudioContract.FRAME_SIZE * AudioContract.NUM_CHANNELS * AudioContract.OPUS_PCM_STRUCT_SIZE];
        // 压缩后的数据， 前2字节用以存储压缩长度，从第3字节开始存储压缩数据
        final byte[] encodeBuffer = new byte[256];
        // 用以快速设置长度信息的包裹用法
        final ByteBuffer encodeSizeBuffer = ByteBuffer.wrap(encodeBuffer, 0, 2);

        // 压缩器
        OpusEncoder encoder = new OpusEncoder(AudioContract.SAMPLE_RATE, AudioContract.NUM_CHANNELS, OpusEncoder.OPUS_APPLICATION_VOIP);
        encoder.setComplexity(4);

        // 开始
        audioRecord.startRecording();

        try {
            while (!Thread.interrupted()) {
                // 本次需要读取的数据总大小，填满缓冲区
                int canReadSize = pcmBuffer.length;
                // 已读大小
                int readSize = 0;
                while (canReadSize > 0) {
                    // 每次读取大小
                    int onceReadSize = audioRecord.read(pcmBuffer, readSize, canReadSize);
                    if (onceReadSize < 0) {
                        throw new RuntimeException("recorder.read() returned error:" + onceReadSize);
                    }
                    canReadSize -= onceReadSize;
                    readSize += onceReadSize;
                }

                // 压缩数据，存储区间从位移2个字节后开始
                int encodeSize = encoder.encode(pcmBuffer, 0, encodeBuffer, 2, AudioContract.FRAME_SIZE);

                // 存储大小信息
                encodeSizeBuffer.clear();
                encodeSizeBuffer.putShort((short) encodeSize);

                // 发送数据
                mOutputStream.write(encodeBuffer, 0, encodeSize + 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            audioRecord.stop();
            audioRecord.release();
            encoder.release();
        }
    }
}
