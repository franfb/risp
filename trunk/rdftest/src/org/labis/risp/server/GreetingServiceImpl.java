package org.labis.risp.server;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.labis.risp.client.GreetingService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.labis.risp.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
//import com.google.gwt.xml.client.Document;
//import com.google.gwt.xml.client.Element;
//import com.google.gwt.xml.client.XMLParser;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
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
		m.read("file:/d:/etsii/labis/rdfs/doc.rdf", "N-TRIPLE");
		
		// Creamos el documento XML que vamos a devolver al cliente
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation().createDocument(null, null, null);
		Element companies = doc.createElement("companies");
		
		name = m.createProperty("http://localhost:2020/vocab/resource/", "licencia_nombreEmpresa");
		lat = m.createProperty("http://localhost:2020/vocab/resource/", "licencia_localizacionLat");
		lng = m.createProperty("http://localhost:2020/vocab/resource/", "licencia_LocalizacionLog");
		info = m.createProperty("http://localhost:2020/vocab/resource/", "licencia_cif");
		ResIterator iter = m.listResourcesWithProperty(lat);
		Resource r;
		
		while (iter.hasNext()) {
			Element company = doc.createElement("company");
			r = iter.nextResource();
			System.out.println("Recurso: " + r.getURI());
			System.out.println("\tNombre: " + r.getProperty(name).getObject().toString());
//			new Double(r.getProperty(lat).getDouble()).toString()
			company.appendChild(doc.createElement("name")).appendChild(doc.createTextNode(r.getProperty(name).getObject().toString()));
			
			System.out.println("\tLatitud: " + new Double(r.getProperty(lat).getDouble()).toString());
			company.appendChild(doc.createElement("lat")).appendChild(doc.createTextNode(new Double(r.getProperty(lat).getDouble()).toString()));
			
			System.out.println("\tLongitud: " + new Float(r.getProperty(lng).getFloat()).toString());
			company.appendChild(doc.createElement("long")).appendChild(doc.createTextNode(new Float(r.getProperty(lng).getFloat()).toString()));
			
			System.out.println("\tInformación: " + r.getProperty(info).getObject().toString());
			company.appendChild(doc.createElement("info")).appendChild(doc.createTextNode(r.getProperty(info).getObject().toString()));
			
			companies.appendChild(company);
		}
		doc.appendChild(companies);
		
		// Mostramos el resultado por la consola
		//System.out.println(doc.toString());
		StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc), result);
        String strXml = writer.toString();
        System.out.println(strXml);
		
//		m.write(System.out, "N3");
//		Property p = m.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#", "lat");
//		System.out.println(p.toString());
		
//		m.write(System.out, "Turtle");
        
        // Devolvemos el XML generado al cliente
		return strXml;
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
	
}
