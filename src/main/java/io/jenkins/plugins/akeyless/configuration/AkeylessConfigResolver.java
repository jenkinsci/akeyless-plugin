package io.jenkins.plugins.akeyless.configuration;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.ExtensionPoint;
import hudson.model.Item;
import hudson.model.ItemGroup;

/**
 * @author alexeydolgopyatov
 */
public abstract class AkeylessConfigResolver implements ExtensionPoint {
    @NonNull
    public abstract AkeylessConfiguration forJob(@NonNull Item job);

    @NonNull
    public abstract AkeylessConfiguration getConfig(@NonNull ItemGroup<Item> itemGroup);
}
