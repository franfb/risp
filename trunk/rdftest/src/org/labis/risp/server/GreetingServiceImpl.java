package org.labis.risp.server;

import java.util.ArrayList;
import java.util.HashSet;

import org.labis.risp.client.GreetingService;
import org.labis.risp.client.MyLatLng;
import org.labis.risp.client.MyPolygon;
import org.labis.risp.client.Portal;
import org.labis.risp.client.Via;
import org.labis.risp.client.Zona;

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
import com.hp.hpl.jena.rdf.model.Statement;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private Property habitantes;
	private Property hojas_padronales;
	private Property coordenada_latitud;
	private Property coordenada_longitud;
	private Property numeroPortal;
	private Property portalVia;
	private Property portalLabel;
	private Property codigoVia;
	private Property longitudMetros;
	private Property nombreVia;
	private Property viaLabel;
	private Property type;
	private String vocab = "http://localhost:2020/vocab/resource/";
	private String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	private String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";


	private String sparql = "http://localhost:2020/sparql";
	private Model model = getModel();

	private double min = 0.002;
	private double max = 0.02;

	public Model getModel() {
		model = ModelFactory.createDefaultModel();
		habitantes = model.createProperty(vocab, "habitantes");
		hojas_padronales = model.createProperty(vocab, "hojas_padronales");
		coordenada_latitud = model.createProperty(vocab, "coordenada_latitud");
		coordenada_longitud = model.createProperty(vocab, "coordenada_longitud");
		numeroPortal = model.createProperty(vocab, "numero_del_portal");
		portalVia = model.createProperty(vocab, "via_del_portal");
		portalLabel = model.createProperty(rdfs, "label");

		codigoVia = model.createProperty(vocab, "codigo_de_via");
		longitudMetros = model.createProperty(vocab, "longitud");
		nombreVia = model.createProperty(vocab, "nombre_completo_via");
		viaLabel = model.createProperty(rdfs, "label");

		type = model.createProperty(rdf, "type");
		return model;
	}

	private double distancia(Resource portal, MyLatLng point) {
		double lat = portal.getRequiredProperty(coordenada_latitud).getLiteral()
				.getDouble();
		double lng = portal.getRequiredProperty(coordenada_longitud).getLiteral()
				.getDouble();
		return Math.sqrt(Math.pow(lat - point.getLatitude(), 2)
				+ Math.pow(lng - point.getLongitude(), 2));
	}

	private Resource portalMasCercano(MyLatLng point, boolean conVia) {
		double distance = min;
		ResIterator res = null;
		while (distance <= max) {
			MyLatLng topRight = new MyLatLng(point.getLatitude() + distance,
					point.getLongitude() + distance);
			MyLatLng bottomLeft = new MyLatLng(point.getLatitude() - distance,
					point.getLongitude() - distance);
			res = getPortales(topRight, bottomLeft, conVia);
			if (res.hasNext()) {
				Resource masCercano = null;
				distance = 999999.9;
				while (res.hasNext()) {
					Resource portal = res.next();
					double d = distancia(portal, point);
					if ((masCercano == null || d < distance)) {
						masCercano = portal;
						distance = d;
					}
				}
				return masCercano;
			}
			distance *= 2;
		}
		return null;
	}

	public Portal getPortal(MyLatLng point) {
		Resource masCercano = portalMasCercano(point, false);
		if (masCercano == null) {
			return null;
		}
		return newPortal(masCercano);
	}

	public Via getVia(MyLatLng point) {
		Resource masCercano = portalMasCercano(point, true);
		if (masCercano == null) {
			return null;
		}
		return newVia(getVia(masCercano), newPortal(masCercano)
				.getCoordenadas());
	}

	private Resource getVia(Resource portal) {
		Resource via = model.getResource(portal.getProperty(portalVia)
				.getResource().getURI());
		if (!via.hasProperty(viaLabel)) {
			model.read(via.getURI());
			via = model.getResource(via.getURI());
		}
		return via;
	}

	private ResIterator getPortales(MyLatLng topRight, MyLatLng BottomLeft,
			boolean conVia) {
		String query = "PREFIX vocab: <http://localhost:2020/vocab/resource/> "
				+ "DESCRIBE  ?por WHERE { "
				+ "?por vocab:coordenada_latitud ?lat . " + "FILTER (?lat < "
				+ topRight.getLatitude() + ") " + "FILTER (?lat > "
				+ BottomLeft.getLatitude() + ") "
				+ "?por vocab:coordenada_longitud ?lng . " + "FILTER (?lng < "
				+ topRight.getLongitude() + ") " + "FILTER (?lng > "
				+ BottomLeft.getLongitude() + ") . }";
		QueryExecution q = QueryExecutionFactory.sparqlService(sparql, query);
		Model m = q.execDescribe();
		model.add(m);
		if (conVia) {
			return m.listSubjectsWithProperty(portalVia);
		}
		return m.listSubjects();
	}

	public Zona getZona(MyPolygon poly) {
		// fAB = (y-y0) * (x1-x0) - (x-x0) * (y1-y0);
		// fCA = (y-y2) * (x0-x2) - (x-x2) * (y0-y2);
		// fBC = (y-y1) * (x2-x1) - (x-x1) * (y2-y1);
		// if (fAB * fBC > 0 && fBC * fCA > 0)

		Zona zona = new Zona();
		zona.setHabitantes(0);
		zona.setHojas(0);

		for (int i = 0; i < poly.getTriangles(); i++) {
			double y0 = poly.getTriangle(i)[0].getLatitude();
			double y1 = poly.getTriangle(i)[1].getLatitude();
			double y2 = poly.getTriangle(i)[2].getLatitude();

			double x0 = poly.getTriangle(i)[0].getLongitude();
			double x1 = poly.getTriangle(i)[1].getLongitude();
			double x2 = poly.getTriangle(i)[2].getLongitude();

			double x1x0 = x1 - x0;
			double y1y0 = y1 - y0;

			double x0x2 = x0 - x2;
			double y0y2 = y0 - y2;

			double x2x1 = x2 - x1;
			double y2y1 = y2 - y1;

			String queryString = "PREFIX vocab: <http://localhost:2020/vocab/resource/> "
					+ "SELECT (sum(?hab) as ?habitantes) (sum(?pad) as ?hojas) WHERE { "
					+ "?por vocab:coordenada_latitud ?lat . "
					+ "?por vocab:coordenada_longitud ?lng . "
					+ "?por vocab:habitantes ?hab . "
					+ "?por vocab:hojas_padronales ?pad . " +

					"FILTER (0 < " + "((?lat - "
					+ y0
					+ ") * "
					+ x1x0
					+ " - (?lng - "
					+ x0
					+ ") * "
					+ y1y0
					+ ") "
					+ // fAB
					" * "
					+ "((?lat - "
					+ y1
					+ ") * "
					+ x2x1
					+ " - (?lng - "
					+ x1
					+ ") * "
					+ y2y1
					+ ") "
					+ // fBC
					") "
					+

					"FILTER (0 < "
					+ "((?lat - "
					+ y2
					+ ") * "
					+ x0x2
					+ " - (?lng - "
					+ x2
					+ ") * "
					+ y0y2
					+ ") "
					+ // fCA
					" * "
					+ "((?lat - "
					+ y1
					+ ") * "
					+ x2x1
					+ " - (?lng - "
					+ x1 + ") * " + y2y1 + ") " + // fBC
					") " + ". }";

			Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
			QueryExecution q = QueryExecutionFactory.sparqlService(sparql,
					query);
			ResultSet set = q.execSelect();
			QuerySolution sol = set.nextSolution();
			zona.setHabitantes(zona.getHabitantes()
					+ sol.getLiteral("habitantes").getInt());
			zona.setHojas(zona.getHojas() + sol.getLiteral("hojas").getInt());
		}
		return zona;
	}

	
	public Zona getVias(MyPolygon poly) {
		ArrayList<Via> vias = new ArrayList<Via>();
		HashSet<Integer> set = new HashSet<Integer>();
		int habitantes = 0;
		int hojas = 0;
		
		for (int i = 0; i < poly.getTriangles(); i++) {
			double y0 = poly.getTriangle(i)[0].getLatitude();
			double y1 = poly.getTriangle(i)[1].getLatitude();
			double y2 = poly.getTriangle(i)[2].getLatitude();

			double x0 = poly.getTriangle(i)[0].getLongitude();
			double x1 = poly.getTriangle(i)[1].getLongitude();
			double x2 = poly.getTriangle(i)[2].getLongitude();

			double x1x0 = x1 - x0;
			double y1y0 = y1 - y0;

			double x0x2 = x0 - x2;
			double y0y2 = y0 - y2;

			double x2x1 = x2 - x1;
			double y2y1 = y2 - y1;

			String queryString = "PREFIX vocab: <http://localhost:2020/vocab/resource/> "
					+ "SELECT ?habitantes ?habitantesVia ?hojas ?longitud ?nombre ?codigo ?lat ?lng WHERE { "
					+ "?por vocab:coordenada_latitud ?lat . "
					+ "?por vocab:coordenada_longitud ?lng . "
					+ "?por vocab:via_del_portal ?via . "
					+ "?por vocab:habitantes ?habitantes . "
					+ "?por vocab:hojas_padronales ?hojas . "
					+ "?via vocab:longitud ?longitud . "
					+ "?via vocab:habitantes ?habitantesVia . "
					+ "?via vocab:nombre_completo_via ?nombre . "
					+ "?via vocab:codigo_de_via ?codigo . "
					+ "FILTER (0 < " + "((?lat - "
					+ y0
					+ ") * "
					+ x1x0
					+ " - (?lng - "
					+ x0
					+ ") * "
					+ y1y0
					+ ") "
					+ // fAB
					" * "
					+ "((?lat - "
					+ y1
					+ ") * "
					+ x2x1
					+ " - (?lng - "
					+ x1
					+ ") * "
					+ y2y1
					+ ") "
					+ // fBC
					") "
					+

					"FILTER (0 < "
					+ "((?lat - "
					+ y2
					+ ") * "
					+ x0x2
					+ " - (?lng - "
					+ x2
					+ ") * "
					+ y0y2
					+ ") "
					+ // fCA
					" * "
					+ "((?lat - "
					+ y1
					+ ") * "
					+ x2x1
					+ " - (?lng - "
					+ x1 + ") * " + y2y1 + ") " + // fBC
					") " + ". }";

			
			
			Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
			QueryExecution q = QueryExecutionFactory.sparqlService(sparql,
					query);
			ResultSet it = q.execSelect();
			while (it.hasNext()){
				QuerySolution sol = it.next();
				int codigo = Integer.parseInt(sol.getLiteral("codigo").getString());
				if (!set.contains(codigo)){
					set.add(codigo);
					vias.add(newVia(sol));
				}
				habitantes += sol.getLiteral("habitantes").getInt();
				hojas += sol.getLiteral("hojas").getInt();
			}
		}
		Zona zona = new Zona();
		zona.setHabitantes(habitantes);
		zona.setHojas(hojas);
		zona.setVias(vias);
		return zona;
	}
	
	
	private Portal newPortal(Resource portal) {
		String via = "Sin vía asociada";
		Statement s = portal.getProperty(nombreVia);
		if (s != null) {
			via = s.getLiteral().getString();
		}
		return new Portal(
				new MyLatLng(portal.getRequiredProperty(coordenada_latitud)
						.getLiteral().getDouble(), portal.getRequiredProperty(
						coordenada_longitud).getLiteral().getDouble()),
				portal.getRequiredProperty(numeroPortal).getLiteral().getInt(),
				portal.getRequiredProperty(habitantes).getLiteral()
						.getInt(),
				portal.getRequiredProperty(hojas_padronales).getLiteral()
						.getInt(),
				portal.getRequiredProperty(portalLabel).getLiteral()
						.getString(), via);
	}

	private Via newVia(Resource via, MyLatLng coord) {
		return new Via(coord, via.getRequiredProperty(habitantes)
				.getLiteral().getInt(), via.getRequiredProperty(longitudMetros)
				.getLiteral().getDouble(), via.getRequiredProperty(nombreVia)
				.getLiteral().getString(), via.getRequiredProperty(codigoVia)
				.getLiteral().getString());
	}

	
	// A PARTIR DE: ?habitantesVia ?longitud ?nombre ?codigo ?lat ?lng
	private Via newVia(QuerySolution sol) {
		return new Via(
			new MyLatLng(sol.getLiteral("lat").getDouble(), sol.getLiteral("lng").getDouble()),
			sol.getLiteral("habitantesVia").getInt(),
			sol.getLiteral("longitud").getDouble(),
			sol.getLiteral("nombre").getString(),
			sol.getLiteral("codigo").getString());
	}
	
	public ArrayList<Portal> getPortales(MyPolygon poly) {
		ResIterator res = getPortales(poly.getTopRight(), poly.getBottomLeft(),
				false);
		ArrayList<Portal> portales = new ArrayList<Portal>();
		while (res.hasNext()) {
			Resource portal = res.next();
			if (poly.contains(new MyLatLng(portal.getRequiredProperty(
					coordenada_latitud).getLiteral().getDouble(), portal
					.getRequiredProperty(coordenada_longitud).getLiteral()
					.getDouble()))) {
				portales.add(newPortal(portal));
			}
		}
		return portales;
	}

//	public ArrayList<Portal> getPortales(Via via) {
//		ResIterator it = model.listSubjectsWithProperty(codigoVia, via
//				.getCodigo());
//		if (!it.hasNext()) {
//			return null;
//		}
//		Resource v = it.next();
//		it = model.listSubjectsWithProperty(portalVia, v);
//		ArrayList<Portal> list = new ArrayList<Portal>();
//		while (it.hasNext()) {
//			Resource portal = it.next();
//			if (!portal.hasProperty(portalLabel)) {
//				model.read(portal.getURI());
//				portal = model.getResource(portal.getURI());
//			}
//			list.add(newPortal(portal));
//		}
//		return list;
//	}

	
	public ArrayList<Portal> getPortales(Via via) {
		String query = "PREFIX vocab: <http://localhost:2020/vocab/resource/> "
			+" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "DESCRIBE ?por WHERE { "
			+ "?por rdf:type vocab:portal . "
		    + "?por vocab:nombre_completo_via \"" + via.getNombre() + "\" . }";
		QueryExecution q = QueryExecutionFactory.sparqlService(sparql, query);
		Model m = q.execDescribe();
		model.add(m);
		ResIterator res = m.listSubjectsWithProperty(hojas_padronales);
		if (!res.hasNext()){
			return null;
		}
		ArrayList<Portal> list = new ArrayList<Portal>();
		while (res.hasNext()) {
			Resource portal = res.next();
			list.add(newPortal(portal));
		}
		return list;
	}
	
	

	public ArrayList<Portal> getPortales(String nombre, int numero) {
		String query = "PREFIX vocab: <http://localhost:2020/vocab/resource/> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> "
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
			+ "DESCRIBE ?por WHERE { "
			+ "?por vocab:numero_del_portal \"" + numero + "\"^^xsd:int . "
		    + "?por vocab:nombre_completo_via ?nombre . " +
		    " FILTER( fn:contains (fn:lower-case(?nombre), fn:lower-case(\"" + nombre + "\"))) }";
		QueryExecution q = QueryExecutionFactory.sparqlService(sparql, query);
		Model m = q.execDescribe();
		model.add(m);
		ResIterator res = m.listSubjectsWithProperty(hojas_padronales);
		if (!res.hasNext()){
			return null;
		}
		ArrayList<Portal> list = new ArrayList<Portal>();
		while (res.hasNext()) {
			Resource portal = res.next();
			list.add(newPortal(portal));
		}
		return list;
	}

	public ArrayList<Via> getVias(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}

	public Via getVia(Portal portal) {
		String query = "PREFIX vocab: <http://localhost:2020/vocab/resource/> "
			+" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "DESCRIBE ?via WHERE { "
			+ "?via rdf:type vocab:via . "
		    + "?via vocab:nombre_completo_via \"" + portal.getVia() + "\" . }";
		QueryExecution q = QueryExecutionFactory.sparqlService(sparql, query);
		Model m = q.execDescribe();
		model.add(m);
		ResIterator res = m.listSubjectsWithProperty(longitudMetros);
		if (!res.hasNext()){
			return null;
		}
		return newVia(res.next(), portal.getCoordenadas());
	}
}
