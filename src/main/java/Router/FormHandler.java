package Router;

import Connection.Request;
import Connection.Response;
import Main.HttpConstants;
import Main.RouteHandler;

import java.nio.file.Path;
import java.util.List;

public class FormHandler implements RouteHandler {
    private Path rootPath;
    private String serverName;
    private String getForm = "<html>\n" +
            "\n" +
            "<h2>GET Form</h2>\n" +
            "<form method=\"get\" action=\"/form\">\n" +
            " <label>Foo:</label>\n" +
            " <input type=\"text\" name=\"foo\"/>\n" +
            " <input type=\"submit\" value=\"Submit\"/>\n" +
            "</form>";
    private String postForm = "<hr>\n <h2>POST Form</h2>\n" +
            "<form method=\"post\" action=\"/form\" enctype=\"multipart/form-data\">\n" +
            " <label>File:</label>\n" +
            " <input type=\"file\" name=\"file\"/>\n" +
            " <input type=\"submit\" value=\"Submit\"/>\n" +
            "</form>";

    public FormHandler(Path rootPath, String serverName) {
        this.rootPath = rootPath;
        this.serverName = serverName;
    }

    @Override
    public Response handle(Request request) {
        if (request.getMethod().equals("GET")) {
            return handleGet(request);
        } else {
            return handlePost(request);
        }
    }

    private Response handleGet(Request request) {
        String queryString = request.getQueryString();
        StringBuilder parsedQueries = new StringBuilder();

        if (queryString != null && !queryString.isEmpty()) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    parsedQueries.append("<li>")
                            .append(keyValue[0])
                            .append(": ")
                            .append(keyValue[1])
                            .append("</li>");
                }
            }
        }
        String newForm = generateFormResponse(parsedQueries, new StringBuilder());

        return createHtmlResponse(200, newForm);
    }

    private Response handlePost(Request request) {
        String contentType = request.getHeader("content-type");
        StringBuilder postResults = new StringBuilder();

        if (contentType == null || !contentType.contains("multipart/form-data")) {
            return errorResponse(400);
        }
        if (contentType != null && contentType.contains("multipart/form-data")) {

            List<Request.MultipartPart> parts = request.getMultipartParts();

            for (Request.MultipartPart part : parts) {
                if (part.isFile()) {
                    postResults.append("<ul>")
                            .append("<li>field name: ").append(part.getName()).append("</li>")
                            .append("<li>file name: ").append(part.getFilename()).append("</li>")
                            .append("<li>content type: ").append(part.getContentType() != null ? part.getContentType() : "application/octet-stream").append("</li>")
                            .append("<li>file size: ").append(part.getContent().length).append("</li>")
                            .append("</ul>");
                }
            }
        }
        String html = generateFormResponse(new StringBuilder(), postResults);
        return createHtmlResponse(200, html);
    }

    private Response createHtmlResponse(int statusCode, String html) {
        return new Response(serverName, statusCode, "text/html", html);
    }

    private Response errorResponse(int errorCode) {
        String statusText = HttpConstants.STATUS_CODES.get(errorCode);
        String errorHtml = "<html><head><title>" + errorCode + " " + statusText + "</title></head>" +
                "<body><h1>" + errorCode + " " + statusText + "</h1>" +
                "Server: " + serverName + "</p></body></html>";
        return new Response(serverName, errorCode, "text/html", errorHtml);
    }

    private String generateFormResponse(StringBuilder parsedQueries, StringBuilder postResults) {
        StringBuilder html = new StringBuilder();
        html.append(getForm);
        if (!parsedQueries.isEmpty()) {
            html.append(parsedQueries);
        }
        html.append(postForm);
        if (!postResults.isEmpty()) {
            html.append(postResults);
        }
        html.append("</html>");
        return html.toString();
    }
}
