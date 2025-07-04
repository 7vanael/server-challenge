package Connection;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class MultiPart {
    HashMap<String, String> headers = new HashMap<>();
    byte[] content = new byte[0];
    String name;
    String filename;

    public HashMap<String, String> getHeaders() { return headers; }
    public byte[] getContent() { return content; }
    public String getName() { return name; }
    public String getFilename() { return filename; }

    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8).trim();
    }

    public String getContentType() {
        return headers.get("content-type");
    }

    public boolean isFile() {
        return filename != null && !filename.isEmpty();
    }
}
