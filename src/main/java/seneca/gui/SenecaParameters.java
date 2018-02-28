/*
 *  SenecaParameters.java
 *
 *  $RCSfile: SenecaParameters.java,v $    $Author: steinbeck $    $Date: 2004/02/16 09:50:53 $    $Revision: 1.4 $
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

import java.util.Properties;

/**
 * Description of the Class
 *
 * @author steinbeck @created June 13, 2002
 */
public class SenecaParameters extends Parameters {
    /*
    property keys
     */

    private String lucyFileLocationName = "filelocation.lucy";
    private String senecaFileLocationName = "filelocation.seneca";
    private String senecaLastFileLocationName = "lastFileName.seneca";
    private String senecaCurrentNewDataFileName = "currentNewDataFile.seneca";
    private String logFileName = "filelocation.logfile";
    private String resultFileName = "filelocation.resultfile";
    private String serverConfigurationDirectoryName = "filelocation.serverConfiguration";
    /*
    property values
     */
    private String lucyFileLocation = "/";
    private String senecaFileLocation = "/";
    private String senecaLastLoadedFileLocation = "/";
    private String senecaCurrentNewDataFileLocation = "";
    private String logFile = "/senecalog";
    private String resultFile = "/senecaresults";
    private String serverConfigurationDirectory = null;

    /**
     * Constructor for the SenecaParameters object
     */
    public SenecaParameters() {
        super("seneca.props", "Seneca Properties");
        getParameters();
    }

    /**
     * Sets the defaults attribute of the SenecaParameters object
     *
     * @param defaultProperties The new defaults value
     */
    
    protected void setDefaults(Properties defaultProperties) {
        defaultProperties.put(lucyFileLocationName, lucyFileLocation);
        defaultProperties.put(senecaFileLocationName, senecaFileLocation);
        defaultProperties.put(senecaLastFileLocationName, senecaLastLoadedFileLocation);
        defaultProperties.put(senecaCurrentNewDataFileName, senecaCurrentNewDataFileLocation);
        defaultProperties.put(logFileName, logFile);
        defaultProperties.put(resultFileName, resultFile);
        if (serverConfigurationDirectory != null) {
            defaultProperties.put(serverConfigurationDirectoryName,
                    serverConfigurationDirectory);
        }
    }

    /**
     * Description of the Method
     */
    
    protected void updateSettingsFromProperties() {
        try {
            lucyFileLocation = properties.getProperty(lucyFileLocationName);
            senecaFileLocation = properties.getProperty(senecaFileLocationName);
            senecaLastLoadedFileLocation = properties.getProperty(senecaLastFileLocationName);
            senecaCurrentNewDataFileLocation = properties.getProperty(senecaCurrentNewDataFileName);
            logFile = properties.getProperty(logFileName);
            resultFile = properties.getProperty(resultFileName);
            serverConfigurationDirectory = properties.getProperty(serverConfigurationDirectoryName);

        } catch (NumberFormatException e) {
            // we don't care if the property was of the wrong format,
            // they've all got default values. So catch the exception
            // and keep going.
        }
    }

    /**
     * Description of the Method
     */
    
    protected void updatePropertiesFromSettings() {
        if (lucyFileLocation != null) {
            properties.put(lucyFileLocationName, lucyFileLocation);
        }
        if (senecaFileLocation != null) {
            properties.put(senecaFileLocationName, senecaFileLocation);
        }
        if (senecaLastLoadedFileLocation != null) {
            properties.put(senecaLastFileLocationName, senecaLastLoadedFileLocation);
        }
        if (senecaCurrentNewDataFileLocation != null) {
            properties.put(senecaCurrentNewDataFileName, senecaCurrentNewDataFileLocation);
        }
        if (logFile != null) {
            properties.put(logFileName, logFile);
        }
        if (resultFile != null) {
            properties.put(resultFileName, resultFile);
        }
        if (serverConfigurationDirectory != null) {
            properties.put(serverConfigurationDirectoryName,
                    serverConfigurationDirectory);
        }
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    
    public String toString() {
        return "[" + "lucyFileLocation=" + lucyFileLocation + ","
                + "senecaFileLocation=" + senecaFileLocation + "," + "logFile="
                + logFile + "," + "resultFile=" + resultFile + "," + "]";
    }

    /**
     * Sets the lucyFileLocation attribute of the SenecaParameters object
     *
     * @param lfl The new lucyFileLocation value
     */
    public void setLucyFileLocation(String lfl) {
        this.lucyFileLocation = lfl;
        saveParameters();
    }

    /**
     * Gets the lucyFileLocation attribute of the SenecaParameters object
     *
     * @return The lucyFileLocation value
     */
    public String getLucyFileLocation() {
        return lucyFileLocation;
    }

    /**
     * Sets the senecaFileLocation attribute of the SenecaParameters object
     *
     * @param sfl The new senecaFileLocation value
     */
    public void setSenecaFileLocation(String sfl) {
        this.senecaFileLocation = sfl;
        saveParameters();
    }

    /**
     * Gets the senecaFileLocation attribute of the SenecaParameters object
     *
     * @return The senecaFileLocation value
     */
    public String getSenecaFileLocation() {
        return senecaFileLocation;
    }

    public String getSenecaLastLoadedFileLocation() {
        return senecaLastLoadedFileLocation;
    }

    public void setSenecaLastLoadedFileLocation(String senecaLastLoadedFileLocation) {
        this.senecaLastLoadedFileLocation = senecaLastLoadedFileLocation;
        saveParameters();
    }


    public String getSenecaCurrentNewDataFileLocation() {
        return senecaCurrentNewDataFileLocation;
    }

    public void setSenecaCurrentNewDataFileLocation(String senecaCurrentNewDataFileLocation) {
        this.senecaCurrentNewDataFileLocation = senecaCurrentNewDataFileLocation;
        saveParameters();
    }

    /**
     * Gets the logFile attribute of the SenecaParameters object
     *
     * @return The logFile value
     */
    public String getLogFile() {
        return this.logFile;
    }

    /**
     * Sets the logFile attribute of the SenecaParameters object
     *
     * @param logFile The new logFile value
     */
    public void setLogFile(String logFile) {
        this.logFile = logFile;
        saveParameters();
    }

    /**
     * Gets the resultFile attribute of the SenecaParameters object
     *
     * @return The resultFile value
     */
    public String getResultFile() {
        return this.resultFile;
    }

    /**
     * Sets the resultFile attribute of the SenecaParameters object
     *
     * @param resultFile The new resultFile value
     */
    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
        saveParameters();
    }

    /**
     * Sets the serverConfigurationDirectory attribute of the SenecaParameters object
     *
     * @param serverConfigurationDirectory The new serverConfigurationDirectory value
     */
    public void setServerConfigurationDirectory(
            String serverConfigurationDirectory) {
        this.serverConfigurationDirectory = serverConfigurationDirectory;
        saveParameters();
    }

    /**
     * Gets the serverConfigurationDirectory attribute of the SenecaParameters object
     *
     * @return The serverConfigurationDirectory value
     */
    public String getServerConfigurationDirectory() {
        return serverConfigurationDirectory;
    }
}
