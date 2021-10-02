package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {

    private final ServerSocket serverSocket;
    private Path rootDirectory;

    public HttpServer(int serverPort) throws IOException {
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

            String[] requestLine = HttpMessage.readLine(client).split(" ");
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
                    yourName = query.split("=")[1];
                }
                String responseText = "<p>Hello " + yourName + "</p>";

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

    private void writeOkResponse(Socket clientSocket, String responseText, String contentType) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + responseText.length() + "\r\n" +
                "Content-Type:" + contentType + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {

        this.rootDirectory = rootDirectory;
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(1962);
        httpServer.setRoot(Paths.get("."));
    }
}

/*
    public static void main(String[] args) throws IOException {

        //klassen brukes for å lage server
        ServerSocket serverSocket = new ServerSocket(8080);

        //åpner en port som klienten kan bruke
        Socket client = serverSocket.accept();

        String requestLine = HttpClient.readLine(client);

        //sender data til browser localhost:portnummer
        //åpner input stream for å se hva klienten sender til oss
        System.out.println(requestLine);

        String headerLine;
        while (!(headerLine = HttpClient.readLine(client)).isBlank()) {
            System.out.println(headerLine);


            String messageBody = "Hello world";

            String responseMessage = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + messageBody.length() + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    messageBody;
            client.getOutputStream().write(responseMessage.getBytes());
        }
    }

 */



