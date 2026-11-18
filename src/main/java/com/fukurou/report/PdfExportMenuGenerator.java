package com.fukurou.report;

import bluej.extensions2.BPackage;
import bluej.extensions2.MenuGenerator;
import javafx.scene.control.MenuItem;

/**
 * Adds an "Export Fukurou PDF Report" item to BlueJ's Tools menu.
 *
 * <p><b>Design pattern: Command.</b> The export action is passed in as a
 * {@link Runnable}, so the menu item runs it without knowing how the report is produced.
 */
public class PdfExportMenuGenerator extends MenuGenerator {
    /** Action run when the menu item is selected. */
    private final Runnable exportAction;

    /**
     * Creates a menu generator that runs the given action.
     *
     * @param exportAction the action to run when the menu item is selected
     */
    public PdfExportMenuGenerator(Runnable exportAction) {
        this.exportAction = exportAction;
    }

    /**
     * Creates the Tools menu item that runs the export action.
     *
     * @param bPackage the package the menu is shown for (not used)
     * @return a new menu item
     */
    @Override
    public MenuItem getToolsMenuItem(BPackage bPackage) {
        MenuItem exportItem = new MenuItem("Export Fukurou PDF Report");
        exportItem.setOnAction(event -> exportAction.run());
        return exportItem;
    }
}
