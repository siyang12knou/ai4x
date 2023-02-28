package com.kailoslab.ai4x.logic.io;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class StringOutputStream extends OutputStream {

    private StringBuffer sb;
    private boolean isErr;

    public StringOutputStream() {
        this(new StringBuffer());
    }

    public StringOutputStream(boolean isErr) {
        this(new StringBuffer(), isErr);
    }

    public StringOutputStream(StringBuffer sb) {
        this(sb, false);
    }

    public StringOutputStream(StringBuffer sb, boolean isErr) {
        this.sb = sb;
        this.isErr = isErr;
    }

    public void close() throws IOException {
        sb = new StringBuffer();
    }

    public void flush() throws IOException {
        if(isErr) {
            log.error(sb.toString());
        } else {
            log.info(sb.toString());
        }
        sb.delete(0, sb.length());
    }

    public void write(byte[] bArray) throws IOException {
        for (byte b: bArray) {
            write(b);
        }
    }

    public void write(byte b) throws IOException {
        if((char)b == '\n') {
            flush();
        } else {
            sb.append((char) b);
        }
    }

    public void write(int i) throws IOException {
        if(i == '\n') {
            flush();
        } else {
            sb.append((char) i);
        }
    }

    public String getData() {
        return sb.toString();
    }
}