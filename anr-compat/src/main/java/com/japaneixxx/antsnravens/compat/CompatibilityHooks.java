package com.japaneixxx.antsnravens.compat;

/**
 * Extension point for optional integrations. Compatibility modules must not own mod loading.
 */
public interface CompatibilityHooks {
    String modId();

    void register();
}
