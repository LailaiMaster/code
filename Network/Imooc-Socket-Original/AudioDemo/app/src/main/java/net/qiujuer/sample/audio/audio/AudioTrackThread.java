package net.qiujuer.sample.audio.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Process;

import net.qiujuer.opus.OpusDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 声音收集线程
 */
public class AudioTrackThread extends Thread {
    private final InputStream mInputStream;
    private final AudioTrack mAudioTrack;
    private AcousticEchoCanceler mAcousticEchoCanceler;

    public AudioTrackThread(InputStream inputStream, int audioSessionId) {
        mInputStream = inputStream;

        // 播放器内部缓冲区大小
        final int minBufferSize = AudioTrack.getMinBufferSize(AudioContract.SAMPLE_RATE, AudioContract.AUDIO_CHANNEL_OUT, AudioFormat.ENCODING_PCM_16BIT);

        // 初始化播放器
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, AudioContract.SAMPLE_RATE,
                AudioContract.AUDIO_CHANNEL_OUT, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM, audioSessionId);

        // 初始化回音消除
        try {
            AcousticEchoCanceler acousticEchoCanceler = AcousticEchoCanceler.create(audioSessionId);
            acousticEchoCanceler.setEnabled(true);
            mAcousticEchoCanceler = acousticEchoCanceler;
        } catch (Exception e) {
            mAcousticEchoCanceler = null;
        }
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_MORE_FAVORABLE);

        AudioTrack audioTrack = mAudioTrack;

        // 原始PCM数据
        final byte[] pcmBuffer = new byte[AudioContract.FRAME_SIZE * AudioContract.NUM_CHANNELS * AudioContract.OPUS_PCM_STRUCT_SIZE];
        // 压缩后的数据
        final byte[] encodeBuffer = new byte[1024];
        // 分配2字节用以读取每次压缩的数据体大小
        final ByteBuffer encodeSizeBuffer = ByteBuffer.allocate(2);

        // 解压
        OpusDecoder decoder = new OpusDecoder(AudioContract.SAMPLE_RATE, AudioContract.NUM_CHANNELS);

        // 开始
        audioTrack.play();

        try {
            while (!Thread.interrupted()) {
                // 获取前2字节，用以计算长度
                encodeSizeBuffer.clear();
                fullData(mInputStream, encodeSizeBuffer.array(), 2);
                encodeSizeBuffer.position(2);
                encodeSizeBuffer.flip();
                int encodeSize = encodeSizeBuffer.getShort();

                // 填充压缩数据体
                if (!fullData(mInputStream, encodeBuffer, encodeSize)) {
                    continue;
                }

                // 解压数据
                int pcmSize = decoder.decode(encodeBuffer, encodeSize, pcmBuffer, AudioContract.FRAME_SIZE);

                // 播放
                audioTrack.write(pcmBuffer, 0, pcmSize);
                audioTrack.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            audioTrack.stop();
            audioTrack.release();
            decoder.release();
            if (mAcousticEchoCanceler != null) {
                mAcousticEchoCanceler.release();
            }
        }
    }


    private boolean fullData(InputStream inputStream, byte[] bytes, int size) throws IOException {
        int readSize = 0;
        do {
            int read = inputStream.read(bytes, readSize, size - readSize);
            if (read == -1) {
                return false;
            }
            readSize += read;
        } while (readSize < size);
        return true;
    }
}
