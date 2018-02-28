package seneca.core;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 21/01/2013
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class FilePreference {

    private static final Logger LOGGER = Logger.getLogger(FilePreference.class);

    public static File getOSAppDataRoot() {

        String OS = System.getProperty("os.name").toUpperCase();

        if (OS.contains("WIN")) {
            // make it easier to handle from within java
            return new File(System.getenv("APPDATA"));
        } else if (OS.contains("MAC")) {
            File file = new File(System.getProperty("user.home")
                    + File.separator + "Library"
                    + File.separator + "Application Support");
            return file;
        } else if (OS.contains("NUX")) {
            return getFolderForNUX();
        }

        return new File(System.getProperty("user.dir"));

    }

    private static File getFolderForNUX() {
        // would be good to write an program specific name
        File appdata = new File(System.getProperty("user.home")
                + File.separator + ".appdata");

        File readme = new File(appdata, "README");

        // create an place a README
        if (!readme.exists()) {
            if (appdata.mkdirs()) {
                FileWriter writer = null;

                try {
                    writer = new FileWriter(readme);
                    writer.write(".appdata is the default folder used by the creative application" +
                            " framework http://sourceforge.net/projects/seneca/");
                    writer.write("The creative application framework is currently used by Seneca but" +
                            "may be useed by other applications in future.");
                } catch (IOException e) {
                    LOGGER.error("Could not create README file for ~/.appdata");
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        LOGGER.error(e.getMessage());
                    }
                }


            }
        }

        return appdata;

    }

    public static File getOSSystemDataRoot() {
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("MAC")) {
            File file = new File(File.separator + "System"
                    + File.separator + "Library"
                    + File.separator + "Seneca"
                    + File.separator + "lucene-4");
            return file;
        } else if (OS.contains("WIN")) {
            // make it easier to handle from within java
            return new File(System.getenv("APPDATA") + File.separator + "Seneca"
                    + File.separator + "lucene-4");
        } else if (OS.contains("NUX")) {
            return new File(getFolderForNUX().getAbsolutePath() + File.separator + "Seneca"
                    + File.separator + "lucene-4");
        }
        return new File(System.getProperty("user.dir") + File.separator + "Seneca"
                + File.separator + "lucene-4");
    }
}
