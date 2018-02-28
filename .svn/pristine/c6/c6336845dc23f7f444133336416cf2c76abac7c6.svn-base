/* 
 * Parameters.java    
 *
 * $RCSfile: ResourceManager.java,v $    $Author: steinbeck $    $Date: 2001/07/25 11:52:40 $    $Revision: 1.1.1.1 $
 * 
 * Copyright (C) 1997-1999  The JChemPaint project
 *
 * Contact: steinbeck@ice.mpg.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package seneca.gui;

import org.apache.log4j.Logger;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceManager {

    private ResourceBundle resources;
    private static final Logger logger = Logger.getLogger(ResourceManager.class);

    public ResourceManager(ResourceBundle resources) {
        this.resources = resources;
    }

    protected String getResourceString(String nm) {
        String str;
        try {
            str = resources.getString(nm);
        } catch (MissingResourceException mre) {
            str = null;
            logger.error(mre.getMessage());
        }
        return str;
    }

    protected URL getResource(String key) {
        String name = getResourceString(key);
        if (name != null) {
            URL url = this.getClass().getResource(name);
            return url;
        }
        return null;
    }
}