package me.ztiany.io.bio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 服用流的一种方式
 */
public class StreamReuse {

    private InputStream input;

    public StreamReuse(InputStream input) {
        if (!input.markSupported()) {
            this.input = new BufferedInputStream(input);
        } else {
            this.input = input;
        }
    }

    public InputStream getInputStream() {
        input.mark(Integer.MAX_VALUE);
        return input;
    }

    public void markUsed() throws IOException {
        input.reset();
    }

}