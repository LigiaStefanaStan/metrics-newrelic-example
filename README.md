# metrics-newrelic-example
Test case for DropWizard metrics Reporter https://github.com/newrelic/dropwizard-metrics-newrelic

If you run the example as it is, then the application will hang at shut down. If you
uncomment the lines in Application.java which shut down the TelemetryClient
thread pool, then it will exit cleanly.