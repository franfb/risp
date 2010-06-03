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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	
	Model model = null;
	

	public Model getModel(){
		Model m = ModelFactory.createDefaultModel();
		System.out.println("VA A LEER");
		m.read("http://localhost:2020/all/poblacion", "RDF/XML");
		System.out.println("HA LEIDO");
		StmtIterator siter = m.listStatements(new SimpleSelector(null,
				RDFS.label, (RDFNode) null) {
			public boolean selects(Statement s) {
				if (!s.getObject().toString().startsWith("38")) {
					System.out.println("HAY UNA DISTINTA");
					// System.exit(1);
				}
				return s.getObject().toString().startsWith("38");
			}
		});
		System.out.println("HA SELECCIONADO");
		if (!siter.hasNext()) {
			System.out.println("No statements were found in the database");
			return null;
		}
		System.out.println("The database contains labels for:");

//		SELECT  ?poblacion
//				WHERE   { ?poblacion vocab:LATITUD ?lat .
//				          FILTER (?lat < 30.5)
//				          ?poblacion vocab:LONGITUD ?lng .
//				          FILTER (?lng < 0)
//				}
//				LIMIT 10
		
		
		// Property poblacion =
		// m.createProperty("http://localhost:2020/vocab/resource/",
		// "POBLACION");
		//
		// Property padrones =
		// m.createProperty("http://localhost:2020/vocab/resource/",
		// "NUMHOJAS");
		// Property tipo =
		// m.createProperty("http://localhost:2020/vocab/resource/", "TIPO");
		// Property latitud =
		// m.createProperty("http://localhost:2020/vocab/resource/", "LATITUD");
		// Property longitud =
		// m.createProperty("http://localhost:2020/vocab/resource/",
		// "LONGITUD");

		//http://localhost:2020/sparql?query=SELECT+DISTINCT+*+WHERE+{%0D%0A++%3Fs+%3Fp+%3Fo%0D%0A}%0D%0ALIMIT+10
		
		Model m2 = ModelFactory.createDefaultModel();
		while (siter.hasNext()) {
			Statement s = siter.nextStatement();
			//System.out.println("http://localhost:2020/data/poblacion/"
			//		+ s.getSubject().getLocalName());
			
			m2.read("http://localhost:2020/data/poblacion/"
					//+ s.getSubject().getLocalName(), "RDF/XML");
					+ s.getObject().toString(), "RDF/XML");
		}
		System.out.println("HA LEIDO TODO");
		m.add(m2);
		return m;
	}


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

	
	public boolean initialize() {
		return true;
	}

}
