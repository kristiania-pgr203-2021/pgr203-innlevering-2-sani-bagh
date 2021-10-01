package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {


    public static void main(String[] args) throws IOException {
        //klassen brukes for å lage server
        ServerSocket serverSocket = new ServerSocket(1962);

        //åpner en port som klienten kan bruke
        Socket client = serverSocket.accept();

        //sender data til browser localhost:portnummer
        //åpner input stream for å se hva klienten sender til oss
        int c;
        while ((c = client.getInputStream().read()) != 1) {
            System.out.print((char) c);
        }
    }
}

