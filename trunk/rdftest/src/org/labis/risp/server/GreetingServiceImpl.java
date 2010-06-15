package org.labis.risp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import org.labis.risp.client.GreetingService;
import org.labis.risp.client.MyLatLng;
import org.labis.risp.client.MyPolygon;
import org.labis.risp.client.Portal;
import org.labis.risp.client.Via;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	
	private Property portalHabitantes;
	private Property portalPadronales;
	private Property portalLatitud;
	private Property portalLongitud;
	private Property portalNumero;
	private Property portalNombreVia;
	private Property portalVia;
	private Property portalTipo;
	private Property portalLabel;
	private Property viaCodigo;
	//private Property viaEtiqueta;
	private Property viaHabitantes;
	private Property viaLongitud;
	private Property viaNombre;
	//private Property viaTipo;
	private Property viaTipo2;
	private Property viaLabel;
	private String vocab = "http://localhost:2020/vocab/resource/";
	private String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	private String sparql = "http://localhost:2020/sparql";
	private Model model = getModel();
	

	private double min = 0.002;
	private double max = 0.02;
	
	//private int count = 0;

	public Model getModel(){
			model = ModelFactory.createDefaultModel();
			portalHabitantes = model.createProperty(vocab, "portal_habitantes");
			portalPadronales = model.createProperty(vocab, "portal_padronales");
			portalLatitud = model.createProperty(vocab, "portal_latitud");
			portalLongitud = model.createProperty(vocab, "portal_longitud");
			portalNumero = model.createProperty(vocab, "portal_numero");
			portalNombreVia = model.createProperty(vocab, "portal_nombreVia");
			portalVia = model.createProperty(vocab, "portal_via");
			portalTipo = model.createProperty(vocab, "portal_tipo");
			portalLabel = model.createProperty(rdfs, "label");
			
			viaCodigo = model.createProperty(vocab, "via_codigo");
			//viaEtiqueta = model.createProperty(vocab, "via_etiqueta");
			viaHabitantes = model.createProperty(vocab, "via_habitantes");
			viaLongitud = model.createProperty(vocab, "via_longitud");
			viaNombre = model.createProperty(vocab, "via_nombre");
			//viaTipo = model.createProperty(vocab, "via_tipo");
			viaTipo2 = model.createProperty(vocab, "via_tipo2");
			viaLabel = model.createProperty(rdfs, "label");
			
		return model;
	}

	private double distancia(Resource portal, MyLatLng point){
		double lat = portal.getRequiredProperty(portalLatitud).getLiteral().getDouble();
		double lng = portal.getRequiredProperty(portalLongitud).getLiteral().getDouble();
		return Math.sqrt(Math.pow(lat - point.getLatitude(), 2) + Math.pow(lng - point.getLongitude(), 2));
	}
	


	
	
	public double[] converter(double x, double y) {
        double  e = 0.08199189;  //excentricidad
        double e2 = 0.08226889; //segunda excentricidad
        double e22 = e2 * e2; //segunda exentricidad al cuadrado
        double c = 6399936.608; //radio polar de curvatura
        int m = -15; //meridiano central correspondiente a canarias
        
        double fi = y / (6366197.724 * 0.9996);
        double cos2fi = Math.cos(fi) * Math.cos(fi);
        double ni = (c * 0.9996) / Math.pow((1 + e22 * cos2fi), 0.5);
        double a = (x - 500000)/ni;
        double a1 = Math.sin(2*fi);
        double a2 = a1 * cos2fi;
        double j2 = fi + (a1/2);
        double j4 = (3 * j2 + a2) / 4;
        double j6 = (5 * j4 + a2 * cos2fi) / 3;
        double alfa = 3 * e22 / 4;
        double beta = 5 * (alfa*alfa) / 3;
        double gamma = 35 * (alfa*alfa*alfa) / 27;
        double bfi = 0.9996 * c * (fi - (alfa*j2) + (beta*j4) - (gamma*j6));
        double b = (y - bfi) / ni;
        double zeta = ((e22 * (a*a)) / 2) * cos2fi;
        double xi = a * (1 - (zeta / 3));
        double eta = b * (1 - zeta) + fi;
        double senhxi = (Math.exp(xi) - Math.exp(-xi)) / 2;
        double deltalambda = Math.atan(senhxi / Math.cos(eta));
        double tau = Math.atan(Math.cos(deltalambda) * Math.tan(eta));
        double firad = fi + (1 + e22 * cos2fi - 1.5 * e22 * Math.sin(fi) * Math.cos(fi)
                        * (tau - fi)) * (tau - fi);
        System.out.println(firad);
        
        double[] geos = new double[2];
        geos[0] = (firad / Math.PI) * 180;
        geos[1] = (deltalambda / Math.PI) * 180 + m;
        return geos;
}

	
	private Resource portalMasCercano(MyLatLng point, boolean conVia){
		double distance = min;
		ResIterator res = null;
		while (distance <= max){
			MyLatLng topRight = new MyLatLng(point.getLatitude() + distance, point.getLongitude() + distance);
			MyLatLng bottomLeft = new MyLatLng(point.getLatitude() - distance, point.getLongitude() - distance);
			res = getPortal(null, topRight, bottomLeft, conVia);
			if (res.hasNext()){
				Resource masCercano = null;
				distance = 999999.9;
				while (res.hasNext()){
					Resource portal = res.next();
					double d = distancia(portal, point);
					if ((masCercano == null || d < distance)){
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
		if (masCercano == null){
			return null;
		}
		return newPortal(masCercano);
	}
	
	
	
	public Via getVia(MyLatLng point) {
		Resource masCercano = portalMasCercano(point, true);
		if (masCercano == null){
			return null;
		}
		return newVia(getVia(masCercano), newPortal(masCercano).getCoordenadas());
	}
	
	private Resource getVia(Resource portal){
		Resource via = model.getResource(portal.getProperty(portalVia).getResource().getURI());
		if (!via.hasProperty(viaLabel)){
			model.read(via.getURI());
			via = model.getResource(via.getURI());
		}
		return via;
	}
	
	private ResIterator getPortal(MyPolygon poly, MyLatLng topRight, MyLatLng BottomLeft, boolean conVia){
		String query = 	
			"PREFIX vocab: <http://localhost:2020/vocab/resource/> " +
			"DESCRIBE  ?por WHERE { " +
			"?por vocab:portal_latitud ?lat . " +
			"FILTER (?lat < " + topRight.getLatitude() + ") " +
			"FILTER (?lat > " + BottomLeft.getLatitude() + ") " +
			"?por vocab:portal_longitud ?lng . " +
			"FILTER (?lng < " + topRight.getLongitude() + ") " +
			"FILTER (?lng > " + BottomLeft.getLongitude() + ") . }";
		QueryExecution q = QueryExecutionFactory.sparqlService(sparql, query);
		Model m = q.execDescribe();
		model.add(m);
//		if (poly != null){
//			ResIterator res = m.listSubjects();
//			while (res.hasNext()){
//				Resource portal = res.next();
//				if (! contains(poly, new MyLatLng(
//					portal.getRequiredProperty(portalLatitud).getLiteral().getDouble(),
//					portal.getRequiredProperty(portalLongitud).getLiteral().getDouble()
//				))){
//					m.removeAll(portal, null, null);
//					//res.remove();
//				}
//			}
//		}
		if (conVia){
			return m.listSubjectsWithProperty(portalVia);
		}
		return m.listSubjects();
	}
	
	private Portal newPortal(Resource portal){
		String via = "Sin vía asociada";
		Statement s = portal.getProperty(portalNombreVia);
		if (s != null){
			via = s.getLiteral().getString();
		}
		return new Portal(
			new MyLatLng(
				portal.getRequiredProperty(portalLatitud).getLiteral().getDouble(),
				portal.getRequiredProperty(portalLongitud).getLiteral().getDouble()
			),
			portal.getRequiredProperty(portalNumero).getLiteral().getInt(),		
			portal.getRequiredProperty(portalHabitantes).getLiteral().getInt(),		
			portal.getRequiredProperty(portalPadronales).getLiteral().getInt(),
			portal.getRequiredProperty(portalTipo).getLiteral().getString(),
			portal.getRequiredProperty(portalLabel).getLiteral().getString(),
			via
		);
	}
	
	private Via newVia(Resource via, MyLatLng coordenadas){		
		return new Via(coordenadas,
			via.getRequiredProperty(viaHabitantes).getLiteral().getInt(),
			via.getRequiredProperty(viaLongitud).getLiteral().getDouble(),
			via.getRequiredProperty(viaNombre).getLiteral().getString(),
			via.getRequiredProperty(viaTipo2).getLiteral().getString(),
			via.getRequiredProperty(viaCodigo).getLiteral().getString()
		);
	}
	
	public ArrayList<Portal> getPortales(MyPolygon poly, MyLatLng topRight, MyLatLng bottomLeft) {
		String url = "jdbc:mysql://localhost/risp";
        String user = "root";
        String passw = "labis";
        try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection cnt = DriverManager.getConnection(url, user, passw);
	        java.sql.Statement stm = cnt.createStatement();
	        ResultSet rs = stm.executeQuery("SELECT CODIGO, COORDX, COORDY FROM PORTAL");
	        while (rs.next()){
	                //System.out.println("CODIGO: " + rs.getLong("CODIGO") + "  LONGITUD: " + rs.getInt("LATITUD"));
	            	double x = rs.getDouble("COORDX");
	            	double y = rs.getDouble("COORDY");
	            	MyLatLng latLng = Coordinates.UTM2LatLng(x, y, 28, false);
	                //double latlong[] = converter(x, y);
	                String sql = "UPDATE PORTAL SET LATITUD = ?, LONGITUD = ? WHERE CODIGO = ?";
	                PreparedStatement s = cnt.prepareStatement(sql);
	                s.setDouble(1, latLng.getLatitude());
	                s.setDouble(2, latLng.getLongitude());
	                s.setString(3, rs.getString("CODIGO"));
	                s.executeUpdate();
	        }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		
		
//		ResIterator res = getPortal(poly, topRight, bottomLeft, false);
//		ArrayList<Portal> portales = new ArrayList<Portal>();
//		while (res.hasNext()){
//			Resource portal = res.next();
//			portales.add(newPortal(portal));
//		}
//		return portales;
                return null;
	}

	public ArrayList<Via> getVias(MyPolygon poly, MyLatLng topRight, MyLatLng bottomLeft) {
		System.out.println("111");
		ResIterator res = getPortal(poly, topRight, bottomLeft, true);
		System.out.println("222");
		ArrayList<Via> vias = new ArrayList<Via>();
		HashSet<Integer> set = new HashSet<Integer>();
		while (res.hasNext()){
			Resource portal = res.next();
			Resource via = getVia(portal);
			int codigoVia = Integer.parseInt(via.getRequiredProperty(viaCodigo).getLiteral().getString());
			if (!set.contains(codigoVia)){
				set.add(codigoVia);
				vias.add(newVia(via, newPortal(portal).getCoordenadas()));
			}
		}
		System.out.println("333");
		return vias;
	}
	
	public boolean contains(MyPolygon p, MyLatLng latLng) {
		int j = 0;
		boolean oddNodes = false;
		double x = latLng.getLongitude();
		double y = latLng.getLatitude();
		for (int i = 0; i < p.getVertexCount(); i++) {
			j++;
			if (j == p.getVertexCount()) {
				j = 0;
			}
			if (((p.getVertex(i).getLatitude() < y) && (p.getVertex(j)
					.getLatitude() >= y))
					|| ((p.getVertex(j).getLatitude() < y) && (p.getVertex(i)
							.getLatitude() >= y))) {
				if (p.getVertex(i).getLongitude()
						+ (y - p.getVertex(i).getLatitude())
						/ (p.getVertex(j).getLatitude() - p.getVertex(i)
								.getLatitude())
						* (p.getVertex(j).getLongitude() - p.getVertex(i)
								.getLongitude()) < x) {
					oddNodes = !oddNodes;
				}
			}
		}
		return oddNodes;
	}

	public ArrayList<Portal> getPortales(Via via) {
		ResIterator it = model.listSubjectsWithProperty(viaCodigo, via.getCodigo());
		if (!it.hasNext()){
			return null;
		}
		Resource v = it.next();
		it = model.listSubjectsWithProperty(portalVia, v);
		ArrayList<Portal> list = new ArrayList<Portal>();
		while (it.hasNext()){
			Resource portal = it.next();
			if (!portal.hasProperty(portalLabel)){
				model.read(portal.getURI());
				portal = model.getResource(portal.getURI());
			}
			list.add(newPortal(portal));
		}
		return list;
	}

}
