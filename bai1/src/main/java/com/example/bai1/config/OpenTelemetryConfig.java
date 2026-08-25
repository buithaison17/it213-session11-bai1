package com.example.bai1.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenTelemetryConfig {

    @Value("${management.otlp.tracing.endpoint:http://localhost:3000/api/public/otel/v1/traces}")
    private String endpoint;

    @Value("${management.otlp.tracing.headers.Authorization:Basic cGtfbGZfMTIzNDU6c2tfbGZfNjc4OTA=}")
    private String authHeader;

    @Bean
    public OtlpHttpSpanExporter otlpHttpSpanExporter() {
        return OtlpHttpSpanExporter.builder()
                .setEndpoint(endpoint)
                .addHeader("Authorization", authHeader)
                .setTimeout(Duration.ofSeconds(2))
                .build();
    }

    @Bean
    public BatchSpanProcessor batchSpanProcessor(OtlpHttpSpanExporter exporter) {
        return BatchSpanProcessor.builder(exporter)
                .setMaxQueueSize(2048)              // Giới hạn hàng đợi RAM phòng thủ OOM
                .setScheduleDelay(Duration.ofSeconds(5)) // Batch định kỳ 5s
                .setMaxExportBatchSize(512)         // Tối đa 512 spans/lần gửi
                .setExporterTimeout(Duration.ofSeconds(2)) // Non-blocking timeout
                .build();
    }

    @Bean
    public OpenTelemetry openTelemetry(BatchSpanProcessor batchSpanProcessor) {
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(batchSpanProcessor)
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.example.bai1", "1.0.0");
    }
}