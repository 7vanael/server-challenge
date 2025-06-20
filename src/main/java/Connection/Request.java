package Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Request {
    private String method;
    private String path;
    private String protocol;
    private int errorCode = 0;
    private boolean valid = false;
    private String queryString = null;
    private HashMap<String, String> headers = new HashMap<>();
    private byte[] body = new byte[0];
    private List<MultipartPart> multipartParts = new ArrayList<>();
    private String segment;


    public static Request parseRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
        Request request = new Request();
        request.parseRequestInternal(reader, inputStream);
        return request;
    }

    private void parseRequestInternal(BufferedReader in, InputStream inputStream) throws IOException {
        System.out.println("Starting request parsing...");
        try {
            String requestLine = in.readLine();
            System.out.println("Request line: " + requestLine);
            if (requestLine == null || requestLine.trim().isEmpty()) {
                errorCode = 400;
                System.out.println("Empty request line, setting error code 400");
                return;
            }

            String[] parts = requestLine.split(" ");
            if (parts.length != 3) {
                errorCode = 400;
                return;
            }

            method = parts[0];
            String fullPath = parts[1];
            protocol = parts[2];

            parseQuery(fullPath);
            parseHeaders(in);
            parseBody(in);

            valid = true;
            System.out.println("Request parsing completed successfully");

        } catch (Exception e) {
            System.out.println("Exception during parsing: " + e.getMessage());
            e.printStackTrace();
            errorCode = 400;
            valid = false;
        }
    }

    private void parseQuery(String fullPath) {
        int queryIndex = fullPath.indexOf('?');
        if (queryIndex != -1) {
            path = processPath(fullPath.substring(0, queryIndex));
            queryString = fullPath.substring(queryIndex + 1);
        } else {
            path = processPath(fullPath);
        }
    }

    private void parseHeaders(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.trim().isEmpty()) {
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String headerName = line.substring(0, colonIndex).trim().toLowerCase();
                String headerValue = line.substring(colonIndex + 1).trim();
                headers.put(headerName, headerValue);
            }
        }
    }

    private void parseBody(BufferedReader in) throws IOException {
        String contentLengthStr = headers.get("content-length");
        System.out.println("Content-Length header: " + contentLengthStr);
        if (contentLengthStr != null) {
            int contentLength = Integer.parseInt(contentLengthStr);
            System.out.println("Parsed content length: " + contentLength);
            if (contentLength > 0) {
                body = readBodyBytesFromReader(in, contentLength);
                System.out.println("Read body, actual length: " + body.length);

                String contentType = headers.get("content-type");
                System.out.println("Content-Type header: " + contentType);
                if (contentType != null && contentType.startsWith("multipart/form-data")) {
                    System.out.println("Detected multipart form data, parsing...");
                    parseMultipartBody();
                } else {
                    System.out.println("Not multipart form data");
                }
            }
        }
    }

    private byte[] readBodyBytesFromReader(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                throw new IOException("Unexpected end of stream while reading body");
            }
            totalRead += read;
        }

        return new String(buffer, 0, totalRead).getBytes(StandardCharsets.ISO_8859_1);
    }

    private void parseMultipartBody() {
        String contentType = headers.get("content-type");
        String boundary = extractBoundary(contentType);

        if (boundary == null) {
            return;
        }

        String bodyStr = new String(body, StandardCharsets.ISO_8859_1);

        System.out.println("Boundary: " + boundary);
        System.out.println("Body length: " + body.length);
        System.out.println("Body content: " + bodyStr.replace("\r", "\\r").replace("\n", "\\n"));

        String boundaryPattern = "--" + boundary;
        String[] parts = bodyStr.split(boundaryPattern);

        // just for display
        System.out.println("Parts found: " + parts.length);
        for (int i = 0; i < parts.length; i++) {
            System.out.println("Part " + i + ": '" + parts[i].replace("\r", "\\r").replace("\n", "\\n") + "'");
        }

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty() || part.equals("--") || part.startsWith("--")) {
                continue;
            }

            MultipartPart multipartPart = parseMultipartPart(part);
            if (multipartPart != null) {
                multipartParts.add(multipartPart);
            }
        }
    }

    private String extractBoundary(String contentType) {
        if (contentType == null) {
            return null;
        }

        String[] parts = contentType.split(";");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("boundary=")) {
                String boundary = part.substring(9);
                if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                    boundary = boundary.substring(1, boundary.length() - 1);
                }
                return boundary;
            }
        }
        return null;
    }

    private MultipartPart parseMultipartPart(String partContent) {
        System.out.println("Parsing part: '" + partContent.replace("\r", "\\r").replace("\n", "\\n") + "'");

        // Find division of headers from content
        int emptyLineIndex = partContent.indexOf("\r\n\r\n");
        if (emptyLineIndex == -1) {
            emptyLineIndex = partContent.indexOf("\n\n");
        }

        if (emptyLineIndex == -1) {
            System.out.println("No empty line found in part");
            return null;
        }

        String headersSection = partContent.substring(0, emptyLineIndex);
        String contentSection = partContent.substring(emptyLineIndex + (partContent.contains("\r\n\r\n") ? 4 : 2));

        System.out.println("Headers: '" + headersSection + "'");
        System.out.println("Content: '" + contentSection + "'");

        MultipartPart part = new MultipartPart();

        String[] headerLines = headersSection.split("\r?\n");
        parseMultipartHeaders(headerLines, part);

        if (contentSection.endsWith("\r\n")) {
            contentSection = contentSection.substring(0, contentSection.length() - 2);
        } else if (contentSection.endsWith("\n")) {
            contentSection = contentSection.substring(0, contentSection.length() - 1);
        }

        part.content = contentSection.getBytes(StandardCharsets.ISO_8859_1);

        System.out.println("Parsed part - name: " + part.name + ", content length: " + part.content.length);

        return part;
    }

    private void parseMultipartHeaders(String[] headerLines, MultipartPart part) {
        for (String headerLine : headerLines) {
            if (headerLine.trim().isEmpty()) continue;

            int colonIndex = headerLine.indexOf(':');
            if (colonIndex > 0) {
                String headerName = headerLine.substring(0, colonIndex).trim().toLowerCase();
                String headerValue = headerLine.substring(colonIndex + 1).trim();
                part.headers.put(headerName, headerValue);

                if (headerName.equals("content-disposition")) {
                    parseContentDisposition(headerValue, part);
                }
            }
        }
    }

    private void parseContentDisposition(String headerValue, MultipartPart part) {
        String[] parts = headerValue.split(";");
        for (String p : parts) {
            p = p.trim();
            if (p.startsWith("name=")) {
                String name = p.substring(5);
                if (name.startsWith("\"") && name.endsWith("\"")) {
                    name = name.substring(1, name.length() - 1);
                }
                part.name = name;
            } else if (p.startsWith("filename=")) {
                String filename = p.substring(9);
                if (filename.startsWith("\"") && filename.endsWith("\"")) {
                    filename = filename.substring(1, filename.length() - 1);
                }
                part.filename = filename;
            }
        }
    }

    private String processPath(String rawPath) {
        String processedPath = rawPath;
        System.out.println("Request processing Path. Raw path: " + rawPath);
        int segmentDemarcation = rawPath.substring(1).indexOf("/")  + 1; //Skip the leading /
        System.out.println("Demarcation: " + segmentDemarcation);
        if (processedPath.isEmpty() || processedPath.equals("/") || processedPath.contains("..")) {
            processedPath = "/index.html";
        }else if(segmentDemarcation > 1){
            segment = rawPath.substring(segmentDemarcation + 1);
            System.out.println("Segment: " + segment);
            processedPath = rawPath.substring(0, segmentDemarcation);
            System.out.println("Path: " + processedPath);
        }
        return processedPath;
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getProtocol() { return protocol; }
    public int getErrorCode() { return errorCode; }
    public boolean isValid() { return valid; }
    public String getQueryString() { return queryString; }
    public HashMap<String, String> getHeaders() { return headers; }
    public byte[] getBody() { return body; }
    public List<MultipartPart> getMultipartParts() { return multipartParts; }
    public String getSegment() {return segment;}

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public MultipartPart getMultipartPart(String name) {
        return multipartParts.stream()
                .filter(part -> name.equals(part.getName()))
                .findFirst()
                .orElse(null);
    }

    public String getMultipartValue(String name) {
        MultipartPart part = getMultipartPart(name);
        return part != null ? part.getContentAsString() : null;
    }

    public static class MultipartPart {
        private HashMap<String, String> headers = new HashMap<>();
        private byte[] content = new byte[0];
        private String name;
        private String filename;

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
}