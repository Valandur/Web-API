package valandur.webapi.ipcomm.internal;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class InternalInputStream extends ServletInputStream {

    private ByteArrayInputStream stream;


    public InternalInputStream(String content) {
        this.stream = new ByteArrayInputStream(content.getBytes());
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }
}
