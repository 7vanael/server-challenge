import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Request {
    private String method;
    private String path;
    private String protocol;
    private int errorCode = 0;
    private Path targetPath;
    private Path rootPath;
    private boolean valid = false;

    public static Request parseRequest(BufferedReader in, Path rootPath) throws IOException {
        Request request = new Request();
        request.rootPath = rootPath;
        request.parseRequestInternal(in);
        return request;
    }

    private void parseRequestInternal(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        parseRequestLine(requestLine);

        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.trim().isEmpty()) {
//            parseHeaderLine(headerLine);
        }
    }

//    private void parseHeaderLine(String headerLine) {
//        int colonIndex = headerLine.indexOf(":");
//        if (colonIndex > 0) {
//            String name = headerLine.substring(0, colonIndex).trim();
//            String value = headerLine.substring(colonIndex + 1).trim();
//            headers.put(name, value);
//        }
//    }

    private void parseRequestLine(String requestLine) {
        if (requestLine == null) {
            errorCode = 400;
            return;
        }
        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            errorCode = 400;
            return;
        }
        method = parts[0];
        String originalPath = parts[1];
        protocol = parts[2];

        path = processPath(originalPath);

        if (!validatePath()) {
            return;
        }
        valid = true;
    }

    private String processPath(String rawPath) {
        String processedPath = rawPath;
        if (processedPath.isEmpty() || processedPath.equals("/")) {
            processedPath = "index.html";
        }
        if (processedPath.startsWith("/")) {
            processedPath = processedPath.substring(1);
        }
        return processedPath;
    }

    private boolean validatePath() {
        try {
            targetPath = rootPath.resolve(path).normalize();
            if (!targetPath.startsWith(rootPath)) {
                errorCode = 403;
                return false;
            }
            if (!Files.exists(targetPath)) {
                errorCode = 404;
                return false;
            }
//            if (Files.isDirectory(targetPath)) {
//                Path indexPath = targetPath.resolve("index.html");
//                if (Files.exists(indexPath)) {
//                    targetPath = indexPath;
//                    path = path + "/index.html";
//                }
//            }
            return true;
        } catch (Exception e) {
            errorCode = 500;
            return false;
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public boolean isValid() {
        return valid;
    }
//    public Map<String, String> getHeaders() {
//        return headers;
//    }
//
//    public String getHeader(String name) {
//        return headers.get(name);
//    }
}