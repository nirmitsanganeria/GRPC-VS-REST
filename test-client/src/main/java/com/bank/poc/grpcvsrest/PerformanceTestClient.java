// package com.bank.poc.grpcvsrest;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;
// import org.springframework.web.reactive.function.client.WebClient;

// import com.bank.poc.grpcvsrest.grpc.TradeRequest;
// import com.bank.poc.grpcvsrest.grpc.TradeServiceGrpc;

// import net.devh.boot.grpc.client.inject.GrpcClient;

// /**
//  * This class will automatically run when the application starts.
//  * It acts as a client to our own server, calling both endpoints
//  * and timing their performance.
//  */
// @Component
// public class PerformanceTestClient implements CommandLineRunner {

//     // The @GrpcClient annotation magically injects a ready-to-use gRPC client stub.
//     @GrpcClient("local-grpc-server")
//     private TradeServiceGrpc.TradeServiceBlockingStub grpcStub;

//     // We create a WebClient to make REST calls.
//     private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();

//     @Override
//     public void run(String... args) throws Exception {
//         String clientId = "MAJOR_BANK_CLIENT";
//         int iterations = 1000;

//         System.out.println("--- Starting Performance Test ---");

//         // --- Warm-up Phase ---
//         // This is important to allow the JVM to optimize code (JIT compilation)
//         System.out.println("Warming up for 50 iterations...");
//         for (int i = 0; i < 50; i++) {
//             callRestEndpoint(clientId);
//             callGrpcEndpoint(clientId);
//         }

//         System.out.println("Warm-up complete. Starting measurement.");
//         Thread.sleep(2000); // Pause briefly before starting the real test

// // --- Test REST Performance ---
//         long restStartTime = System.nanoTime();
//         for (int i = 0; i < iterations; i++) {
//             callRestEndpoint(clientId);
//         }
//         long restEndTime = System.nanoTime();
//         long restTotalTime = (restEndTime - restStartTime) / 1_000_000; // ms
//         double restSeconds = restTotalTime / 1000.0;
//         double restThroughput = iterations / restSeconds;
//         System.out.printf("✅ REST: %d iterations took %d ms. Average: %.4f ms/call | Throughput: %.2f req/sec%n",
//                 iterations, restTotalTime, (double) restTotalTime / iterations, restThroughput);


//         // --- Test gRPC Performance ---
//         long grpcStartTime = System.nanoTime();
//         for (int i = 0; i < iterations; i++) {
//             callGrpcEndpoint(clientId);
//         }
//         long grpcEndTime = System.nanoTime();
//         long grpcTotalTime = (grpcEndTime - grpcStartTime) / 1_000_000; // ms
//         double grpcSeconds = grpcTotalTime / 1000.0;
//         double grpcThroughput = iterations / grpcSeconds;
//         System.out.printf("🚀 gRPC: %d iterations took %d ms. Average: %.4f ms/call | Throughput: %.2f req/sec%n",
//                 iterations, grpcTotalTime, (double) grpcTotalTime / iterations, grpcThroughput);

//         double difference1 = (double) (restTotalTime - grpcTotalTime) / restTotalTime * 100;
//         System.out.printf("--- gRPC was ~%.2f%% faster ---%n", difference1);
//         double difference2 = ((grpcThroughput - restThroughput) / restThroughput) * 100;
//         System.out.printf("--- gRPC had ~%.2f%% higher throughput ---%n", difference2);
//     }

//     private void callRestEndpoint(String clientId) {
//         // .blockLast() waits for the API call to complete.
//         webClient.get()
//                 .uri("/api/rest/clients/{clientId}/trades", clientId)
//                 .retrieve()
//                 .bodyToFlux(TradePojo.class)
//                 .blockLast();
//     }

//     private void callGrpcEndpoint(String clientId) {
//         TradeRequest request = TradeRequest.newBuilder().setClientId(clientId).build();
//         // This is a direct, blocking method call on the stub.
//         grpcStub.getTradesForClient(request);
//     }
// }


package com.bank.poc.grpcvsrest;

// Add these required imports from the 'common' module
import javax.net.ssl.SSLException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.bank.poc.grpcvsrest.grpc.TradeRequest;
import com.bank.poc.grpcvsrest.grpc.TradeResponse;
import com.bank.poc.grpcvsrest.grpc.TradeServiceGrpc;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import net.devh.boot.grpc.client.inject.GrpcClient;
import reactor.netty.http.client.HttpClient;

@Component
public class PerformanceTestClient implements CommandLineRunner {

    @GrpcClient("local-grpc-server")
    private TradeServiceGrpc.TradeServiceBlockingStub grpcStub;

    private final WebClient restWebClient;
    private final WebClient protoWebClient;
    private final ApplicationContext context;

    public PerformanceTestClient(ApplicationContext context) throws SSLException {
        this.context = context;
        // Standard client for REST/JSON over HTTP
        this.restWebClient = WebClient.builder().baseUrl("http://localhost:8080").build();
        
        // Special client that trusts our self-signed cert for REST/Protobuf over HTTPS/HTTP2
        HttpClient http2Client = HttpClient.create()
                .secure(t -> {
                    try {
                        t.sslContext(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build());
                    } catch (SSLException e) {
                        throw new RuntimeException(e);
                    }
                });
        this.protoWebClient = WebClient.builder()
                .baseUrl("https://localhost:8443")
                .clientConnector(new ReactorClientHttpConnector(http2Client))
                .build();
    }


    @Override
    public void run(String... args) throws Exception {
        String clientId = "MAJOR_BANK_CLIENT";
        int iterations = 1000;

        System.out.println("--- Starting Performance Test ---");
        System.out.println("Waiting for servers to start...");
        Thread.sleep(5000); // Wait for servers to be ready
        System.out.println("Warming up...");
        for (int i = 0; i < 50; i++) {
            callRestEndpoint(clientId);
            callProtoRestEndpoint(clientId);
            callGrpcEndpoint(clientId);
        }
        System.out.println("Warm-up complete. Starting measurement.");
        Thread.sleep(2000);



// --- Test REST/JSON Performance ---
        long restStartTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) callRestEndpoint(clientId);
        long restTotalTime = (System.nanoTime() - restStartTime) / 1_000_000;
        double restThroughput = iterations / (restTotalTime / 1000.0);

        // --- Test REST/Protobuf Performance ---
        long protoRestStartTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) callProtoRestEndpoint(clientId);
        long protoRestTotalTime = (System.nanoTime() - protoRestStartTime) / 1_000_000;
        double protoRestThroughput = iterations / (protoRestTotalTime / 1000.0);

        // --- Test gRPC Performance ---
        long grpcStartTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) callGrpcEndpoint(clientId);
        long grpcTotalTime = (System.nanoTime() - grpcStartTime) / 1_000_000;
        double grpcThroughput = iterations / (grpcTotalTime / 1000.0);

        // --- Calculate Percentage Improvements vs Baseline (REST/JSON) ---
        double protoTimeImprovement = ((restTotalTime - protoRestTotalTime) / (double) restTotalTime) * 100;
        double protoThroughputImprovement = ((protoRestThroughput - restThroughput) / restThroughput) * 100;
        double grpcTimeImprovement = ((restTotalTime - grpcTotalTime) / (double) restTotalTime) * 100;
        double grpcThroughputImprovement = ((grpcThroughput - restThroughput) / restThroughput) * 100;

        // --- Print Formatted Results Table ---
        System.out.println("--- Final Results ---");
        String format = "%-25s | %12s | %16s | %18s | %22s%n";
        System.out.println("-".repeat(110));
        System.out.printf(format, "API Style", "Total Time", "Time Improvement", "Throughput", "Throughput Improvement");
        System.out.println("-".repeat(110));

        System.out.printf("%-25s | %,12d ms | %16s | %,18.2f req/sec | %22s%n",
                "✅ REST (JSON/HTTP1)", restTotalTime, "Baseline", restThroughput, "Baseline");

        System.out.printf("%-25s | %,12d ms | %15.1f%% | %,18.2f req/sec | %21.1f%%%n",
                "✅ REST (Proto/HTTP2)", protoRestTotalTime, -protoTimeImprovement, protoRestThroughput, protoThroughputImprovement);
        
        System.out.printf("%-25s | %,12d ms | %15.1f%% | %,18.2f req/sec | %21.1f%%%n",
                "🚀 gRPC (Proto/HTTP2)", grpcTotalTime, -grpcTimeImprovement, grpcThroughput, grpcThroughputImprovement);
        System.out.println("-".repeat(110));

        System.out.println("--- Performance Test Complete. Shutting down client. ---");
        SpringApplication.exit(context, () -> 0);



    }

    private void callRestEndpoint(String clientId) {
        restWebClient.get()
                .uri("/api/rest/clients/{clientId}/trades", clientId)
                .retrieve()
                .bodyToFlux(TradePojo.class)
                .blockLast();
    }

    private void callProtoRestEndpoint(String clientId) {
            protoWebClient.get()
                    .uri("/api/proto/clients/{clientId}/trades", clientId)
                    .accept(MediaType.valueOf("application/x-protobuf"))
                    .retrieve()
                    .bodyToMono(TradeResponse.class)
                    .block();
    }
    
    private void callGrpcEndpoint(String clientId) {
        TradeRequest request = TradeRequest.newBuilder().setClientId(clientId).build();
        grpcStub.getTradesForClient(request);
    }
}