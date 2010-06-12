package org.labis.risp.server;

import java.util.ArrayList;

import org.labis.risp.client.GreetingService;
import org.labis.risp.client.LatLong;
import org.labis.risp.client.Portal;
import org.labis.risp.client.Via;


import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	ArrayList<LatLngBounds> zonas = new ArrayList<LatLngBounds>();
	
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

	private double min = 0.002;
	private double max = 0.02;
	
	private int count = 0;

	public Model getModel(){
			System.out.println("CREANDO EL MODELO DESDE CERO");
		
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

	private double distancia(Resource portal, LatLong point){
		double lat = portal.getRequiredProperty(portalLatitud).getLiteral().getDouble();
		double lng = portal.getRequiredProperty(portalLongitud).getLiteral().getDouble();
		return Math.sqrt(Math.pow(lat - point.getLatitude(), 2) + Math.pow(lng - point.getLongitude(), 2));
	}
	
	
	public Portal getPortal(LatLong point) {
		double distance = min;
		ResultSet res = null;
		while (distance <= max){
			LatLong topRight = new LatLong(point.getLatitude() + distance, point.getLongitude() + distance);
			LatLong bottomLeft = new LatLong(point.getLatitude() - distance, point.getLongitude() - distance);
			res = portales(topRight, bottomLeft);
			if (res.hasNext()){
				break;
			}
			res = null;
			distance *= 2;
		}
		if (res == null){
			return null;
		}
		
		Resource masCercano = null;
		distance = 999999.9;
		
		while (res.hasNext()){
			QuerySolution sol = res.next();
			Resource portal = model.getResource(sol.getResource("por").getURI());
			if (!portal.hasProperty(portalLabel)){
				model.read(portal.getURI());
				portal = model.getResource(portal.getURI());
			}
			Resource via = null;
			if (portal.getProperty(portalVia) != null){
				via = portal.getProperty(portalVia).getResource();
				if (!via.hasProperty(viaLabel)){
					model.read(via.getURI());
				}
			}
			double d = distancia(portal, point);
			
			if (masCercano == null || d < distance){
				masCercano = portal;
				distance = d;
			}
		}
		
		Via v = null;
		if (masCercano.getProperty(portalVia) != null){
			Resource via = masCercano.getProperty(portalVia).getResource();
			v = newVia(via);
		}
		return newPortal(masCercano, v);
	}

	
	public Via getVia(LatLong point) {
		double distance = min;
		ResultSet res = null;
		
		System.out.println("CONTADOR: " + count++);
		
		System.out.println("CHECKPOINT 1");

		while (distance <= max){
			LatLong topRight = new LatLong(point.getLatitude() + distance, point.getLongitude() + distance);
			LatLong bottomLeft = new LatLong(point.getLatitude() - distance, point.getLongitude() - distance);
			res = portalesLatLng(topRight, bottomLeft);
			if (res.hasNext()){
				break;
			}
			res = null;
			distance *= 2;
			System.out.println("CHECKPOINT 1.1");
		}
		
		System.out.println("CHECKPOINT 2");
		if (res == null){
			return null;
		}
		
		Resource masCercano = null;
		distance = 999999.9;
		
		while (res.hasNext()){
			QuerySolution sol = res.next();
			Resource portal = sol.getResource("por");

			portal.addLiteral(portalLatitud, sol.getLiteral("lat").getDouble());
			portal.addLiteral(portalLongitud, sol.getLiteral("lng").getDouble());
			portal.addProperty(portalVia, sol.getResource("via"));
			
			double d = distancia(portal, point);
			if ((masCercano == null || d < distance)){
				masCercano = portal;
				distance = d;
			}
			//System.out.println("CHECKPOINT 2.1");
		}
		
		System.out.println("CHECKPOINT 3");

		
		Resource via = masCercano.getProperty(portalVia).getResource();
		if (!via.hasProperty(viaLabel)){
			model.read(via.getURI());
			via = model.getResource(via.getURI());
		}
		
		Via v = newVia(via);
		
		ResIterator r = portales(masCercano.getProperty(portalVia).getResource());
		System.out.println("CHECKPOINT 4");
		ArrayList<Portal> list = new ArrayList<Portal>();
		int i = 0;
		while (r.hasNext()){
			Resource portal = r.next();
			if (!portal.hasProperty(portalLabel)){
				i++;
				model.read(portal.getURI());
				portal = model.getResource(portal.getURI());
			}
			list.add(newPortal(portal, v));
		}
		System.out.println("POR EL SPARQL: " + list.size() + " y " + i);
		v.setPortales(list);
		return v;
	}
	
	public Via getViaOriginal(LatLong point) {
		double distance = min;
		ResultSet res = null;
		
		System.out.println("CHECKPOINT 1");

		while (distance <= max){
			LatLong topRight = new LatLong(point.getLatitude() + distance, point.getLongitude() + distance);
			LatLong bottomLeft = new LatLong(point.getLatitude() - distance, point.getLongitude() - distance);
			res = portales(topRight, bottomLeft);
			if (res.hasNext()){
				break;
			}
			res = null;
			distance *= 2;
			System.out.println("CHECKPOINT 1.1");
		}
		
		System.out.println("CHECKPOINT 2");
		if (res == null){
			return null;
		}
		
		Resource masCercano = null;
		distance = 999999.9;
		
		while (res.hasNext()){
			QuerySolution sol = res.next();
			Resource portal = model.getResource(sol.getResource("por").getURI());
			if (!portal.hasProperty(portalLabel)){
				model.read(portal.getURI());
				portal = model.getResource(portal.getURI());
			}
			double d = distancia(portal, point);
			if ((masCercano == null || d < distance) && portal.getProperty(portalVia) != null){
				masCercano = portal;
				distance = d;
			}
			//System.out.println("CHECKPOINT 2.1");
		}
		System.out.println("CHECKPOINT 3");
		
		if (masCercano == null){
			return null;
		}
		
		Resource via = masCercano.getProperty(portalVia).getResource();
		if (!via.hasProperty(viaLabel)){
			model.read(via.getURI());
		}
		
		Via v = newVia(masCercano.getProperty(portalVia).getResource());
		
		ResIterator r = portales(masCercano.getProperty(portalVia).getResource());
		System.out.println("CHECKPOINT 4");
		ArrayList<Portal> list = new ArrayList<Portal>();
		int i = 0;
		while (r.hasNext()){
			Resource portal = r.next();
			if (!portal.hasProperty(portalLabel)){
				i++;
				model.read(portal.getURI());
				portal = model.getResource(portal.getURI());
			}
			list.add(newPortal(portal, v));
		}
		System.out.println("POR EL SPARQL: " + list.size() + " y " + i);
		v.setPortales(list);
		return v;
	}
	
	
	private ResultSet portales(LatLong topRight, LatLong BottomLeft){
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
		return res;
	}
	
	private ResultSet portalesLatLng(LatLong topRight, LatLong BottomLeft){
		String query = 	
			"PREFIX vocab: <http://localhost:2020/vocab/resource/> " +
			"SELECT  ?por ?lat ?lng ?via WHERE   { " +
			"?por vocab:portal_via ?via . " +
			"?por vocab:portal_latitud ?lat . " +
			"FILTER (?lat < " + topRight.getLatitude() + ") " +
			"FILTER (?lat > " + BottomLeft.getLatitude() + ") " +
			"?por vocab:portal_longitud ?lng . " +
			"FILTER (?lng < " + topRight.getLongitude() + ") " +
			"FILTER (?lng > " + BottomLeft.getLongitude() + ") . }";
		QueryExecution q = QueryExecutionFactory.sparqlService(sparql, query);
		ResultSet res = q.execSelect();
		return res;
	}
	
	private ResIterator portales(Resource via){
		return model.listSubjectsWithProperty(portalVia, via);

//		ResIterator r = model.listSubjectsWithProperty(portalVia, via);
//		int i = 0;
//		while (r.hasNext()){
//			r.next();
//			i++;
//			//System.out.println("VIA DE LA CALLE: " + r.next().getURI());
//		}
//		System.out.println("POR EL MODEL: " + i);
//		
//		
//		
//		String query = 	
//			"PREFIX vocab: <http://localhost:2020/vocab/resource/> " +
//			"SELECT  ?por WHERE   { " +
//			"?por vocab:portal_via <" + via.getURI() + "> . }";
//		QueryExecution q = QueryExecutionFactory.sparqlService(sparql, query);
//		ResultSet res = q.execSelect();
//		return res;
	}
	
	private Portal newPortal(Resource portal, Via v){
		return new Portal(
			new LatLong(
				portal.getRequiredProperty(portalLatitud).getLiteral().getDouble(),
				portal.getRequiredProperty(portalLongitud).getLiteral().getDouble()
			),
			portal.getRequiredProperty(portalNumero).getLiteral().getInt(),		
			portal.getRequiredProperty(portalHabitantes).getLiteral().getInt(),		
			portal.getRequiredProperty(portalPadronales).getLiteral().getInt(),
			portal.getRequiredProperty(portalTipo).getLiteral().getString(),
			portal.getRequiredProperty(portalLabel).getLiteral().getString(),
			v
		);
	}
	
	private Via newVia(Resource via){		
		return new Via(null,
			via.getRequiredProperty(viaHabitantes).getLiteral().getInt(),
			via.getRequiredProperty(viaLongitud).getLiteral().getDouble(),
			via.getRequiredProperty(viaNombre).getLiteral().getString(),
			via.getRequiredProperty(viaTipo).getLiteral().getString()
		);
	}
	
	public ArrayList<Portal> getPortales(LatLong topRight, LatLong bottomLeft) {
		ResultSet res = portales(topRight, bottomLeft);
		
		ArrayList<Portal> portales = new ArrayList<Portal>();
		while (res.hasNext()){
			QuerySolution sol = res.next();

			Resource portal = model.getResource(sol.getResource("por").getURI());
			if (!portal.hasProperty(portalLabel)){
				model.read(portal.getURI());
				portal = model.getResource(portal.getURI());
			}
			
			Via v = null;
			if (portal.getProperty(portalVia) != null){
				Resource via = portal.getRequiredProperty(portalVia).getResource();
				if (!via.hasProperty(viaLabel)){
					model.read(via.getURI());
					via = model.getResource(via.getURI());
				}
				v = newVia(via);
			}
			portales.add(newPortal(portal, v));
		}
		return portales;
	}

	@Override
	public ArrayList<Via> getVias(LatLong topRight, LatLong BottomLeft) {
		// TODO Auto-generated method stub
		return null;
	}

}
