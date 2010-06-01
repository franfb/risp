package org.labis.risp.server;

import java.io.StringWriter;

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
//import org.labis.risp.shared.FieldVerifier;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
//import com.google.gwt.xml.client.Document;
//import com.google.gwt.xml.client.Element;
//import com.google.gwt.xml.client.XMLParser;
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

		
		// Aqu� se deber�a obtener el modelo desde el servidor D2RServer
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
		
		// Definimos la informaci�n de la primera empresa
		String companyURI = "http://risp.labis.org/Empresa1";
		String companyName = "McDonald's";
		String latitud = "28.460757";
		String longitud = "-16.306085";
		String information = "Informaci�n sobre McDonald's...";
		
		// Creamos el recurso de la primera empresa
		m.createResource(companyURI)
			.addProperty(VCARD.FN, companyName)
			.addProperty(lat, latitud)
			.addProperty(lng, longitud)
			.addProperty(info, information);
		
		// Definimos la informaci�n de la segunda empresa
		companyURI = "http://risp.labis.org/Empresa2";
		companyName = "Guarapo";
		latitud = "28.458595";
		longitud = "-16.301445";
		information = "Informaci�n sobre Guarapo...";
		
		// Creamos el recurso de la primera empresa
		m.createResource(companyURI)
			.addProperty(VCARD.FN, companyName)
			.addProperty(lat, latitud)
			.addProperty(lng, longitud)
			.addProperty(info, information);
		
		// Devolvemos el modelo
		return m;
	}

	
	public Street[] getStreets(LatLong topRight, LatLong BottonLeft) {
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
