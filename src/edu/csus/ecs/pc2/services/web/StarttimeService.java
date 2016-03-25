package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;



@Path("/starttime")
@Produces(MediaType.APPLICATION_JSON)
public class StarttimeService {
	
	
	public StarttimeService(){
		super();
	}
	
	/**
	 * This method ...
	 * 
	 * @param 
	 * @return 
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public String setStartime() {
		
	    //from the CLI Wiki Contest_Start_Interface spec:
//	    PUT HTTP body is application/json:
//	    { "starttime":1265335138.26 }
//	    or:
//	    { "starttime":"undefined" }
//	    HTTP response is:
//	    200: if successful.
//	    400: if the payload is invalid json, start time is invalid, etc.
//	    401: if authentication failed.
//	    403: if contest is already started
//	    403: if setting to 'undefined' with less than 10s left to previous start time.
//	    403: if setting to new (defined) start time with less than 30s left to previous start time.
//	    403: if the new start time is less than 30s from now.
		
		//output the response to the requester (note that this actually returns it to Jersey, 
		// which converts it to JSON and forwards that to the caller as the HTTP.response).
//		return Response.status(Response.Status.OK).build(); 
	    
	    return "Start Time <here>" ;

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getStartTime() {
	
	//from the CLI Wiki Contest_Start_Interface spec:
//	GET HTTP body returns application/json:
//	{ "starttime":1265335138.26 }
//	or:
//	{ "starttime":"undefined" }
	    
//	    return Response.status(Response.Status.OK).build(); 

	    return "Start Time <here>";
	}
	

}