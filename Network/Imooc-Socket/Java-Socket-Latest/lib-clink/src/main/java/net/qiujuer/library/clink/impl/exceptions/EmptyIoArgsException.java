package net.qiujuer.library.clink.impl.exceptions;

import java.io.IOException;

/**
 * IoArgs 为NULL时抛出的异常
 */
public class EmptyIoArgsException extends IOException {
    public EmptyIoArgsException(String s) {
        super(s);
    }
}