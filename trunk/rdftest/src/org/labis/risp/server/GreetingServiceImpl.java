package org.labis.risp.server;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.labis.risp.client.GreetingService;
import org.labis.risp.client.LatLong;
import org.labis.risp.client.Street;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.VCARD;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {
	// Properties used by the RDF model
	Property lat;
	Property lng;
	Property info;
	Property name;
	
	public String greetServer(String input) throws Exception {
		if (true)
			return "feo";
		// Verify that the input is valid. 
//		if (!FieldVerifier.isValidName(input)) {
//			// If the input is not valid, throw an IllegalArgumentException back to
//			// the client.
//			throw new IllegalArgumentException(
//					"Name must be at least 4 characters long");
//		}

		
		// Aquí se debería obtener el modelo desde el servidor D2RServer
		// En su lugar, vamos a crear un modelo de ejemplo mediante Jena.
//		Model m = createRdfModel();
		Model m = ModelFactory.createDefaultModel();
//		m.read("file:/d:/etsii/labis/rdfs/licencia.rdf", "N-TRIPLE");
		m.read("http://localhost:2020/all/licencia", "RDF/XML");
		
		StmtIterator siter = m.listStatements(
				new SimpleSelector(null, RDFS.label, (RDFNode) null) {
					public boolean selects(Statement s)
		            { return s.getObject().toString().startsWith("licencia"); }
			    });
		
		// Creamos el documento XML que vamos a devolver al cliente
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation().createDocument(null, null, null);
		Element companies = doc.createElement("companies");
		
		if (siter.hasNext()) {
		    System.out.println("The database contains labels for:");
		    
			name = m.createProperty("http://localhost:2020/vocab/resource/", "label");
			lat = m.createProperty("http://localhost:2020/vocab/resource/", "licencia_localizacionLat");
			lng = m.createProperty("http://localhost:2020/vocab/resource/", "licencia_LocalizacionLog");
			info = m.createProperty("http://localhost:2020/vocab/resource/", "licencia_cif");
					    
		    
		    while (siter.hasNext()) {
		    	Statement s = siter.nextStatement();
		        System.out.println("http://localhost:2020/data/licencia/" + s.getSubject().getLocalName());
		        Model m2 = ModelFactory.createDefaultModel();
		        m2.read("http://localhost:2020/data/licencia/" + s.getSubject().getLocalName(), "RDF/XML");
				ResIterator iter = m2.listResourcesWithProperty(lat);
				Resource r;
				
				while (iter.hasNext()) {
					Element company = doc.createElement("company");
					r = iter.nextResource();
					System.out.println("Recurso: " + r.getURI());
					System.out.println("\tNombre: " + r.getProperty(name).getObject().toString());
//					new Double(r.getProperty(lat).getDouble()).toString()
					company.appendChild(doc.createElement("name")).appendChild(doc.createTextNode(r.getProperty(name).getObject().toString()));
					
					System.out.println("\tLatitud: " + new Double(r.getProperty(lat).getDouble()).toString());
					company.appendChild(doc.createElement("lat")).appendChild(doc.createTextNode(new Double(r.getProperty(lat).getDouble()).toString()));
					
					System.out.println("\tLongitud: " + new Float(r.getProperty(lng).getFloat()).toString());
					company.appendChild(doc.createElement("long")).appendChild(doc.createTextNode(new Float(r.getProperty(lng).getFloat()).toString()));
					
					System.out.println("\tInformacion: " + r.getProperty(info).getObject().toString());
					company.appendChild(doc.createElement("info")).appendChild(doc.createTextNode(r.getProperty(info).getObject().toString()));
					
					companies.appendChild(company);
				}				
		    }
			doc.appendChild(companies);		    
		} else {
		    System.out.println("No statements were found in the database");
		}

		// Mostramos el resultado por la consola
		System.out.println(doc.toString());
		StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc), result);
        String strXml = writer.toString();
        System.out.println(strXml);
		
//		m.write(System.out, "N-TRIPLE");;
		
        
        // Devolvemos el XML generado al cliente
		return strXml;
//		return "prueba";
	}

	public Model createRdfModel() {
		Model m = ModelFactory.createDefaultModel();
		// Creamos algunas propiedades que vamos a usar
		lat = m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#", "lat");
		lng = m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#", "long");
		info = m.createProperty("http://risp.labis.org/company#", "info");
		
		// Definimos la información de la primera empresa
		String companyURI = "http://risp.labis.org/Empresa1";
		String companyName = "McDonald's";
		String latitud = "28.460757";
		String longitud = "-16.306085";
		String information = "Información sobre McDonald's...";
		
		// Creamos el recurso de la primera empresa
		m.createResource(companyURI)
			.addProperty(VCARD.FN, companyName)
			.addProperty(lat, latitud)
			.addProperty(lng, longitud)
			.addProperty(info, information);
		
		// Definimos la información de la segunda empresa
		companyURI = "http://risp.labis.org/Empresa2";
		companyName = "Guarapo";
		latitud = "28.458595";
		longitud = "-16.301445";
		information = "Información sobre Guarapo...";
		
		// Creamos el recurso de la primera empresa
		m.createResource(companyURI)
			.addProperty(VCARD.FN, companyName)
			.addProperty(lat, latitud)
			.addProperty(lng, longitud)
			.addProperty(info, information);
		
		// Devolvemos el modelo
		return m;
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
	
	
	public Street[] getStreets(LatLong topRight, LatLong BottonLeft) {
		
		String url = "jdbc:mysql://localhost/risp";
		String user = "root";
		String passw = "labis";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection cnt = DriverManager.getConnection(url, user, passw);
			java.sql.Statement stm = cnt.createStatement();
			ResultSet rs = stm.executeQuery("SELECT CODIGO, COORDX, COORDY FROM HOJA1");
			while (rs.next()){
				//System.out.println("CODIGO: " + rs.getLong("CODIGO") + "  LONGITUD: " + rs.getInt("LATITUD"));
				double x = Double.valueOf(rs.getString("COORDX").replace(',', '.')).doubleValue();
				double y = Double.valueOf(rs.getString("COORDY").replace(',', '.')).doubleValue();
				double latlong[] = converter(x, y);
				String sql = "UPDATE HOJA1 SET LATITUD = ?, LONGITUD = ?, X = ?, Y = ? WHERE CODIGO = ?";
				PreparedStatement s = cnt.prepareStatement(sql);
				s.setDouble(1, latlong[0]);
				s.setDouble(2, latlong[1]);
				s.setDouble(3, x);
				s.setDouble(4, y);
				s.setLong(5, rs.getLong("CODIGO"));
				s.executeUpdate();
			}
			
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
		int n = 30;
		int pMax = 400;
		Street[] streets = new Street[n];
		for (int i = 0; i < n; i++){
			double lat = Math.random() * (topRight.getLatitude() - BottonLeft.getLatitude()) + BottonLeft.getLatitude(); 
			double lng = Math.random() * (topRight.getLongitude() - BottonLeft.getLongitude()) + BottonLeft.getLongitude();
			int p = new Double(Math.random() * pMax).intValue();
			streets[i] = new Street(i, new LatLong(lat, lng), p, p / 10 + 1, 'N', "nombre de la calle");
		}
		return streets;
	}

	public Street getStreet(LatLong place) {
		int pMax = 400;
		int p = new Double(Math.random() * pMax).intValue();
		return new Street(69, place, p, p / 10 + 1, 'I', "nombre de la calle");
	}
	
}
