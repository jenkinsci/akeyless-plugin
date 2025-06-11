package io.jenkins.plugins.akeyless.cloudid;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AwsCredentialResolver {

    public static class AwsCredentials {
        public final String accessKeyId;
        public final String secretAccessKey;
        public final String sessionToken;

        public AwsCredentials(String accessKeyId, String secretAccessKey, String sessionToken) {
            this.accessKeyId = accessKeyId;
            this.secretAccessKey = secretAccessKey;
            this.sessionToken = sessionToken;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("accessKeyId=").append(accessKeyId);
            sb.append(", secretAccessKey=").append(secretAccessKey);
            sb.append(", sessionToken=").append(sessionToken);
            return sb.toString();
        }
    }

    public static AwsCredentials resolve() throws Exception {
        // 1. Environment Variables
        // Map env = System.getenv();
        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String sessionToken = System.getenv("AWS_SESSION_TOKEN"); // optional

        if (accessKey != null && secretKey != null) {
            return new AwsCredentials(accessKey, secretKey, sessionToken);
        }

        // 2. ECS Container Credentials (via AWS_CONTAINER_CREDENTIALS_RELATIVE_URI)
        String relativeUri = System.getenv("AWS_CONTAINER_CREDENTIALS_RELATIVE_URI");
        if (relativeUri != null) {
            String ecsUrl = "http://169.254.170.2" + relativeUri;
            return fetchCredentialsFromMetadataService(ecsUrl);
        }

        // 3. EC2 Instance Metadata (IMDSv2)
        String token = fetchImdsV2Token();
        String roleName = httpGet("http://169.254.169.254/latest/meta-data/iam/security-credentials/", token);
        String credsJson =
                httpGet("http://169.254.169.254/latest/meta-data/iam/security-credentials/" + roleName, token);

        // Parse JSON (using manual parsing or Jackson if needed)
        Map<String, Object> json = new com.fasterxml.jackson.databind.ObjectMapper().readValue(credsJson, Map.class);

        return new AwsCredentials(
                (String) json.get("AccessKeyId"), (String) json.get("SecretAccessKey"), (String) json.get("Token"));
    }

    private static String fetchImdsV2Token() throws Exception {
        URL url = new URL("http://169.254.169.254/latest/api/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("X-aws-ec2-metadata-token-ttl-seconds", "21600");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed to fetch IMDSv2 token");
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String token = br.lines().collect(java.util.stream.Collectors.joining("\n"));
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read IMDSv2 token response", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            conn.disconnect();
        }
    }

    private static String httpGet(String url, String imdsToken) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        if (imdsToken != null) {
            conn.setRequestProperty("X-aws-ec2-metadata-token", imdsToken);
        }

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed to fetch metadata from " + url);
        }

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder out = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            reader.close();
        }
        return out.toString();
    }

    private static AwsCredentials fetchCredentialsFromMetadataService(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed to fetch ECS credentials from " + urlStr);
        }

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            reader.close();
        }

        Map<String, Object> json = new ObjectMapper().readValue(response.toString(), Map.class);

        return new AwsCredentials(
                (String) json.get("AccessKeyId"), (String) json.get("SecretAccessKey"), (String) json.get("Token"));
    }
}
