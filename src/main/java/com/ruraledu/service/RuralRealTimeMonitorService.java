package com.ruraledu.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Satisfies CO4: Socket Programming (ServerSocket, Socket, Multi-client handling).
 * This simulates a real-time monitor for rural education center heartbeats.
 */
@Service
public class RuralRealTimeMonitorService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RuralRealTimeMonitorService.class);
    private ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private boolean running = false;
    private static final int PORT = 9090;

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                running = true;
                logger.info("Rural Real-Time Monitor Socket Server started on port {}", PORT);
                
                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    executorService.submit(() -> handleClient(clientSocket));
                }
            } catch (Exception e) {
                if (running) {
                    logger.error("Error in Rural Real-Time Monitor Socket Server: {}", e.getMessage(), e);
                }
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                logger.debug("Received center heartbeat: {}", inputLine);
                out.println("ACK: Heartbeat received from " + inputLine);
            }
        } catch (Exception e) {
            logger.error("Error handling client connection: {}", e.getMessage(), e);
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                logger.error("Error closing client socket: {}", e.getMessage());
            }
        }
    }

    @PreDestroy
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            executorService.shutdownNow();
        } catch (Exception e) {
            logger.error("Error stopping Rural Real-Time Monitor Socket Server: {}", e.getMessage(), e);
        }
    }
}
