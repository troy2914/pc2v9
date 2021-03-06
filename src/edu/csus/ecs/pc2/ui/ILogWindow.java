// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;


/**
 * Log window interface.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ILogWindow extends UIPlugin {

    void setTitle(String string);

    void setVisible(boolean showWindow);

    boolean isVisible();

    void dispose();
}
