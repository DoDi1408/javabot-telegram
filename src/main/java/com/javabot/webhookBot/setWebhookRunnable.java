package com.javabot.webhookBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class setWebhookRunnable implements Runnable {

    @Override
    public void run() {
        try {
            String apiUrl = "https://api.telegram.org/bot" + System.getenv("BOT_CREDENTIALS_PSW") +"/setWebhook?url=https://api.romongo.uk/bot/extreme-bot-endpoint";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("RESPONSE " + response.toString());

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
