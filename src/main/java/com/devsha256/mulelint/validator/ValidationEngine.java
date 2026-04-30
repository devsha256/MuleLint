package com.devsha256.mulelint.validator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import com.devsha256.mulelint.Activator;
import com.devsha256.mulelint.markers.MarkerUtils;
import com.devsha256.mulelint.parser.MulePropertyExtractor;
import com.devsha256.mulelint.parser.YamlParser;
import com.devsha256.mulelint.preferences.PreferenceConstants;

public class ValidationEngine {

    public static void validateProject(IProject project, IProgressMonitor monitor) {
        monitor.beginTask("Validating Mule Properties", 100);
        try {
            MarkerUtils.deleteMarkers(project);
            monitor.worked(10);

            IPreferenceStore store = Activator.getDefault().getPreferenceStore();
            String targetPref = store.getString(PreferenceConstants.P_TARGET_FILES);
            if (targetPref == null || targetPref.trim().isEmpty()) targetPref = "yaml,yml";
            String[] targetExts = targetPref.split(",");
            for(int i=0; i<targetExts.length; i++) targetExts[i] = targetExts[i].trim();

            String sourcePref = store.getString(PreferenceConstants.P_SOURCE_FILES);
            if (sourcePref == null || sourcePref.trim().isEmpty()) sourcePref = "xml,dwl";
            String[] sourceExts = sourcePref.split(",");
            for(int i=0; i<sourceExts.length; i++) sourceExts[i] = sourceExts[i].trim();

            Set<String> leafProperties = new HashSet<>();
            Set<String> parentProperties = new HashSet<>();
            java.util.Map<String, String> propertyToYamlFile = new java.util.HashMap<>();
            List<IFile> yamlFiles = findFiles(project, targetExts);
            
            for (IFile yamlFile : yamlFiles) {
                try (InputStream is = yamlFile.getContents()) {
                    YamlParser.YamlResult yamlResult = YamlParser.extractFlattenedKeys(is);
                    leafProperties.addAll(yamlResult.leafKeys);
                    parentProperties.addAll(yamlResult.parentKeys);
                    
                    for (String key : yamlResult.leafKeys) propertyToYamlFile.put(key, yamlFile.getName());
                    for (String key : yamlResult.parentKeys) propertyToYamlFile.put(key, yamlFile.getName());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            monitor.worked(40);

            List<IFile> sourceFiles = findFiles(project, sourceExts);
            List<com.devsha256.mulelint.views.MuleValidationView.ValidationResult> results = new ArrayList<>();
            Set<String> usedProperties = new HashSet<>();
            
            for (IFile sourceFile : sourceFiles) {
                try (InputStream is = sourceFile.getContents();
                     java.util.Scanner scanner = new java.util.Scanner(is, "UTF-8")) {
                    scanner.useDelimiter("\\A");
                    String content = scanner.hasNext() ? scanner.next() : "";
                    List<MulePropertyExtractor.PropertyReference> refs = MulePropertyExtractor.extractProperties(content);
                    
                    for (MulePropertyExtractor.PropertyReference ref : refs) {
                        boolean isLeaf = leafProperties.contains(ref.key);
                        boolean isParent = parentProperties.contains(ref.key);
                        
                        String status;
                        if (isLeaf) {
                            status = "Valid";
                            usedProperties.add(ref.key);
                        } else if (isParent) {
                            status = "Missing"; // Referencing a parent is invalid
                        } else {
                            status = "Missing";
                        }
                        
                        results.add(new com.devsha256.mulelint.views.MuleValidationView.ValidationResult(
                            ref.key, 
                            sourceFile.getName(), 
                            ref.lineNumber, 
                            status
                        ));
                        
                        if (!"Valid".equals(status)) {
                            MarkerUtils.createMarker(
                                sourceFile, 
                                "Invalid/Missing Mule Property: " + ref.key + (isParent ? " (Parent key)" : ""), 
                                ref.lineNumber, 
                                IMarker.SEVERITY_ERROR
                            );
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            // Add Unused leaf properties
            for (String key : leafProperties) {
                if (!usedProperties.contains(key)) {
                    results.add(new com.devsha256.mulelint.views.MuleValidationView.ValidationResult(
                        key, propertyToYamlFile.get(key), 0, "Unused"
                    ));
                }
            }
            monitor.worked(50);
            
            org.eclipse.swt.widgets.Display.getDefault().asyncExec(() -> {
                try {
                    org.eclipse.ui.IWorkbenchPage page = org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    com.devsha256.mulelint.views.MuleValidationView view = (com.devsha256.mulelint.views.MuleValidationView) page.showView(com.devsha256.mulelint.views.MuleValidationView.ID);
                    if (view != null) {
                        view.setLastProject(project);
                        view.setResults(results);
                        if (results.isEmpty()) {
                            org.eclipse.swt.widgets.Shell shell = org.eclipse.swt.widgets.Display.getDefault().getActiveShell();
                            if (shell == null) {
                                org.eclipse.ui.IWorkbenchWindow window = org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                                if (window != null) shell = window.getShell();
                            }
                            if (shell != null) {
                                org.eclipse.jface.dialogs.MessageDialog.openInformation(
                                    shell,
                                    "Mule Validation",
                                    "No Mule property references found. Scanned " + sourceFiles.size() + " source files and " + yamlFiles.size() + " YAML files."
                                );
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
        } finally {
            monitor.done();
        }
    }

    private static List<IFile> findFiles(IProject project, String[] extensions) {
        List<IFile> files = new ArrayList<>();
        try {
            project.accept(new IResourceVisitor() {
                @Override
                public boolean visit(IResource resource) throws CoreException {
                    // Skip hidden folders (like .settings) and target folder
                    if (resource.getName().startsWith(".") || resource.getName().equals("target")) {
                        return false;
                    }
                    
                    if (resource.getType() == IResource.FILE) {
                        String fileExt = resource.getFileExtension();
                        if (fileExt != null) {
                            for (String ext : extensions) {
                                if (fileExt.equalsIgnoreCase(ext)) {
                                    files.add((IFile) resource);
                                    break;
                                }
                            }
                        }
                    }
                    return true;
                }
            });
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return files;
    }
}
