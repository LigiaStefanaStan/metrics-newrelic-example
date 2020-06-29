package example;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.newrelic.NewRelicReporter;
import com.newrelic.telemetry.TelemetryClient;
import com.newrelic.telemetry.http.HttpPoster;
import com.newrelic.telemetry.metrics.MetricBatchSender;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public class Application {

    private static final MetricRegistry metricRegistry = new MetricRegistry();

    private static final HttpPoster httpPoster = new HttpClientPoster();

    private static final MetricBatchSender metricBatchSender =
            MetricBatchSender
                    .builder()
                    .apiKey("my-api-key")
                    .httpPoster(httpPoster)
                    .build();

    private static final NewRelicReporter newRelicReporter =
            NewRelicReporter
                    .build(metricRegistry, metricBatchSender)
                    .build();

    private static final ConsoleReporter consoleReporter =
            ConsoleReporter
                    .forRegistry(metricRegistry)
                    .build();

    public static void main(String[] args) throws Exception {
        newRelicReporter.start(30, TimeUnit.SECONDS);
        consoleReporter.start(30, TimeUnit.SECONDS);

        metricRegistry.counter("my-counter").inc();
        newRelicReporter.report();
        consoleReporter.report();

        newRelicReporter.stop();
        consoleReporter.stop();

        // Uncommenting this code will close the TelemetryClient and let the application shut down

        // final Field senderField = newRelicReporter.getClass().getDeclaredField("sender");
        // senderField.setAccessible(true);
        // final TelemetryClient telemetryClient = (TelemetryClient) senderField.get(newRelicReporter);
        // telemetryClient.shutdown();

        System.out.println("Done");
    }
}
