package no.kristiania.http;

import org.junit.Test;

import java.io.IOException;

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
}
