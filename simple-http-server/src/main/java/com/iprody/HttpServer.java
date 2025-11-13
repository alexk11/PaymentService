package com.iprody;

import java.io.*;
import java.net.*;


public class HttpServer {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8088);
        System.out.println("Server started at http://localhost:8088");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new
                    OutputStreamWriter(clientSocket.getOutputStream()));

            // Чтение 1-й строки запроса
            String line = in.readLine();
            if (line != null && !line.isEmpty()) {
                String filenameFromUrl = extractFilename(line);
                if (!filenameFromUrl.isEmpty()) {
                    long fileSize = fileSizeAtPath(filenameFromUrl);
                    if (fileSize != -1) {
                        String fileExtension = extractFileExtension(filenameFromUrl);
                        if (!fileExtension.isEmpty()) {
                            out.write("HTTP/1.1 200 OK\r\n");
                            out.write("Content-Type: " + fileExtension + "; charset=UTF-8\r\n");
                            out.write("Content-Length: " + fileSize + "\r\n");
                        }
                    } else { // file does not exist
                        out.write("HTTP/1.1 404 Not Found\r\n");
                    }
                    out.write("\r\n");
                    out.flush();
                    clientSocket.close();
                }
            }
        }
    }

    /**
     * Extract filename from the URL.
     * @param line: in format 'GET /index.html HTTP/1.1'
     * @return the name of the file
     */
    private static String extractFilename(String line) {
        // read 1-st line of incoming request
        if (!line.isEmpty()) {
            String[] arr = line.split("\\s+");
            if (arr.length >= 2) {
                return arr[1];
            }
        }
        return "";
    }

    private static String extractFileExtension(String filename) {
        String[] arr = filename.split("\\.");
        if (arr.length >= 2) {
            return arr[arr.length-1];
        }
        return "unknown";
    }

    private static long fileSizeAtPath(String filename) {
        String filePath = "static" + filename;
        URL resourceUrl = HttpServer.class.getClassLoader().getResource(filePath);

        if (resourceUrl != null) {
            File file = new File(resourceUrl.getFile());
            if (file.exists()) {
                return file.length();
            }
        }
        return Long.parseLong(String.valueOf(-1));
    }

}
