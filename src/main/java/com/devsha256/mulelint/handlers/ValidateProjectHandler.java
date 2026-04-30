package com.devsha256.mulelint.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.devsha256.mulelint.validator.ValidationEngine;

public class ValidateProjectHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
            Object element = (selection instanceof IStructuredSelection) ? ((IStructuredSelection) selection).getFirstElement() : null;
            IProject project = null;
            if (element instanceof IProject) {
                project = (IProject) element;
            } else if (element instanceof org.eclipse.core.runtime.IAdaptable) {
                project = (IProject) ((org.eclipse.core.runtime.IAdaptable) element).getAdapter(IProject.class);
            }
            
            // Fallback to last project if selection is not a project
            if (project == null) {
                com.devsha256.mulelint.views.MuleValidationView view = com.devsha256.mulelint.views.MuleValidationView.getInstance();
                if (view != null) {
                    project = view.getLastProject();
                }
            }
            
            if (project != null) {
                
                org.eclipse.ui.IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
                if (window != null && window.getActivePage() != null) {
                    window.getActivePage().saveAllEditors(false);
                }

                final IProject finalProject = project;
                Job job = new Job("Mule Property Validation") {
                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        ValidationEngine.validateProject(finalProject, monitor);
                        return Status.OK_STATUS;
                    }
                };
                job.setUser(true);
                job.schedule();
            }
        return null;
    }
}
