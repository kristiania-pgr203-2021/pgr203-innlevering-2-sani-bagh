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
    private String messageBody;

    public HttpClient(String host, int port, String requestTarget) throws IOException {
        //oppretter connection to server
        Socket socket = new Socket(host, port);

        //request to server
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Connection: close\r\n" + //sier til serveren at vi skal gjøre bare 1 requsest
                "\r\n";
        //getOutputStream er Socket sin property som clienten har
        socket.getOutputStream().write(request.getBytes());

        //skal lese tilbake første linje i response headers
        String[] statusLine = readLine(socket).split(" ");//splitter opp status linjen
        this.statusCode = Integer.parseInt(statusLine[1]);//tar imot value under index 1 i status

        String headerLine;
        //leser header linjene frem til det er blank
        while (!(headerLine = readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String headerField = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();
            headerFields.put(headerField, headerValue);
        }

        this.messageBody = readBytes(socket, getContentLength());
    }

    private String readBytes(Socket socket, int contentLength) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            buffer.append((char)socket.getInputStream().read());
        }
        return buffer.toString();
    }

    public static String readLine(Socket socket) throws IOException {
        //skal bygge opp string gradvis
        StringBuilder result = new StringBuilder();

        int c;
        //while det kommer på slutten av linje '\r'
        while ((c = socket.getInputStream().read()) != '\r') {
            result.append((char) c);
        }

        //leser ny linje
        int expectedNewline = socket.getInputStream().read();
        assert expectedNewline == '\n';
        return result.toString();
    }

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        System.out.println(client.getStatusCode());
        //System.out.println(client.getHeader("Content-Type"));


    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName);
    }

    public int getContentLength() {
        int result = Integer.parseInt(getHeader("Content-Length"));
        return result;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
