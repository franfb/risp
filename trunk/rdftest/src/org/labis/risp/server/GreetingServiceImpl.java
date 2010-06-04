package org.labis.risp.server;

import java.util.ArrayList;

import org.labis.risp.client.GreetingService;
import org.labis.risp.client.LatLong;
import org.labis.risp.client.Street;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public ArrayList<Street> getStreets(LatLong topRight, LatLong BottomLeft) {
		
		String query = 	
			"PREFIX vocab: <http://localhost:2020/vocab/resource/> " +
			"SELECT  ?lat ?lng ?hab ?pad ?tip ?cod WHERE   { " +
			"?pob vocab:LATITUD ?lat . " +
			"FILTER (?lat < " + topRight.getLatitude() + ") " +
			"FILTER (?lat > " + BottomLeft.getLatitude() + ") " +
			"?pob vocab:LONGITUD ?lng . " +
			"FILTER (?lng < " + topRight.getLongitude() + ") " +
			"FILTER (?lng > " + BottomLeft.getLongitude() + ") " +
			"?pob vocab:HABITANTES ?hab . " +
			"?pob vocab:NUMHOJAS ?pad . " +
			"?pob vocab:TIPO ?tip . " +
			"?pob vocab:CODIGO ?cod . }";
		
		String url = "http://localhost:2020/sparql";
			
		QueryExecution q = QueryExecutionFactory.sparqlService(url, query);
		ResultSet res = q.execSelect();
		
		ArrayList<Street> streets = new ArrayList<Street>();
		while (res.hasNext()){
			QuerySolution sol = res.next();
			streets.add(new Street(
					sol.getLiteral("cod").getLong(),
					new LatLong(sol.getLiteral("lat").getDouble(), sol.getLiteral("lng").getDouble()),
					sol.getLiteral("hab").getInt(),		
					sol.getLiteral("pad").getInt(),
					sol.getLiteral("tip").getChar()));
		}
		return streets;
	}

	public Street getStreet(LatLong place) {
		int pMax = 400;
		int p = new Double(Math.random() * pMax).intValue();
		return new Street(69, place, p, p / 10 + 1, 'I', "nombre de la calle");
	}
}
