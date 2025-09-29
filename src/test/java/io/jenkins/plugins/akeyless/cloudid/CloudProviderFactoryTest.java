package io.jenkins.plugins.akeyless.cloudid;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CloudProviderFactoryTest {

    @Test
    void returnsAwsProvider() {
        assertThat(CloudProviderFactory.getCloudIdProvider("aws_iam"), instanceOf(AwsCloudIdProvider.class));
    }

    @Test
    void returnsAzureProvider() {
        assertThat(CloudProviderFactory.getCloudIdProvider("azure_ad"), instanceOf(AzureCloudIdProvider.class));
    }

    @Test
    void returnsGcpProvider() {
        assertThat(CloudProviderFactory.getCloudIdProvider("gcp"), instanceOf(GcpCloudIdProvider.class));
    }

    @Test
    void throwsOnUnsupportedType() {
        assertThrows(RuntimeException.class, () -> CloudProviderFactory.getCloudIdProvider("unknown"));
    }
}
