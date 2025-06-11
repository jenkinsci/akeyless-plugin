package io.jenkins.plugins.akeyless.cloudid;

public class CloudIDRetriever {
    public static void main(String[] args) {
        CloudIdProvider cloudIdProvider = new AzureCloudIdProvider();
        try {
            String cloudId = cloudIdProvider.getCloudId();
            System.out.println("Cloud ID: " + cloudId);
        } catch (Exception e) {
            System.err.println("Error retrieving Cloud ID: " + e.getMessage());
        }
    }
}
