package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    @Test
    void shouldReturn404ForUnknownRequest() throws IOException {
        HttpServer server = new HttpServer(10001);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldRespondWithRequestTargetIn404() throws IOException {
        HttpServer server = new HttpServer(10004);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals("File not found: /non-existing", client.getMessageBody());
    }

    @Test
    void shouldRespondWith200ForKnownRequestTarget() throws IOException {
        HttpServer server = new HttpServer(10005);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello");

        assertAll(
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("text/html", client.getHeader("Content-Type")),
                () -> assertEquals("<p>Hello world</p>", client.getMessageBody())
        );
    }

    @Test
    void shouldHandleMoreThanOneRequest() throws IOException {
        HttpServer server = new HttpServer(0);

        assertEquals(200, new HttpClient("localhost", server.getPort(), "/hello").getStatusCode());
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/hello").getStatusCode());
    }

    @Test
    void shouldServeFiles() throws IOException {
        //når vi kjørerte den testen ble opprettet fil exapmle-file.txt
        HttpServer server = new HttpServer(0);
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent = "A file created at " + LocalTime.now();
        Files.write(Path.of("target/test-classes/example-file.txt"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.txt");
        assertEquals(fileContent, client.getMessageBody());
        assertEquals("text/plain", client.getHeader("Content-Type"));

    }
}
