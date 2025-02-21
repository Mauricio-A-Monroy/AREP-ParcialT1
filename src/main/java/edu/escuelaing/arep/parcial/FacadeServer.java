package edu.escuelaing.arep.parcial;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FacadeServer
{
    public static void main( String[] args ) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean isRunning = true;
        while (isRunning) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean firstLine = true;
            String path = "";
            while ((inputLine = in.readLine()) != null) {
                if(firstLine){
                    path = inputLine.split(" ")[1];
                    firstLine = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            System.out.println("Path: " + path);

            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n" + getDefaultResponse();
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String getDefaultResponse() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("src/main/resources/static/index.html"));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();
        return response.toString();
    }
}
