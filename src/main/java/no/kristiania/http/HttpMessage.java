package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    public String startLine;
    public static final Map<String, String> headerFields = new HashMap<>();
    public static String messageBody;

    public HttpMessage(Socket socket) throws IOException {
        startLine = HttpMessage.readLine(socket);
        readHeaders(socket);
        if (headerFields.containsKey("Content-Length")) {
            messageBody = HttpMessage.readBytes(socket, getContentLength());
        }
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

    static String readBytes(Socket socket, int contentLength) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            buffer.append((char) socket.getInputStream().read());
        }
        return buffer.toString();
    }

    private void readHeaders(Socket socket) throws IOException {
        String headerLine;
        //leser header linjene frem til det er blank
        while (!(headerLine = HttpMessage.readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String headerField = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();
            headerFields.put(headerField, headerValue);
        }
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName);
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }
}
