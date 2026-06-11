package seneca.gui;

import javax.swing.*;
import java.awt.Taskbar;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class SenecaMain {

    private static String os = System.getProperty("os.name");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (os.equalsIgnoreCase("Mac OS X")) {
            setupOSX();
        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                
                public void run() {

                    Seneca.getInstance();
                    //autoOpenPreviousFile();
                }
            });
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }

    }

    private static void autoOpenPreviousFile() {
        SenecaParameters parameters = new SenecaParameters();
        String previousFileName = parameters.getSenecaLastLoadedFileLocation();
        Seneca.getInstance().openFile(new File(previousFileName));
    }

    private static void setupOSX() {

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                "SENECA");
        // Set the dock icon via the modern java.awt.Taskbar API (since Java 9).
        // Replaces the removed/encapsulated com.apple.eawt.Application call, which
        // no longer resolves on modern modular JDKs without --add-exports.
        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar.getTaskbar().setIconImage(Seneca.logo_512x512.getImage());
            }
        } catch (UnsupportedOperationException | SecurityException ex) {
            // Dock icon is cosmetic; ignore if the platform refuses it.
        }
    }
}
