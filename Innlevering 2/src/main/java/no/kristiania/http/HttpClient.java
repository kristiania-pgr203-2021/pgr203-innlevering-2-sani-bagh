package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.sql.PreparedStatement;

public class HttpClient {
    private final int statusCode;

    public HttpClient(String host, int port, String requestTarget) throws IOException {

        Socket socket = new Socket(host, port);

        //request to server
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Connection: close\r\n"  + //sier til serveren at vi skal gjøre bare 1 requsest
                "\r\n";
        socket.getOutputStream().write(request.getBytes());

        //skal lese og holde første linje i response headers
        String[] statusLine = readLine(socket).split(" ");
        this.statusCode = Integer.parseInt(statusLine[1]);//tar imot value under index 1 i status


    }

    private String readLine(Socket socket) throws IOException {
        //skal bygge opp string gradvis
        StringBuilder result = new StringBuilder();

        //while det kommer på slutten av linje '\r'
        int c;
        while ((c = socket.getInputStream().read()) != '\r') {
            result.append((char) c);
        }

        return result.toString();
    }

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        System.out.println(client.getStatusCode());
    }

    public int getStatusCode() {
        return statusCode;
    }
}
