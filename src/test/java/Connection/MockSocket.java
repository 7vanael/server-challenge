package Connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MockSocket extends Socket {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private boolean closed = false;

    public MockSocket(String request){
        this.inputStream = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        this.outputStream = new ByteArrayOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void close(){
        closed = true;
    }

    @Override
    public boolean isClosed(){
        return closed;
    }

    public String getResponse() {
        return outputStream.toString();
    }
}
