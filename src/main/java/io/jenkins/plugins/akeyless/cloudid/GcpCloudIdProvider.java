package io.jenkins.plugins.akeyless.cloudid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GcpCloudIdProvider implements CloudIdProvider {

    private static final String METADATA_URL =
            "http://metadata.google.internal/computeMetadata/v1/instance/service-accounts/default/identity?audience=akeyless.io&format=full";

    @Override
    public String getCloudId() throws Exception {
        String token = fetchIdentityToken();
        return Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    private String fetchIdentityToken() throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(METADATA_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Metadata-Flavor", "Google");
            conn.setConnectTimeout(3000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException(
                        "Failed to retrieve identity token from GCP metadata server. Response code: " + responseCode);
            }
            return Utils.readDataFromStream(conn.getInputStream()).toString();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignore) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
