package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer {

    private final ServerSocket serverSocket;
    private Path rootDirectory;
    private List<String> category = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    public HttpServer(int serverPort) throws IOException {
        //klassen brukes for å lage server
        serverSocket = new ServerSocket(serverPort);
        new Thread(this::handleClients).start();

    }

    private void handleClients() {
        try {
            while (true) {
                handleClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleClient() throws IOException {
        //setter sammen klient og server i en socket
        Socket client = serverSocket.accept();

        HttpMessage httpMessage = new HttpMessage(client);
        String[] requestLine = httpMessage.startLine.split(" ");
        String requestTarget = requestLine[1];

        // indexOf() vil returnere -1 da den ikke finner indeksen til "?"
        // fileTarget blir requestLine[1] fra posisjon 0 til "?"
        // query er resterende del av requestLine[1] fra "?"
        int questionPos = requestTarget.indexOf('?');
        String fileTarget;
        String query = null;
        if (questionPos != -1) {
            fileTarget = requestTarget.substring(0, questionPos);
            query = requestTarget.substring(questionPos + 1);
        } else {
            fileTarget = requestTarget;
        }

        if (fileTarget.equals("/hello")) {
            String yourName = "world";
            if (query != null) {

                Map<String, String> queryMap = parseRequestParameters(query);
                yourName = queryMap.get("name");
            }
            String responseText = "<p>Hello " + yourName + "</p>";


            writeOkResponse(client, responseText, "text/html");
        } else if (fileTarget.equals("/api/newProduct")) {
            Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
            Product product = new Product();
            product.setName(queryMap.get("name"));
            products.add(product);
            writeOkResponse(client, "it is done", "text/html");
        } else if (fileTarget.equals("/api/roleOptions")) {
            String responseText = "";

            int value = 1;
            for (String category : category) {
                responseText += "<option value=" + (value++) + ">" + category + "</option>";
            }


            writeOkResponse(client, responseText, "text/html");

        } else {
            if (rootDirectory != null && Files.exists(rootDirectory.resolve(fileTarget.substring(1)))) {
                //leser innhold av file
                String responseText = Files.readString(rootDirectory.resolve(fileTarget.substring(1)));

                String contentType = "text/plain";
                if (requestTarget.endsWith(".html")) {
                    contentType = "text/html";
                }
                writeOkResponse(client, responseText, contentType);
                return;
            }


            String responseText = "File not found: " + requestTarget;

            String response = "HTTP/1.1 404 Not found\r\n" +
                    "Content-Length: " + responseText.length() + "\r\n" +
                    "\r\n" +
                    responseText;
            client.getOutputStream().write(response.getBytes());
        }
    }

    private Map<String, String> parseRequestParameters(String query) {
        Map<String, String> queryMap = new HashMap<>();
        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf('=');
            String parameterName = queryParameter.substring(0, equalsPos);
            String parameterValue = queryParameter.substring(equalsPos + 1);
            queryMap.put(parameterName, parameterValue);
        }
        return queryMap;
    }

    private void writeOkResponse(Socket clientSocket, String responseText, String contentType) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + responseText.length() + "\r\n" +
                "Content-Type:" + contentType + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(1962);
        httpServer.setRoot(Paths.get("."));
    }


    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {

        this.rootDirectory = rootDirectory;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<Product> getProducts() {
        return products;
    }


}




