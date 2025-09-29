package io.jenkins.plugins.akeyless;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class MaskSecretsLogsFilterTest {

    @Test
    void patternReturnsEmptyForNullInput() {
        assertThat(MaskSecretsLogsFilter.getPatternStringForSecrets(null), is(""));
    }

    @Test
    void patternQuotesAndOrdersByLength() {
        String pattern = MaskSecretsLogsFilter.getPatternStringForSecrets(Arrays.asList("a", "abc", "ab"));
        // abc should come before ab before a, and be quoted
        assertThat(pattern, containsString("\\Qabc\\E|\\Qab\\E|\\Qa\\E"));
    }
}
