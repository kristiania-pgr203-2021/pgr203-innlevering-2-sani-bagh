package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private final int statusCode;
    private final Map<String, String> headerFields = new HashMap<>();

    public HttpClient(String host, int port, String requestTarget) throws IOException {

        Socket socket = new Socket(host, port);

        //request to server
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Connection: close\r\n" + //sier til serveren at vi skal gjøre bare 1 requsest
                "\r\n";
        socket.getOutputStream().write(request.getBytes());

        //skal lese og holde første linje i response headers
        String[] statusLine = readLine(socket).split(" ");
        this.statusCode = Integer.parseInt(statusLine[1]);//tar imot value under index 1 i status

        String headerLine;
        while (!(headerLine = readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String headerField = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();
            headerFields.put(headerField, headerValue);
        }

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
        // System.out.println(client.getHeader("text/html; charset=utf-8"));


    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getHeader(String headerContent) {
        return headerFields.get(headerContent);
    }
}
