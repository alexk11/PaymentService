package com.iprody;

import java.io.*;
import java.net.*;
import java.nio.file.Files;


public class HttpServer {

    private final static String sourceDir = "static";

    public static void main(String[] args) throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(8088)) {
            System.out.println("Server started at http://localhost:8088");
            while (true) {
                serveRequest(serverSocket.accept());
            }
        }
    }

    /**
     * Handle incoming GET request
     * @param socket client socket
     */
    private static void serveRequest(Socket socket) {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream out = socket.getOutputStream()) {

            while(!in.ready());

            String line = in.readLine();
            if (line != null && !line.isEmpty()) {
                String fileName = extractFileName(line);
                if (!fileName.isEmpty()) {
                    File file = getFileAtPath(fileName);
                    if (file != null) {
                        String fileExtension = extractFileExtension(fileName);
                        if (!fileExtension.isEmpty()) {
                            var content = Files.readAllBytes(file.toPath());
                            out.write(("HTTP/1.1 200 OK\r\n").getBytes());
                            out.write(("Content-Type: " + fileExtension + "; charset=UTF-8\r\n").getBytes());
                            out.write(("Content-Length: " + content.length + "\r\n").getBytes());
                            out.write(content);
                        } else {
                            out.write(("HTTP/1.1 400 Wrong file name format\r\n").getBytes());
                        }
                    } else {
                        out.write(("HTTP/1.1 404 File not found\r\n").getBytes());
                    }
                } else {
                    out.write(("HTTP/1.1 400 Bad request\r\n").getBytes());
                }
                out.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    /**
     * Extract filename from the URL.
     * @param line: in format 'GET /index.html HTTP/1.1'
     * @return the name of the file or an empty string
     */
    private static String extractFileName(String line) {
       String[] arr = line.split("\\s+");
       return arr.length >= 2 ? arr[1] : "";
    }

    /**
     * Extract file extension from the filename.
     * @param filename: the name of the file in format '/index.html'
     * @return the file extension, here 'html', or an empty string if there is no extension
     */
    private static String extractFileExtension(String filename) {
        int dotInx = filename.lastIndexOf(".");
        if (dotInx > 0 && dotInx < filename.length()-1) {
            return filename.substring(dotInx + 1);
        }
        return "";
    }

    /**
     * Check if file exists in the /resources/static folder
     * @param fileName the name of the file, for example /index.html
     * @return the file or null if the file was not found
     */
    private static File getFileAtPath(String fileName) {
        URL resourceUrl = HttpServer.class.getClassLoader().getResource(sourceDir + fileName);
        if (resourceUrl != null) {
            File file = new File(resourceUrl.getFile());
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

}