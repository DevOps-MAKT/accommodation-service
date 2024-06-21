package uns.ac.rs;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@ApplicationScoped
public class MicroserviceCommunicator {
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceCommunicator.class);

    public GeneralResponse processResponse(String apiUrl, String requestMethod, String authorizationHeader) {
        return this.sendRequest(apiUrl, requestMethod, authorizationHeader);
    }
    private GeneralResponse sendRequest(String apiUrl, String requestMethod, String authorizationHeader) {
        StringBuilder response = new StringBuilder();
        int responseCode = 500;
        try {

            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(requestMethod);

            connection.setRequestProperty("Content-Type", "application/json");
            if (!authorizationHeader.isEmpty()) connection.setRequestProperty("Authorization", authorizationHeader);

            responseCode = connection.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            connection.disconnect();

        } catch (IOException e) {
            logger.error("Service integration failed: {}", e.getMessage());
            return new GeneralResponse<>("", String.valueOf(responseCode));
        }
        return new GeneralResponse<>(response.toString());
    }

}