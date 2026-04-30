package com.devsha256.mulelint.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class MarkerUtils {

    public static final String MARKER_ID = "com.devsha256.mulelint.mulePropertyMarker";

    public static void createMarker(IResource resource, String message, int lineNumber, int severity) {
        try {
            IMarker marker = resource.createMarker(MARKER_ID);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            if (lineNumber > 0) {
                marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMarkers(IResource resource) {
        try {
            resource.deleteMarkers(MARKER_ID, true, IResource.DEPTH_INFINITE);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
}
