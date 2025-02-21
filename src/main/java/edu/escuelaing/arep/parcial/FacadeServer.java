package edu.escuelaing.arep.parcial;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class FacadeServer {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL ="http://localhost:8080";
    public static void main( String[] args ) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
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

            if (path.startsWith("/cliente")) {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n" + getDefaultResponse();
            }
            else if(path.startsWith("/consulta")){
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n" + connectToBackEnd(path);
            }
            else{
                outputLine = "HTTP/1.1 404 NOT FOUND\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n";
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String connectToBackEnd(String path) throws IOException {
        URL obj = new URL(GET_URL + path.replace("consulta", "compreflex"));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            return "HTTP/1.1 404 NOT FOUND\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n";
        }

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
