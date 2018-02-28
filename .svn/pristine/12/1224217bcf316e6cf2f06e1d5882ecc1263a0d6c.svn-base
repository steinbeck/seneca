/*
 *  Parameters.java
 *
 *  $RCSfile: Parameters.java,v $    $Author: steinbeck $    $Date: 2004/02/16 09:50:53 $    $Revision: 1.4 $
 *
 *  Copyright (C) 1997-1999  The JChemPaint project
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package seneca.gui;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Description of the Class
 *
 * @author steinbeck
 * @created September 9, 2001
 */
public abstract class Parameters {


    protected Properties properties = null;

    private boolean DEBUG = false;
    private String propertiesFilename;
    private String propertiesDescription;
    private static final Logger logger = Logger.getLogger(Parameters.class);

    /**
     * Constructor for the Parameters object
     *
     * @param propertiesFilename    Description of Parameter
     * @param propertiesDescription Description of Parameter
     */
    protected Parameters(String propertiesFilename, String propertiesDescription) {
        this.propertiesFilename = propertiesFilename;
        this.propertiesDescription = propertiesDescription;
    }


    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    public Properties getProperties() {
        return this.properties;
    }


    protected abstract void setDefaults(Properties defaults);

    protected void getParameters() {
        Properties defaults = new Properties();
        FileInputStream in = null;

        setDefaults(defaults);

        properties = new Properties(defaults);

        try {
            in = new FileInputStream(System.getProperty("userApp.root") + propertiesFilename);
            properties.load(in);

        } catch (java.io.FileNotFoundException e) {
            in = null;
            logger.error("Can't find properties file using defaults.");

        } catch (java.io.IOException e) {
            logger.error("Can't read properties file using defaults.");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (java.io.IOException e) {
                }
                in = null;
            }
        }

        updateSettingsFromProperties();

    }

    protected abstract void updatePropertiesFromSettings();


    protected abstract void updateSettingsFromProperties();


    protected void saveParameters() {

        updatePropertiesFromSettings();

        if (DEBUG) {
            System.out.println("Just set properties: " + propertiesDescription);
            System.out.println(toString());
        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(System.getProperty("userApp.root") + propertiesFilename);
            properties.store(out, propertiesDescription);
        } catch (java.io.IOException e) {
            logger.error("Can't save properties. Oh well, it's not a big deal.");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (java.io.IOException e) {
                }
                out = null;
            }
        }
    }
}
