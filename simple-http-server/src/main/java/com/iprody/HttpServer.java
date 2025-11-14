package com.iprody;

import java.io.*;
import java.net.*;


public class HttpServer {

    public static void main(String[] args) throws IOException {
        // create and open server socket
        ServerSocket serverSocket = new ServerSocket(8088);
        System.out.println("Server started at http://localhost:8088");
        // listen for client requests
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
                    if (fileSize != -1) { // file found
                        String fileExtension = extractFileExtension(filenameFromUrl);
                        if (!fileExtension.isEmpty()) { // file has extension
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
        if (!line.isEmpty()) {
            String[] arr = line.split("\\s+");
            if (arr.length >= 2) {
                return arr[1];
            }
        }
        return "";
    }

    /**
     * Extract filename from the URL.
     * @param filename: the name of the file in format '/index.html'
     * @return the file extension, here 'html', or an empty string if there is no extension
     */
    private static String extractFileExtension(String filename) {
        String[] arr = filename.split("\\.");
        if (arr.length >= 2) {
            return arr[arr.length-1];
        }
        return "";
    }

    /**
     * Check if file exists in the /resources/static folder and return its size in bytes,
     * or -1 if the file was not found
     * @param filename the name of the file, for example /index.html
     * @return size of the file in bytes
     */
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
