/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Datapipe, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.jenkins.plugins.akeyless.model;

import static hudson.Util.fixEmptyAndTrim;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessIssuer extends AbstractDescribableImpl<AkeylessIssuer> implements AkeylessSecretBase {

    private String path;
    private String name;
    private String certName;
    private String publicKey;
    private String csrBase;

    private List<AkeylessSecretValue> secretValues;

    @DataBoundConstructor
    public AkeylessIssuer(
            String path,
            String name,
            String certName,
            String publicKey,
            String csrBase,
            List<AkeylessSecretValue> secretValues) {
        this.path = fixEmptyAndTrim(path);
        this.secretValues = secretValues;
        this.name = name;
        this.certName = certName;
        this.publicKey = publicKey;
        this.csrBase = csrBase;
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return this.name;
    }

    public String getCertName() {
        return this.certName;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public String getCsrBase() {
        return this.csrBase;
    }

    public List<AkeylessSecretValue> getSecretValues() {
        return this.secretValues;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<AkeylessIssuer> {

        @Override
        public String getDisplayName() {
            return "Akeyless Issuer";
        }
    }
}
