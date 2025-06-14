import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Request {
    private String method;
    private String path;
    private String protocol;
    private String originalPath;
    private String[] requestLine;
    private int errorCode = 0;
    private Path targetPath;
    private final Path rootPath;
    private boolean valid = false;


    public Request(BufferedReader clientMessageInstream, Path rootPath) throws IOException {
        this.rootPath = rootPath;
        parseRequest(clientMessageInstream);
    }

    private void parseRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        parseRequestLine(requestLine);

        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.trim().isEmpty()) {

        }
    }

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
        originalPath = parts[1];
        protocol = parts[2]; //Probably don't need this?

        path = processPath(originalPath);

        if (!validatePath()) {
            return;
        }
        valid = true;
    }

    private String processPath(String rawPath){
        String processedPath = rawPath;
        if(processedPath.isEmpty()||processedPath.equals("/")) {
            processedPath = "index.html";
        }
        if(processedPath.startsWith("/")) {
            processedPath = processedPath.substring(1);
        }
        return processedPath;
    }

    private boolean validatePath(){
        try {
            targetPath = rootPath.resolve(path).normalize();
            if(!targetPath.startsWith(rootPath)){
                errorCode = 403;
                return false;
            }
            if(!Files.exists(targetPath)){
                errorCode = 404;
                return false;
            }
            if (Files.isDirectory(targetPath)) {
                Path indexPath = targetPath.resolve("index.html");
                if (Files.exists(indexPath)) {
                    targetPath = indexPath;
                    path = path + "/index.html";
                } else {
                    errorCode = 404; // No index.html in directory
                    return false;
                }
            }
            return true;
        } catch (Exception e){
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

    private boolean freeFromErrors(String request, Path rootPath) {

        requestLine = request.split(" ");
        if (requestLine.length < 3) {
            errorCode = 400;
            return false;
        }
        targetPath = rootPath.resolve(requestLine[1]).normalize();
        if (!targetPath.startsWith(rootPath)) {
            errorCode = 403;
            return false;
        }
        if (!Files.exists(targetPath)) {
            errorCode = 404;
            return false;
        }
        return true;
    }
}