package edu.escuelaing.arep.parcial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class BackEndServer {
    public static void main( String[] args ) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8080.");
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
                if (firstLine) {
                    path = inputLine.split(" ")[1];
                    firstLine = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            System.out.println("Path: " + path);

            if (path.startsWith("/compreflex")) {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n" + getResponse(path);
            } else {
                outputLine = "HTTP/1.1 404 NOT FOUND\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"+ "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<head>\n"
                        + "<meta charset=\"UTF-8\">\n"
                        + "<title>Title of the document</title>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "<h1>NOT FOUND</h1>\n"
                        + "</body>\n"
                        + "</html>\n";;
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String getResponse(String path) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //compreflex?comando=binaryInvoke(java.lang.Math, max, double, 4.5, double, -3.7)
        String resource = path.split("=")[1];
        System.out.println("resource = " + resource);

        if (resource.startsWith("Class")) {
            String className = resource.split("\\(")[1].split("\\)")[0];
            Class c = Class.forName(className);
            return Arrays.toString(c.getDeclaredFields()) + Arrays.toString(c.getDeclaredMethods());

        } else if(resource.startsWith("invoke")){
            String className = resource.split("\\(")[1].split("\\)")[0].split(",")[0];
            String methodName = resource.split("\\(")[1].split("\\)")[0].split(",")[1];

            Class c = Class.forName(className);
            Method m = c.getDeclaredMethod(methodName);
            return m.invoke(null).toString();

        } else if(resource.startsWith("unaryInvoke")){
            String className = resource.split("\\(")[1].split("\\)")[0].split(",")[0];
            String methodName = resource.split("\\(")[1].split("\\)")[0].split(",")[1];
            String paramtype = resource.split("\\(")[1].split("\\)")[0].split(",")[2];
            String paramvalue = resource.split("\\(")[1].split("\\)")[0].split(",")[3];

            Class c = Class.forName(className);
            Method m = c.getDeclaredMethod(methodName, getClass(paramtype));
            return m.invoke(null, getCastedValue(paramtype,paramvalue)).toString();

        } else if(resource.startsWith("binaryInvoke")){
            String className = resource.split("\\(")[1].split("\\)")[0].split(",")[0];
            String methodName = resource.split("\\(")[1].split("\\)")[0].split(",")[1];
            String paramtype1 = resource.split("\\(")[1].split("\\)")[0].split(",")[2];
            String paramvalue1 = resource.split("\\(")[1].split("\\)")[0].split(",")[3];
            String paramtype2 = resource.split("\\(")[1].split("\\)")[0].split(",")[4];
            String paramvalue2 = resource.split("\\(")[1].split("\\)")[0].split(",")[5];

            Class c = Class.forName(className);
            Method m = c.getDeclaredMethod(methodName, getClass(paramtype1), getClass(paramtype2));
            return m.invoke(null, getCastedValue(paramtype1,paramvalue1), getCastedValue(paramtype2,paramvalue2)).toString();

        } else{
            return "HTTP/1.1 404 NOT FOUND\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n";
        }
    }

    private static Object getCastedValue(String paramtype, String paramvalue) {
        if (paramtype.equals("int")){
            return Integer.valueOf(paramvalue);
        } else if (paramtype.equals("double")){
            return Double.valueOf(paramvalue);
        } else{
            if (paramvalue.startsWith("%22")){
                paramvalue = paramvalue.replace("%22", "");
            }
            return paramvalue;
        }
    }

    private static Class<?> getClass(String paramtype) {
        if (paramtype.equals("int")){
            return int.class;
        } else if (paramtype.equals("double")){
            return double.class;
        } else{
            return String.class;
        }
    }
}
