package no.kristiania.http;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class HttpClientTest {
    @Test
    public void shouldReturnStatusCode() throws IOException {
        assertEquals(200,
                new HttpClient("httpbin.org", 80, "/html")
                        .getStatusCode());
        assertEquals(404,
                new HttpClient("httpbin.org", 80, "/non-existing")
                        .getStatusCode());

    }

    @Test
    public void shouldReturnHeaders() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type"));
    }

    @Test
    public void shouldReadContentLength() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals(3741, client.getContentLength());
    }

    @Test
    public void shouldReadMessageBody() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertTrue("Expected HTML: " + client.getMessageBody(), client.getMessageBody().startsWith("<!DOCTYPE html"));
    }

    @Test
    void shouldServeFiles() throws IOException {
        HttpServer server = new HttpServer(0);
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent = "A file created at " + LocalTime.now();
        Files.write(Path.of("target/test-classes/example-file.txt"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.txt");
        Assertions.assertEquals(fileContent, client.getMessageBody());
        Assertions.assertEquals("text/plain", client.getHeader("Content-Type"));

    }
}
