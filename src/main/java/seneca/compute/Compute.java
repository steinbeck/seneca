/* Compute.java
 *
 * Copyright (C) 1997, 1998, 1999, 2000  Dr. Christoph Steinbeck
 *
 * Contact: steinbeck@ice.mpg.de
 *
 * This software is published and distributed under artistic license.
 * The intent of this license is to state the conditions under which this Package 
 * may be copied, such that the Copyright Holder maintains some semblance
 * of artistic control over the development of the package, while giving the 
 * users of the package the right to use and distribute the Package in a
 * more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * The complete text of the license can be found in a file called LICENSE 
 * accompanying this package.
 */

package seneca.compute;

/**
 * Interface defining the methods of a Compute object
 */

public interface Compute {
    public void executeTask();

    public Object getTaskStatus();

    public Object getTaskResult();

    public String getTaskType();

    public void endTask();

    public void resetTask();

    public boolean willingToManage();

    public String getHostName();

    public String getID();

    public void setID(String id);

    public String getVersion();

    public long getSpeed();
}
