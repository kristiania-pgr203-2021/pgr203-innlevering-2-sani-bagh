package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {
    private final int statusCode;
    private HttpMessage httpMessage;


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

        httpMessage = new HttpMessage(socket);

        //skal lese tilbake første linje i response headers
        String[] statusLine = httpMessage.startLine.split(" ");//splitter opp status linjen
        this.statusCode = Integer.parseInt(statusLine[1]);//tar imot value under index 1 i status

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
        return httpMessage.headerFields.get(headerName);
    }


    public String getMessageBody() {
        return httpMessage.messageBody;
    }
}
