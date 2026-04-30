package com.devsha256.mulelint.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.devsha256.mulelint.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(PreferenceConstants.P_SOURCE_FILES, "xml,dwl");
        store.setDefault(PreferenceConstants.P_TARGET_FILES, "yaml,yml");
    }
}
