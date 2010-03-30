package org.labis.risp.server;

import org.labis.risp.client.GreetingService;
import org.labis.risp.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		
		Model m = ModelFactory.createDefaultModel();
		m.read("http://ws.geonames.org/rdf?geonameId=2950159");
		m.write(System.out, "N3");
//		Property p = m.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#", "lat");
//		System.out.println(p.toString());
		
//		m.write(System.out, "Turtle");
		
		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}
}
