package Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Request implements RequestI {
    private String method;
    private String path;
    private String protocol;
    private int errorCode = 0;
    private boolean valid = false;
    private String queryString = null;
    private HashMap<String, String> headers = new HashMap<>();
    private byte[] body = new byte[0];
    private List<MultiPart> multiParts = new ArrayList<>();
    private String segment;
    private String cookieString;
    private HashMap<String, String> cookies = new HashMap<>();


    public static Request parseRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
        Request request = new Request();
        request.parseRequestInternal(reader);
        return request;
    }

    private void parseRequestInternal(BufferedReader in) {
        try {
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.trim().isEmpty()) {
                errorCode = 400;
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
        } catch (Exception e) {
            System.out.println("Exception during parsing: " + e.getMessage());
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
        parseCookies();
    }

    private void parseCookies() {
        String cookieHeader = headers.get("cookie");
        if(cookieHeader != null){
            this.cookieString = cookieHeader;
            String[] cookiePairs = headers.get("cookie").split(";");
            for (String cookie : cookiePairs) {
                if (cookie.trim().isEmpty()) continue;

                int equalsIndex = cookie.indexOf('=');
                if (equalsIndex > 0) {
                    String cookieName = cookie.substring(0, equalsIndex).trim();//Does this need to go to lowercase also?
                    String cookieValue = cookie.substring(equalsIndex + 1).trim();
                    cookies.put(cookieName, cookieValue);
                }
            }
        }
    }

    private void parseBody(BufferedReader in) throws IOException {
        String contentLengthStr = headers.get("content-length");
        if (contentLengthStr != null) {
            int contentLength = Integer.parseInt(contentLengthStr);
            if (contentLength > 0) {
                body = readBodyBytesFromReader(in, contentLength);

                String contentType = headers.get("content-type");
                if (contentType != null && contentType.startsWith("multipart/form-data")) {
                    parseMultipartBody();
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

        String boundaryPattern = "--" + boundary;
        String[] parts = bodyStr.split(boundaryPattern);

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty() || part.equals("--") || part.startsWith("--")) {
                continue;
            }

            MultiPart multiPart = parseMultipartPart(part);
            if (multiPart != null) {
                multiParts.add(multiPart);
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

    private MultiPart parseMultipartPart(String partContent) {

        int emptyLineIndex = partContent.indexOf("\r\n\r\n");

        String headersSection = partContent.substring(0, emptyLineIndex);
        String contentSection = partContent.substring(emptyLineIndex + 4);

        MultiPart part = new MultiPart();

        String[] headerLines = headersSection.split("\r\n");
        parseMultipartHeaders(headerLines, part);

        if (contentSection.endsWith("\r\n")) {
            contentSection = contentSection.substring(0, contentSection.length() - 2);
        }

        part.content = contentSection.getBytes(StandardCharsets.ISO_8859_1);
        return part;
    }

    private void parseMultipartHeaders(String[] headerLines, MultiPart part) {
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

    private void parseContentDisposition(String headerValue, MultiPart part) {
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
        if (rawPath.isEmpty() || rawPath.equals("/") || rawPath.contains("..")) {
            processedPath = "/index.html";
        }
        if (rawPath.startsWith("/ping/")) {
            int segmentDemarcation = processedPath.substring(1).indexOf("/")  + 1; //Skip the leading /
            if(segmentDemarcation > 1){
                segment = processedPath.substring(segmentDemarcation + 1);
                processedPath = processedPath.substring(0, segmentDemarcation);
            }
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
    public List<MultiPart> getMultipartParts() { return multiParts; }
    public String getSegment() {return segment;}

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public MultiPart getMultipartPart(String name) {
        return multiParts.stream()
                .filter(part -> name.equals(part.getName()))
                .findFirst()
                .orElse(null);
    }

    public String getMultipartValue(String name) {
        MultiPart part = getMultipartPart(name);
        return part != null ? part.getContentAsString() : null;
    }

    public HashMap<String, String> getCookies() {
        return cookies;
    }


}