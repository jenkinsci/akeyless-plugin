package io.jenkins.plugins.akeyless.cloudid;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;

public class AzureCloudIdProvider implements CloudIdProvider {

    private static final String METADATA_URL = "http://169.254.169.254/metadata/identity/oauth2/token";
    private static final String API_VERSION = "2018-02-01";
    private static final String RESOURCE = "https://management.azure.com/";

    public String getCloudId(String objectId) throws Exception {
        String query = String.format("api-version=%s&resource=%s", API_VERSION, URLEncoder.encode(RESOURCE, "UTF-8"));
        if (objectId != null && !objectId.isEmpty()) {
            query += "&object_id=" + URLEncoder.encode(objectId, "UTF-8");
        }

        URL url = new URL(METADATA_URL + "?" + query);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Metadata", "true");
        conn.setRequestProperty("User-Agent", "AKEYLESS");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new IOException("Failed to get token. Status: " + status);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> json = mapper.readValue(response.toString(), Map.class);
            String accessToken = (String) json.get("access_token");

            if (accessToken == null || accessToken.isEmpty()) {
                throw new IOException("Access token not found in response");
            }

            return Base64.getEncoder().encodeToString(accessToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    @Override
    public String getCloudId() throws Exception {
        return getCloudId("");
    }
}
