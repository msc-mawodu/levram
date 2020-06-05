package com.mawodu.levram;

import com.mawodu.levram.clients.Client;
import org.apache.tomcat.jni.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ClientHandler {

    Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private static final int DELAY = 10;
    private static final int TIMEOUT = 5000;

    Client client;

    public ClientHandler(Client client) {
        this.client = client;
    }

    public Optional<String> processRequest(String parameter) {
        Future<String> response = client.makeRequest(parameter);
        Optional<String> result = Optional.empty();
        try {
            long timeout = System.currentTimeMillis() + TIMEOUT;
            while(System.currentTimeMillis() < timeout) {
                if (response.isDone()) {
                    logger.info("Succesfully received data from {} @Thread: {}", client.name(), Thread.currentThread().getId());
                    return Optional.of(response.get());
                }
                Thread.sleep(DELAY);
            }

        } catch (InterruptedException | ExecutionException ex) {
            logger.error("FAILED to get data @Thread: {}", Thread.currentThread().getId());
            logger.error(String.valueOf(ex));
            Thread.currentThread().interrupt();
        }
        logger.warn("Failed to receive data from {} @Thread: {}", client.name(), Thread.currentThread().getId());
        return result;
    }

}
