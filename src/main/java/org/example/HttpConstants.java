package org.example;

import java.util.Map;

public class HttpConstants {
    public static final Map<Integer, String> STATUS_CODES = Map.of(
            200, "OK",
            400, "Bad Connection.Request",
            404, "Not Found",
            405, "Method Not Allowed",
            500, "Internal org.example.Server Error",
            501, "Not Implemented"
    );

    public static final Map<String, String> CONTENT_TYPES = Map.of(
            ".html", "text/html",
            ".png", "image/png",
            ".jpg", "image/jpeg",
            ".jpeg", "image/jpeg",
            ".gif", "image/gif",
            ".txt", "text/plain"
    );

    public static final String PROTOCOL_VERSION = "HTTP/1.1";
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
}
