package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.packet.Packet;


/**
 * Represents functions provided by modules comprising the contest engine.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IController  {
    
    void submitRun(int teamNumber, String problemName, String languageName, String filename) throws Exception;

    void setSiteNumber(int i);

    void setContestTime(ContestTime contestTime);

    void sendToClient(Packet confirmPacket);

    void setClientId(ClientId clientId);

}
