package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {


    private final HttpServer server = new HttpServer(0);//port nummer 0 finner automatisk port nummere

    public HttpServerTest() throws IOException {
    }

    @Test
    void shouldReturn404ForUnknownRequest() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldRespondWithRequestTargetIn404() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/non-existing");
        assertEquals("File not found: /non-existing", client.getMessageBody());
    }

    @Test
    void shouldRespondWith200ForKnownRequestTarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/products");

        assertAll(
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("text/html", client.getHeader("Content-Type")),
                () -> assertEquals("<li>Product:  </li>", client.getMessageBody())
        );
    }

    @Test
    void shouldHandleMoreThanOneRequest() throws IOException {

        assertEquals(200, new HttpClient("localhost", server.getPort(), "/api/products").getStatusCode());
        assertEquals(200, new HttpClient("localhost", server.getPort(), "/api/products").getStatusCode());
    }

    @Test
    void shouldEchoQueryParameter() throws IOException {
        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/products?productName= "
        );
        assertEquals("<li>Product: </li>", client.getMessageBody());
    }

    @Test
    void shouldReturnContentType() throws IOException {

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/products");

        assertAll(
                () -> assertEquals(200, client.getStatusCode()),
                () -> assertEquals("<li>Product:  </li>", client.getMessageBody())
        );
    }

    @Test
    void shouldServeFiles() throws IOException {
        //når vi kjørerte den testen ble opprettet fil exapmle-file.txt
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent = "A file created at " + LocalTime.now();
        Files.write(Path.of("target/test-classes/example-file.txt"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.txt");
        assertEquals(fileContent, client.getMessageBody());
        assertEquals("text/plain", client.getHeader("Content-Type"));

    }

    @Test
    void shouldUseFileExtensionForContentType() throws IOException {
        server.setRoot(Paths.get("target/test-classes"));

        String fileContent = "<p>Hello</p>";
        Files.write(Path.of("target/test-classes/example-file.html"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.html");
        assertEquals("text/html", client.getHeader("Content-Type"));
    }

    @Test
    void shouldReturnCategoryFromServer() throws IOException {
        server.setCategory(List.of("Hair", "Skin", "Vitamins"));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/categoryOptions");
        assertEquals(
                "<option value=1>Hair</option><option value=2>Skin</option><option value=3>Vitamins</option>",
                client.getMessageBody()
        );
    }



}
