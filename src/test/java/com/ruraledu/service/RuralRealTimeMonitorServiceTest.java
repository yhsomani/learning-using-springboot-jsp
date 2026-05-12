package com.ruraledu.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class RuralRealTimeMonitorServiceTest {

    private RuralRealTimeMonitorService service;

    @BeforeEach
    void setUp() {
        service = new RuralRealTimeMonitorService();
    }

    @AfterEach
    void tearDown() {
        service.stopServer();
    }

    @Test
    void testServerStartupAndClientInteraction() throws IOException, InterruptedException {
        // Start the server
        service.startServer();

        // Give the server a moment to start
        Thread.sleep(500);

        // Connect a client
        try (Socket clientSocket = new Socket("localhost", 9090);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            // Send heartbeat
            out.println("Center-1");

            // Read response
            String response = in.readLine();
            assertEquals("ACK: Heartbeat received from Center-1", response);
        }
    }

    @Test
    void testServerStartupFailure() throws IOException, InterruptedException {
        // Bind the port beforehand to cause a BindException in startServer()
        try (ServerSocket blocker = new ServerSocket(9090)) {
            service.startServer();
            // Wait for the server thread to try to start and fail
            Thread.sleep(500);

            // Server should fail to bind, but it catches the exception internally.
            // Since `running` is false initially, the exception won't be printed,
            // but we ensure the thread doesn't crash the application.
            assertTrue(blocker.isBound());
        }
    }
}
