package com.ztiany.basic.generic.erasure;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/26 15:59
 */
public class OChildNode extends ONode {

    @Override
    public void setData(Number data) {
        super.setData(data);
    }

    /*
    下面两种都无法够成方法覆盖。
    @Override
    public void setData(Object data) {
        super.setData(data);
    }

    @Override
    public void setData(Integer data) {
        super.setData(data);
    }
     */

    @Override
    public Number getData() {
        return super.getData();
    }

    /*
    下面方式可以够成方法覆盖。
    @Override
    public Integer getData() {
        return super.getData();
    }
     */

}
