// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

/**
 * Listener for all Login Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IContestTimeListener {

    void contestTimeAdded (ContestTimeEvent event);
    
    void contestTimeRemoved (ContestTimeEvent event);
    
    void contestTimeChanged(ContestTimeEvent event);

    void contestStarted(ContestTimeEvent event);

    void contestStopped(ContestTimeEvent event);
    
    void contestAutoStarted(ContestTimeEvent event);

    void refreshAll(ContestTimeEvent event);

}
