package clink.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * 发送包的定义
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/18 17:18
 */
public abstract class SendPacket<Stream extends InputStream> extends Packet<Stream> {

    private boolean isCanceled = false;

    public boolean isCanceled() {
        return isCanceled;
    }

    public void cancel() {
        isCanceled = true;
    }

    /**
     * 获取当前可用数据大小，PS：
     * <p>
     *     <li>流的类型有限制，文件流一般可用正常获取，对于正在填充的流不一定有效，或得不到准确值</li>
     *     <li>我们利用该方法不断得到直流传输的可发送数据量，从而不断生成Frame</li>
     *     <li>缺陷：对于流的数据量大于Int有效值范围外则得不到准确值</li>
     * </p>
     *
     * 一般情况下，发送数据包时不使用该方法，而使用总长度进行运算，对于直流传输则需要使用该方法，因为对于直流而言没有最大长度
     *
     * @return 默认返回stream的可用数据量：0代表无数据可输出了
     */
    public int available() {
        InputStream stream = open();
        try {
            int available = stream.available();
            if (available < 0) {
                return 0;
            }
            return available;
        } catch (IOException e) {
            return 0;
        }
    }

}