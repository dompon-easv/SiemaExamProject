package dk.siema.siemaexamproject.dal.api;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TiffApiClient {

    private static final String BASE_URL =
            "https://studentiffapi-production.up.railway.app";

    private final HttpClient client = HttpClient.newHttpClient();

    public InputStream fetchRandomFile() throws Exception {
        URL url = new URL(BASE_URL + "/getRandomFile");
        return url.openStream();
    }

    public InputStream fetchAllFiles() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/getAllFiles"))
                .GET()
                .build();

        HttpResponse<InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed: " + response.statusCode());
        }

        return response.body();
    }
}