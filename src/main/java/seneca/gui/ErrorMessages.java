/* 
 * ErrorMessages.java    
 *
 * $RCSfile: ErrorMessages.java,v $    $Author: steinbeck $    $Date: 2004/02/16 09:50:53 $    $Revision: 1.4 $
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

import javax.swing.*;

public class ErrorMessages {
    public static boolean DEBUG = false;

    public static void error(String message) {
        System.err.print("Seneca: ");
        System.err.println(message);
    }

    public static void error(String message, Exception e) {
        if (DEBUG) {
            if (e != null) {
                e.printStackTrace();
            }
        }
        System.err.print("Seneca: ");
        System.err.println(message);
    }

    public static void fatalError(String message, Exception e) {
        if (DEBUG) {
            if (e != null) {
                e.printStackTrace();
            }
        }
        System.err.print("Seneca: ");
        System.err.println(message);
        System.err.println("Exiting.....");
        System.exit(-1);
    }

    public static void hint(String message) {
        JOptionPane.showMessageDialog(null, "SENECA hint", message,
                JOptionPane.INFORMATION_MESSAGE);
    }

}