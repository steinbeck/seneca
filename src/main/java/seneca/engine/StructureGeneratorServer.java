/*
 *  $RCSfile: StructureGeneratorServer.java,v $
 *  $Author: steinbeck $
 *  $Date: 2002/09/22 13:50:08 $
 *  $Revision: 1.6 $
 *
 *  Copyright (C) 1997, 1998, 1999, 2000  Dr. Christoph Steinbeck
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This software is published and distributed under artistic license.
 *  The intent of this license is to state the conditions under which this Package
 *  may be copied, such that the Copyright Holder maintains some semblance
 *  of artistic control over the development of the package, while giving the
 *  users of the package the right to use and distribute the Package in a
 *  more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *  THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  The complete text of the license can be found in a file called LICENSE
 *  accompanying this package.
 */
package seneca.engine;

import org.apache.log4j.Logger;
import seneca.compute.Compute;
import seneca.structgen.StructureGenerator;

import java.net.InetAddress;

/**
 * @author steinbeck @created September 9, 2001
 */
public class StructureGeneratorServer implements Compute {

    String id = null;
    String hostName = null;
    String status = null;
    long speed = -1;


    StructureGenerator structureGenerator = null;
    private static final Logger logger = Logger.getLogger(StructureGeneratorServer.class);

    public final static String VERSION = "$Revision: 1.6 $";


    public StructureGeneratorServer(String id) {
        this();
        setID(id);

    }

    public StructureGeneratorServer() {
        super();
        setID("StructureGeneratorServer " + getVersion() + " on "
                + getHostName());
    }

    
    public void setID(String id) {
        this.id = id;
        logger.info("Server ID set to " + id);
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    
    public Object getTaskResult() {
        logger.info("Request for taskresult");
        return structureGenerator.getResult();
    }

    
    public String getTaskType() {
        return null;
    }

    public String getTaskVersion() {

        return structureGenerator.getVersion();
    }

    
    public String getID() {
        return this.id;
    }

    
    public String getHostName() {
        if (hostName == null) {
            try {
                hostName = InetAddress.getLocalHost().getHostName();
                logger.info("Request for hostname: " + hostName);
            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }

        return hostName;
    }

    public String getHostAddress() {
        String address = null;
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return address;
    }

    
    public Object getTaskStatus() {

        try {
            if (structureGenerator != null) {
                return structureGenerator.getStatus();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;

    }

    
    public String getVersion() {
        String cleanver = VERSION.substring(VERSION.indexOf(" "),
                VERSION.lastIndexOf("$")).trim();
        return cleanver;
    }

    public long getSpeed() {
        return speed;
    }

    public String getIdentMessage() {
        String s = ("This is " + id + "\n");
        try {
            s += "Version " + getVersion() + "\n";
            s += "running on " + getHostName();
        } catch (Exception exc) {
            s += "Can't get identification info";
        }
        return s;
    }

    public void executeTask() {

        structureGenerator.start();

    }

    
    public boolean willingToManage() {
        return true;
    }

    
    public void endTask() {
        structureGenerator.stop();
    }

    
    public void resetTask() {
        structureGenerator = null;
    }


    public void setStructureGenerator(StructureGenerator gen) {

        this.structureGenerator = gen;
    }

    public StructureGenerator getStructureGenerator() {
        return structureGenerator;
    }

}
