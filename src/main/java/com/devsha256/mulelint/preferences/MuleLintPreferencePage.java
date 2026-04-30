package com.devsha256.mulelint.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.devsha256.mulelint.Activator;

public class MuleLintPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public MuleLintPreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("MuleLint Configuration Settings");
    }

    @Override
    public void createFieldEditors() {
        addField(new StringFieldEditor(PreferenceConstants.P_SOURCE_FILES, 
            "Source Files (comma separated extensions):", getFieldEditorParent()));
            
        addField(new StringFieldEditor(PreferenceConstants.P_TARGET_FILES, 
            "Target Files (comma separated extensions):", getFieldEditorParent()));
    }

    @Override
    public void init(IWorkbench workbench) {
    }
}
