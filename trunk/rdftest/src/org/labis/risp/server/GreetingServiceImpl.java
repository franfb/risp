package org.labis.risp.server;

import java.util.ArrayList;

import org.labis.risp.client.GreetingService;
import org.labis.risp.client.LatLong;
import org.labis.risp.client.Portal;
import org.labis.risp.client.Via;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	private Model model = null;
	Property portalHabitantes;
	Property portalPadronales;
	Property portalLatitud;
	Property portalLongitud;
	Property portalNumero;
	Property portalVia;
	Property portalTipo;
	Property portalLabel;

	public Model getModel(){
		if (model == null){
			model = ModelFactory.createDefaultModel();
			portalHabitantes = model.createProperty("http://localhost:2020/vocab/resource/", "portal_habitantes");
			portalPadronales = model.createProperty("http://localhost:2020/vocab/resource/", "portal_padronales");
			portalLatitud = model.createProperty("http://localhost:2020/vocab/resource/", "portal_latitud");
			portalLongitud = model.createProperty("http://localhost:2020/vocab/resource/", "portal_longitud");
			portalNumero = model.createProperty("http://localhost:2020/vocab/resource/", "portal_numero");
			portalVia = model.createProperty("http://localhost:2020/vocab/resource/", "portal_via");
			portalTipo = model.createProperty("http://localhost:2020/vocab/resource/", "portal_tipo");
			portalLabel = model.createProperty("http://localhost:2020/vocab/resource/", "portal_label");
		}
		return model;
	}

	public Portal getPortal(LatLong place) {
		int pMax = 400;
		int p = new Double(Math.random() * pMax).intValue();
		return new Portal(place, 69, p, p / 10 + 1, "I", "el codigo va aqui", null);
	}
	
	public ArrayList<Portal> getPortales(LatLong topRight, LatLong BottomLeft) {
//		String query = 	
//			"PREFIX vocab: <http://localhost:2020/vocab/resource/> " +
//			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
//			"SELECT  ?lat ?lng ?hab ?pad ?tip ?cod ?num WHERE   { " +
//			"?pob vocab:portal_latitud ?lat . " +
//			"FILTER (?lat < " + topRight.getLatitude() + ") " +
//			"FILTER (?lat > " + BottomLeft.getLatitude() + ") " +
//			"?pob vocab:portal_longitud ?lng . " +
//			"FILTER (?lng < " + topRight.getLongitude() + ") " +
//			"FILTER (?lng > " + BottomLeft.getLongitude() + ") " +
//			"?pob vocab:portal_habitantes ?hab . " +
//			"?pob vocab:portal_padronales ?pad . " +
//			"?pob vocab:portal_tipo ?tip . " +
//			"?pob vocab:portal_numero ?num . " +
//			"?pob rdfs:label ?cod . }";
//		
//		String url = "http://localhost:2020/sparql";
//			
//		QueryExecution q = QueryExecutionFactory.sparqlService(url, query);
//		ResultSet res = q.execSelect();
//		
//		ArrayList<Portal> portales = new ArrayList<Portal>();
//		while (res.hasNext()){
//			QuerySolution sol = res.next();
//			
//			portales.add(new Portal(
//					new LatLong(sol.getLiteral("lat").getDouble(), sol.getLiteral("lng").getDouble()),
//					sol.getLiteral("num").getInt(),		
//					sol.getLiteral("hab").getInt(),		
//					sol.getLiteral("pad").getInt(),
//					sol.getLiteral("tip").getString(),
//					sol.getLiteral("cod").getString(), null));
//		}
		String query = 	
			"PREFIX vocab: <http://localhost:2020/vocab/resource/> " +
			"SELECT  ?pob WHERE   { " +
			"?pob vocab:portal_latitud ?lat . " +
			"FILTER (?lat < " + topRight.getLatitude() + ") " +
			"FILTER (?lat > " + BottomLeft.getLatitude() + ") " +
			"?pob vocab:portal_longitud ?lng . " +
			"FILTER (?lng < " + topRight.getLongitude() + ") " +
			"FILTER (?lng > " + BottomLeft.getLongitude() + ") . }";
		
		String url = "http://localhost:2020/sparql";
			
		QueryExecution q = QueryExecutionFactory.sparqlService(url, query);
		ResultSet res = q.execSelect();
		
		ArrayList<Portal> portales = new ArrayList<Portal>();
		
		

		
		while (res.hasNext()){
			QuerySolution sol = res.next();
			
			portales.add(new Portal(
				new LatLong(
					sol.getResource("pob").getRequiredProperty(portalLatitud).getDouble(),
					sol.getResource("pob").getRequiredProperty(portalLongitud).getDouble()
				),
				sol.getResource("pob").getRequiredProperty(portalNumero).getInt(),		
				sol.getLiteral("hab").getInt(),		
				sol.getLiteral("pad").getInt(),
				sol.getLiteral("tip").getString(),
				sol.getLiteral("cod").getString(), null)
			);
		}
		return portales;
	}

	@Override
	public Via getVia(LatLong place) {
		// TODO Auto-generated method stub
		return null;
	}
}
