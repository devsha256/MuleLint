package com.devsha256.mulelint.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class MuleValidationView extends ViewPart {

    public static final String ID = "com.devsha256.mulelint.views.MuleValidationView";
    private TableViewer viewer;
    private static MuleValidationView instance;
    private List<ValidationResult> currentResults = new ArrayList<>();
    private org.eclipse.core.resources.IProject lastProject;

    public void setLastProject(org.eclipse.core.resources.IProject project) {
        this.lastProject = project;
    }

    public org.eclipse.core.resources.IProject getLastProject() {
        return lastProject;
    }

    public static class ValidationResult {
        public String propertyKey;
        public String sourceFile;
        public int lineNumber;
        public String status;

        public ValidationResult(String key, String file, int line, String status) {
            this.propertyKey = key;
            this.sourceFile = file;
            this.lineNumber = line;
            this.status = status;
        }
    }

    public MuleValidationView() {
        instance = this;
    }

    private Text searchText;
    private String filterString = "";
    private String categoryFilter = "ALL";

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        // Filter panel
        Composite filterComposite = new Composite(parent, SWT.NONE);
        filterComposite.setLayout(new GridLayout(4, false));
        filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        org.eclipse.swt.widgets.Button allBtn = new org.eclipse.swt.widgets.Button(filterComposite, SWT.RADIO);
        allBtn.setText("All");
        allBtn.setSelection(true);
        allBtn.addListener(SWT.Selection, e -> { if (allBtn.getSelection()) { categoryFilter = "ALL"; viewer.refresh(); } });

        org.eclipse.swt.widgets.Button errorBtn = new org.eclipse.swt.widgets.Button(filterComposite, SWT.RADIO);
        errorBtn.setText("Errors (Missing)");
        errorBtn.addListener(SWT.Selection, e -> { if (errorBtn.getSelection()) { categoryFilter = "ERROR"; viewer.refresh(); } });

        org.eclipse.swt.widgets.Button warnBtn = new org.eclipse.swt.widgets.Button(filterComposite, SWT.RADIO);
        warnBtn.setText("Warnings (Unused)");
        warnBtn.addListener(SWT.Selection, e -> { if (warnBtn.getSelection()) { categoryFilter = "WARNING"; viewer.refresh(); } });

        // Search panel
        Composite searchComposite = new Composite(parent, SWT.NONE);
        searchComposite.setLayout(new GridLayout(2, false));
        searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        org.eclipse.swt.widgets.Label searchLabel = new org.eclipse.swt.widgets.Label(searchComposite, SWT.NONE);
        searchLabel.setText("Search: ");
        
        searchText = new Text(searchComposite, SWT.BORDER | SWT.SEARCH | SWT.ICON_SEARCH);
        searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        searchText.setMessage("Type to filter properties...");
        searchText.addModifyListener(e -> {
            filterString = searchText.getText().toLowerCase();
            viewer.refresh();
        });

        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        createColumns(parent, viewer);
        
        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        viewer.addFilter(new org.eclipse.jface.viewers.ViewerFilter() {
            @Override
            public boolean select(org.eclipse.jface.viewers.Viewer viewer, Object parentElement, Object element) {
                ValidationResult result = (ValidationResult) element;
                
                // Category Filter
                if ("ERROR".equals(categoryFilter) && !"Missing".equals(result.status)) return false;
                if ("WARNING".equals(categoryFilter) && !"Unused".equals(result.status)) return false;
                
                // Search Filter
                if (filterString == null || filterString.isEmpty()) return true;
                return result.propertyKey.toLowerCase().contains(filterString) ||
                       result.sourceFile.toLowerCase().contains(filterString);
            }
        });

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(currentResults);
    }

    private void createColumns(final Composite parent, final TableViewer viewer) {
        String[] titles = { "Property Key", "Status", "Source File", "Line" };
        int[] bounds = { 250, 100, 250, 100 };

        TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((ValidationResult) element).propertyKey;
            }
        });

        col = createTableViewerColumn(titles[1], bounds[1], 1);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((ValidationResult) element).status;
            }
            @Override
            public Color getForeground(Object element) {
                String status = ((ValidationResult) element).status;
                if ("Valid".equals(status)) {
                    return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
                } else if ("Missing".equals(status)) {
                    return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
                } else if ("Unused".equals(status)) {
                    return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
                }
                return null;
            }
        });

        col = createTableViewerColumn(titles[2], bounds[2], 2);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((ValidationResult) element).sourceFile;
            }
        });

        col = createTableViewerColumn(titles[3], bounds[3], 3);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return String.valueOf(((ValidationResult) element).lineNumber);
            }
        });
    }

    private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
    }

    public void setResults(List<ValidationResult> results) {
        this.currentResults = results;
        if (viewer != null && !viewer.getTable().isDisposed()) {
            viewer.setInput(results);
            viewer.refresh();
        }
    }

    public static MuleValidationView getInstance() {
        return instance;
    }

    @Override
    public void setFocus() {
        if (viewer != null && !viewer.getTable().isDisposed()) {
            viewer.getControl().setFocus();
        }
    }
}
