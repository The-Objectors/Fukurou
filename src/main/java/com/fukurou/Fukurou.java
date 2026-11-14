//This code is a placeholder taken from the BlueJ guide website at https://www.bluej.org/extensions/writingextensions2.html
package com.fukurou;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;
import bluej.extensions2.ExtensionException;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;
import javafx.scene.control.Alert;

/*
 * This is the starting point of a BlueJ Extension
 */
public class Fukurou extends Extension implements PackageListener
{
    TelemetryData data = new TelemetryData();
    CompileHandler compileHandler = new CompileHandler(data.getCompileData(), data.getEventLog());
    LifecycleHandler lifecycleHandler = new LifecycleHandler(data.getLifecycleData(), data.getEventLog());
    InvocationHandler invocationHandler = new InvocationHandler(data.getInvocationData(), data.getEventLog());
    EditorActivityHandler editorActivityHandler = new EditorActivityHandler(data.getEditorData());
    private final Generator jsonGenerator = new JsonGenerator();
    private final Generator pdfGenerator = new PdfGenerator();
    
    /*
     * When this method is called, the extension may start its work.
     */
    @Override 
    public void startup(BlueJ bluej)
    {
        // Listen for BlueJ events at the "package" level
        bluej.addPackageListener(lifecycleHandler);
        bluej.addCompileListener(compileHandler);
        bluej.addClassListener(lifecycleHandler);
        bluej.addInvocationFinishedListener(invocationHandler);
        
        //Access BlueJ JavaFX thread
        editorActivityHandler.start();

        // sets a menu generator to allow a menu to be created for manual pdf export
        bluej.setMenuGenerator(new PdfExportMenuGenerator(this::exportPdf));
                  
    }

    /*
     * A package has been opened. Print the name of the project it is part of.
     * System.out is redirected to the BlueJ debug log file.
     * The location of this file is given in the Help/About BlueJ dialog box.
     */
    @Override 
    public void packageOpened(PackageEvent ev)
    {
        try
        {
            System.out.println("Project " + ev.getPackage().getProject().getName() 
                               + " opened.");
        }
        catch (ExtensionException e)
        {
            System.out.println("Project closed by BlueJ");
        }
    } 
               
    /*
     * A package is closing.
     */
    @Override
    public void packageClosing(PackageEvent ev)
    {
    } 
               
    /*
     * This method must decide if this Extension is compatible with the                
     * current release of the BlueJ Extensions API
     */
    @Override
    public boolean isCompatible()
    { 
        return (getExtensionsAPIVersionMajor() >= 3); 
    }

    /*
     * Returns the version number of this extension
     */
    @Override
    public String getVersion ()
    { 
        return ("1.0.0"); 
    }

    /*
     * Returns the user-visible name of this extension
     */
    @Override
    public String getName ()
    { 
        return ("Fukurou"); 
    }

    private void exportPdf() {
        try {
            data.updateSessionSnapshot();
            File report = pdfGenerator.generate(data);

            new Alert(Alert.AlertType.INFORMATION,
                "PDF report saved to: \n" + report.getAbsolutePath()
            ).showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                "PDF export failed: \n" + e.getMessage()
            ).showAndWait();
            e.printStackTrace();
        }
}

    // finalizes and generates report
    @Override
    public void terminate()
    {
        data.finishSession();
        try {
            jsonGenerator.generate(data);
        } catch (IOException ex) {
            System.err.println("Could not export JSON: " + ex.getMessage());
            ex.printStackTrace();
        }
        exportPdf();
        System.out.println ("Fukurou terminates");
    }

    @Override           
    public String getDescription ()
    {
        return ("Collect usage metrics from users.");
    }

    /*
     * Returns a URL where you can find info on this extension.
     * The real problem is making sure that the link will still be alive 
     * in three years...
     */
    @Override
    public URL getURL ()
    {
        try
        {
            return new URL("https://github.com/The-Objectors/Fukurou");
        }
        catch ( Exception e )
        {
            // The link is either dead or otherwise unreachable
            System.out.println ("Fukurou: getURL: Exception="+e.getMessage());
            return null;
        }
    }

}