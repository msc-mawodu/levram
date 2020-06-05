package com.mawodu.levram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.DigestUtils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Future;

// todo: implements Callable ???
public class DataProvider {

    Logger logger = LoggerFactory.getLogger(DataProvider.class);

    private static final String DOMAIN = "http://gateway.marvel.com";
    private static final String CHARACTERS_ENDPOINT = "/v1/public/characters";

    @Value("${marvel.pub}")
    private String API_PUB_KEY;

    @Value("${marvel.pk}")
    private String API_PRIVATE_KEY;

    public Future<String> call(int offset)  {

        if (API_PRIVATE_KEY == null || API_PUB_KEY == null) {
            logger.info(String.format("Attempting to pull data from marvel api @Thread: %s", Thread.currentThread().getId()));
            return null;
        }

        logger.info(String.format("Attempting to pull data from marvel api @Thread: %s", Thread.currentThread().getId()));
        int status = 0;
        try {

            String timestamp = String.valueOf(System.currentTimeMillis());
            String hash = DigestUtils.md5DigestAsHex(String.format("%s%s%s",timestamp, API_PRIVATE_KEY, API_PUB_KEY).getBytes());

            String urlStr = new StringBuilder(String.format("%s%s", DOMAIN, CHARACTERS_ENDPOINT))
                    .append("?")
                    .append(String.format("ts=%s&", timestamp))
                    .append(String.format("apikey=%s&", API_PUB_KEY))
                    .append(String.format("hash=%s&",hash))
                    .append(String.format("offset=%s",offset))
                    .toString();

            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setInstanceFollowRedirects(true);

            status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            logger.info(String.format("Succesfully pulled data from marvel api @Thread: %s", Thread.currentThread().getId()));
            return new AsyncResult<String>(content.toString());

        } catch (IOException e) {
            logger.error(String.format("Failed to pull data from marvel api @Thread: %s", Thread.currentThread().getId()));
            return new AsyncResult<String>(String.format("ERROR %s", status));
        }
    }

}
