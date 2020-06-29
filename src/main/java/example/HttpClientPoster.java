package example;

import com.newrelic.telemetry.http.HttpPoster;
import com.newrelic.telemetry.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpClientPoster implements HttpPoster {

    @Override
    public HttpResponse post(URL url, Map<String, String> headers, byte[] body, String mediaType) throws IOException {
        final Header[] convertedHeaders = headers.entrySet().stream()
                .map(entry -> new BasicHeader(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()).toArray(new Header[0]);

        final org.apache.http.HttpResponse response = Request.Post(url.toString())
                .body(new ByteArrayEntity(body))
                .setHeaders(convertedHeaders)
                .addHeader(HTTP.CONTENT_TYPE, mediaType)
                .execute()
                .returnResponse();

        return parseHttpClientResponse(response);
    }

    private HttpResponse parseHttpClientResponse(org.apache.http.HttpResponse response) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        final String body = reader.lines().collect(Collectors.joining());
        final int statusCode = response.getStatusLine().getStatusCode();
        final String message = response.getEntity().getContent().toString();

        final Map<String, List<String>> convertedHeaders =
                Arrays.stream(response.getAllHeaders())
                        .collect(Collectors.groupingBy(
                                Header::getName,
                                Collectors.mapping(Header::getValue, Collectors.toList())
                        ));

        return new HttpResponse(body, statusCode, message, convertedHeaders);
    }
}
