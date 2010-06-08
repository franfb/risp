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
import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	private Property portalHabitantes;
	private Property portalPadronales;
	private Property portalLatitud;
	private Property portalLongitud;
	private Property portalNumero;
	private Property portalVia;
	private Property portalTipo;
	private Property portalLabel;
	private Property viaCodigo;
	private Property viaEtiqueta;
	private Property viaHabitantes;
	private Property viaLongitud;
	private Property viaNombre;
	private Property viaTipo;
	private Property viaTipo2;
	private Property viaLabel;
	private String vocab = "http://localhost:2020/vocab/resource/";
	private String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	private String sparql = "http://localhost:2020/sparql";
	private Model model = getModel();


	public Model getModel(){
			model = ModelFactory.createDefaultModel();
			portalHabitantes = model.createProperty(vocab, "portal_habitantes");
			portalPadronales = model.createProperty(vocab, "portal_padronales");
			portalLatitud = model.createProperty(vocab, "portal_latitud");
			portalLongitud = model.createProperty(vocab, "portal_longitud");
			portalNumero = model.createProperty(vocab, "portal_numero");
			portalVia = model.createProperty(vocab, "portal_via");
			portalTipo = model.createProperty(vocab, "portal_tipo");
			portalLabel = model.createProperty(rdfs, "label");
			
			viaCodigo = model.createProperty(vocab, "via_codigo");
			viaEtiqueta = model.createProperty(vocab, "via_etiqueta");
			viaHabitantes = model.createProperty(vocab, "via_habitantes");
			viaLongitud = model.createProperty(vocab, "via_longitud");
			viaNombre = model.createProperty(vocab, "via_nombre");
			viaTipo = model.createProperty(vocab, "via_tipo");
			viaTipo2 = model.createProperty(vocab, "via_tipo2");
			viaLabel = model.createProperty(rdfs, "label");
			
		return model;
	}

	public Portal getPortal(LatLong place) {
		int pMax = 400;
		int p = new Double(Math.random() * pMax).intValue();
		return new Portal(place, 69, p, p / 10 + 1, "I", "el codigo va aqui", null);
	}
	
	public ArrayList<Portal> getPortales(LatLong topRight, LatLong BottomLeft) {
		String query = 	
			"PREFIX vocab: <http://localhost:2020/vocab/resource/> " +
			"SELECT  ?por WHERE   { " +
			"?por vocab:portal_latitud ?lat . " +
			"FILTER (?lat < " + topRight.getLatitude() + ") " +
			"FILTER (?lat > " + BottomLeft.getLatitude() + ") " +
			"?por vocab:portal_longitud ?lng . " +
			"FILTER (?lng < " + topRight.getLongitude() + ") " +
			"FILTER (?lng > " + BottomLeft.getLongitude() + ") . }";
		
		QueryExecution q = QueryExecutionFactory.sparqlService(sparql, query);
		ResultSet res = q.execSelect();
		
		ArrayList<Portal> portales = new ArrayList<Portal>();
		while (res.hasNext()){
			QuerySolution sol = res.next();

			Resource portal = model.getResource(sol.getResource("por").getURI());
			if (!portal.hasProperty(portalLabel)){
				model.read(portal.getURI());
				portal = model.getResource(portal.getURI());
			}
			Resource via = portal.getRequiredProperty(portalVia).getResource();
			
			if (!via.hasProperty(viaLabel)){
				model.read(via.getURI());
				via = model.getResource(via.getURI());
			}
			
			portales.add(new Portal(
				new LatLong(
					portal.getRequiredProperty(portalLatitud).getLiteral().getDouble(),
					portal.getRequiredProperty(portalLongitud).getLiteral().getDouble()
				),
				portal.getRequiredProperty(portalNumero).getLiteral().getInt(),		
				portal.getRequiredProperty(portalHabitantes).getLiteral().getInt(),		
				portal.getRequiredProperty(portalPadronales).getLiteral().getInt(),
				portal.getRequiredProperty(portalTipo).getLiteral().getString(),
				portal.getRequiredProperty(portalLabel).getLiteral().getString(),
				new Via(null,
					via.getRequiredProperty(viaHabitantes).getLiteral().getInt(),
					via.getRequiredProperty(viaLongitud).getLiteral().getDouble(),
					via.getRequiredProperty(viaNombre).getLiteral().getString(),
					via.getRequiredProperty(viaTipo).getLiteral().getString()
				)
			));
		}
		return portales;
	}

	@Override
	public Via getVia(LatLong place) {
		// TODO Auto-generated method stub
		return null;
	}
}
