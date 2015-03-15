

# Introducción #

La reutilización de la información del sector público (RISP) consta de dos partes:
  * Publicación de la información por parte de las Administraciones Públicas.
  * Reutilización de la información publicada por parte de personas físicas o jurídicas.

En este proyecto se pretende implementar las dos partes, desarrollando un producto que consuma la información publicada, para que sirva a modo de ejemplo de las posibilidades que ofrece la RISP.


# Esquema publicación - reutilización #

<img src='http://risp.googlecode.com/files/esquema_proyecto_r2.PNG' alt='Esquema de la implementación del proyecto' border='1' align='middle' width='800'>

<h1>Publicación</h1>

La primera parte consiste en la publicación de datos que las Administraciones Públicas quieran poner a disposición de los usuarios. En nuestro caso, vamos a crear una base de datos a modo de ejemplo para ilustrar cómo realizaremos la publicación de la información. Para ello, utilizaremos una base de datos MySql para crear una base de datos relacional de prueba y la utilidad <a href='http://www4.wiwiss.fu-berlin.de/bizer/d2r-server/'>D2RServer</a> que permite publicar en RDF los datos disponibles en una base de datos relacional.<br>
<br>
<h2>Creación de la base de datos</h2>

Para crear la base de datos utilizamos la herramienta <a href='http://www.appservnetwork.com/'>AppServ</a> que facilita la instalación de Apache, MySQL y PHP y en la cual estas aplicaciones se configuran en forma automática.<br>
<br>
Como primera aproximación creamos una base de datos llamada “licencias”, que contiene una única tabla llamada “licencia”. Dicha tabla consta de 6 campos para recoger la información referente a las mismas:<br>
<br>
<ul><li>C.I.F: Código de Identificación Fiscal. Se trata de un atributo formado por una letra seguida de ocho números, por ello el tipo de datos que elegimos para este campo es varchar(9).<br>
</li><li>nombreEmpresa: Nombre de la empresa que ha solicitado la licencia. El tipo de datos elegido es varchar(30).<br>
</li><li>LocalizacionLat: Latitud de la coordenada a través de la cual podemos geolocalizar el lugar en el que se ha solicitado la licencia. Se trata de un atributo de tipo float.<br>
</li><li>LocalizacionLog: Longitud de la coordenada a través de la cual podemos geolocalizar el lugar en el que se ha solicitado la licencia. Se trata de un atributo de tipo float.<br>
</li><li>fechaLicencia: Fecha en que se ha solicitado la licencia. Se trata de un atributo de tipo date.<br>
</li><li>estadoLicencia: Estado en el que se encuentra la tramitación de la licencia: en trámite o resuelta.</li></ul>

<h2>Conexión entre la base de datos y D2R Server</h2>

<a href='http://www4.wiwiss.fu-berlin.de/bizer/d2r-server/'>D2R Server</a> es una herramienta para publicar bases de datos relacionales en la Web Semántica. Permite a los exploradores HTML y RDF navegar por el contenido de la base de datos y, además, permite a las aplicaciones realizar consultas a la base de datos usando el lenguaje SPARQL, todo ello mediante un mapeo directo sobre la base de datos relacional.<br>
<br>
Los pasos a realizar para conectar la base de datos con D2RServer son (una descripción más detallada está disponible en la página oficial de D2RServer, en la sección Quick Start):<br>
<ul><li>Descargar <a href='http://www4.wiwiss.fu-berlin.de/bizer/d2r-server/'>D2RServer</a> y descomprimirlo.<br>
</li><li>Si no vamos a utilizar MySql, tendremos que descargarnos un driver JDBC para la base de datos que vayamos a usar. En nuestro caso, este paso no es necesario.<br>
</li><li>Abrimos una consola y nos ponemos dentro de la carpeta de <i>D2RServer</i>, donde está el script <i>generate-mapping</i>.<br>
</li><li>Para generar el fichero de mapeo .n3 escribimos:<br>
</li></ul><blockquote><code> generate-mapping -o mapping.n3 -u root -p contraseña jdbc:mysql://127.0.01/licencias </code>
</blockquote><ul><li>Si queremos volcar en un único fichero RDF toda la base de datos escribimos:<br>
</li></ul><blockquote><code> drump-rdf -m mapping.n3 -o fichero.rdf </code></blockquote>


<h1>Reutilización</h1>

Las aplicaciones web que mezclan datos de distintas fuentes para crear nuevo contenido se denominan <i>mashups</i> (<a href='http://es.wikipedia.org/wiki/Mashup_%28aplicaci%C3%B3n_web_h%C3%ADbrida%29'>Wikipedia</a>). Para la reutilización de los datos publicados por este proyecto se ha decidido desarrollar un mashup, implementado mediante Google Web Toolkit y Google Maps para crear la interfaz de usuario de la aplicación web, apoyado en el uso de Jena en el lado del servidor web para consumir los datos publicados en RDF.<br>
<br>
Nota: esta parte del proyecto puede llevar a confusión por aparentar ser un paso necesario en la publicación de información del sector público; no hay que olvidar que la RISP consta de dos partes bien diferenciados y que, por lo general, están desarrolladas por partes distintas (las AA.PP. publican información; las personas o empresas reutilizan esa información). Por tanto, esta parte del proyecto consiste en la reutilización de la información publicada anteriormente en formato RDF, por medio de una aplicación web totalmente independiente de la parte de publicación de información (que contará con su propio servidor web, al cual nos referiremos en la mayoría de los casos simplemente como "servidor").<br>
<br>
<h2>Detalles de implementación del mashup</h2>

Como se ha mencionado anteriormente, hemos optado por utilizar Google Web Toolkit (GWT) para desarrollar la aplicación web. GWT permite desarrollar y mantener aplicaciones JavaScript, programando en Java. No es objeto de este proyecto hacer un tutorial de GWT, por lo que si quieres saber más sobre su uso, te remitimos a la <a href='http://code.google.com/intl/es-ES/webtoolkit/'>página oficial de GWT</a>. Sólo recordar que si vas a utilizar Eclipse como IDE, no hace falta que descargues GWT desde su página web, mejor utiliza el <a href='http://code.google.com/intl/es-ES/eclipse/'>complemento de Google para Eclipse</a>.<br>
<br>
GWT además permite una cómoda integración con el API de Google Maps, mediante la <a href='http://code.google.com/p/gwt-google-apis/wiki/MapsGettingStarted'>librería de Google Maps para GWT</a>. Esta librería es muy útil en nuestro caso, ya que queremos representar datos georeferenciados.<br>
<br>
Por otro lado, para procesar los datos publicados en RDF se ha utilizado el <a href='http://jena.sourceforge.net/'>framework para web semántica Jena</a>. Si quieres conocer más sobre las posiblidades que ofrece Jena y cómo utilizarlo, te recomendamos que accedas a su <a href='http://jena.sourceforge.net/tutorial/RDF_API/index.html'>tutorial</a>. Si no tienes mucha experiencia usando Jena, Eclipse o los dos, un buen punto de partida es este <a href='http://www.iandickinson.me.uk/articles/jena-eclipse-helloworld/'>tutorial para principiantes sobre Jena con Eclipse</a>.<br>
<br>
Una vez introducidas las librerías que vamos a utilizar, pasamos a describir la estructura básica de nuestra aplicación GWT:<br>
<ul><li>Lado del cliente (GWT + Google Maps API)<br>
</li><li>Lado del servidor (GWT + Jena)</li></ul>

<h3>Lado del cliente</h3>

La interfaz de usuario web del cliente consistirá en una interfaz muy sencilla que muestra la información en un mapa, de manera conveniente para que sea legible y útil por parte del usuario.<br>
<br>
Desde el punto de vista de la programación, la aplicación de prueba que hemos desarrollado sólo contiene el título de la página web y un mapa de Google Maps con distintos puntos marcados en él, que señalan distintas empresas ficticias ubicadas en Santa Cruz de Tenerife y San Cristóbal de La Laguna.<br>
<br>
Esta información la obtiene desde el servidor web, mediante una petición RPC en la que el servidor le devuelve una cadena de caracteres que contiene un XML con la información a representar en el mapa. Este XML contiene:<br>
<ul><li>Nombre de la empresa<br>
</li><li>Ubicación de la empresa (representada en latitud/longitud)<br>
</li><li>CIF<br>
</li><li>Estado de la licencia de obra<br>
</li><li>Información adicional</li></ul>

Haciendo un recorrido (<i>parsing</i>) del XML devuelto mediante los <a href='http://code.google.com/intl/es-ES/webtoolkit/doc/1.6/DevGuideCodingBasics.html#DevGuideXML'>métodos que ofrece GWT</a> es muy sencillo representar esa información en el mapa utilizando la librería de Google Maps para GWT.<br>
<br>
<h3>Lado del servidor</h3>

Desde el punto del servidor sólo tenemos una clase que implementa una <a href='http://code.google.com/intl/es-ES/webtoolkit/doc/1.6/DevGuideServerCommunication.html#DevGuideRemoteProcedureCalls'>RPC de GWT</a>. Dentro de la misma tendremos un método que será llamado por el cliente para obtener el XML mencionado anteriormente.<br>
<br>
Para generar dicho XML se tiene que obtener la información que se ha publicado en RDF en la parte de <b>publicación</b> de este proyecto. Para obtener el RDF se ha utiliado el framework Jena, que nos proporciona un método que puede leer un RDF desde una URL y crear un modelo a partir del mismo.<br>
<br>
Una vez creado el modelo, se leen los recursos que tengan la propiedad <i>lat</i>, que representa la latitud de la ubicación de una empresa (esto no nos asegura que esté presente la longitud como propiedad, pero es presumible que lo estará). Si se desea conocer más detalles sobre modelos, recursos y propiedades de Jena, es recomendable leer el <a href='http://jena.sourceforge.net/tutorial/RDF_API/index.html'>tutorial de su página oficial</a>. Procesamos cada recurso que tenga esa propiedad y vamos generando el XML correspondiente a ese recurso, con la información que describimos en el apartado anterior. Finalmente, se transforma el XML generado a String y se devuelve al cliente.<br>
<br>
Para saber cómo generar XML en Java y convertirlo a String podemos utilizar este <a href='http://www.rgagnon.com/javadetails/java-0530.html'>tutorial</a>.