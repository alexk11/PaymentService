package com.iprody.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpServer {

    private static final String SOURCE_DIR = "static";
    private static final int PORT = 8084;
    private static final String HTTP_VERSION = "HTTP/1.1";

    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "application/javascript",
            "json", "application/json",
            "txt", "text/plain",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif"
    );

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started at http://localhost:" + PORT);
            while (true) {
                serveRequest(serverSocket.accept());
            }
        }
    }

    /**
     * Request handler
     * @param socket: the client socket
     */
    private static void serveRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             socket) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                sendResponse(out, 400, "Bad Request", "text/plain", "Bad request");
                return;
            }

            String fileName = extractFileName(requestLine);
            if (fileName.isEmpty()) {
                sendResponse(out, 400, "Bad Request", "text/plain", "Bad request");
                return;
            }

            File file = getFileAtPath(fileName);
            if (file == null) {
                sendResponse(out, 404, "Not Found", "text/plain", "File not found");
                return;
            }

            String fileExtension = extractFileExtension(fileName);
            if (fileExtension.isEmpty()) {
                sendResponse(out, 400, "Bad Request", "text/plain", "Wrong file name format");
                return;
            }

            String content = Files.readString(file.toPath(), UTF_8);
            String mimeType = MIME_TYPES.getOrDefault(fileExtension, "application/octet-stream");
            sendResponse(out, 200, "OK", mimeType, content);

        } catch (IOException e) {
            System.err.println("Error serving request: " + e.getMessage());
        }
    }

    private static void sendResponse(BufferedWriter out,
                                     int statusCode,
                                     String statusText,
                                     String contentType,
                                     String body) throws IOException {
        out.write(HTTP_VERSION + " " + statusCode + " " + statusText + "\r\n");
        out.write("Content-Type: " + contentType + "; charset=UTF-8" + "\r\n");
        out.write("Content-Length: " + body.length() + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.flush();
    }

    /**
     * Extract filename from the URL.
     *
     * @param line in format 'GET /index.html HTTP/1.1'
     * @return the name of the file or an empty string
     */
    private static String extractFileName(String line) {
        String[] arr = line.split("\\s+");
        return arr.length >= 2 ? arr[1] : "";
    }

    /**
     * Extract file extension from the filename.
     *
     * @param filename the name of the file in format '/index.html'
     * @return the file extension, here 'html', or an empty string if there is no extension
     */
    private static String extractFileExtension(String filename) {
        int dotInx = filename.lastIndexOf(".");
        if (dotInx > 0 && dotInx < filename.length() - 1) {
            return filename.substring(dotInx + 1);
        }
        return "";
    }

    /**
     * Check if file exists in the /resources/static folder
     *
     * @param fileName the name of the file, for example /index.html
     * @return the file or null if the file was not found
     */
    private static File getFileAtPath(String fileName) {
        URL resourceUrl = HttpServer.class.getClassLoader().getResource(SOURCE_DIR + fileName);
        if (resourceUrl != null) {
            File file = new File(resourceUrl.getFile());
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

}
