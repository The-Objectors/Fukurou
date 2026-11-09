package com.fukurou;

import bluej.extensions2.BPackage;
import bluej.extensions2.MenuGenerator;
import javafx.scene.control.MenuItem;

public class PdfExportMenuGenerator extends MenuGenerator {
    private final Runnable exportAction;

    public PdfExportMenuGenerator(Runnable exportAction) {
        this.exportAction = exportAction;
    }

    @Override
    public MenuItem getToolsMenuItem(BPackage bPackage) {
        MenuItem exportItem = new MenuItem("Export Fukurou PDF Report");
        exportItem.setOnAction(event -> exportAction.run());
        return exportItem;
    }
}