package no.kristiania.http;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class HttpServer {

    private final ServerSocket serverSocket;
    private Path rootDirectory;

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);


    }

    private void handleClients() {


    }


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
}


