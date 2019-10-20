# ONT-D2RQ – A Database to OWL Mapper (API and Tools)
## This is a modified fork of D2RQ (https://github.com/d2rq/d2rq).

## Notice
**The activity in this repository is frozen.
The new project's home is https://github.com/owlcs/ont-d2rq**

There are following major differences with the original:

* It is a maven project while the original is ant
* Up-to-date dependencies (java 8, Apache Jena 3.x, etc)
* OWL2 support
* A Fuseki (SPARQLer) based embedded server instead of Joseki based native D2RQ Server
* [ONT-API](https://github.com/avicomp/ont-api) (an [OWL-API](https://github.com/owlcs/owlapi) alternative implementation over [Apache Jena](https://github.com/apache/jena))

## Usage
* To build: `mvn clean package`
* To run tools: `$ java -jar tools\target\d2rq.jar`
* To include in dependencies [jitpack.io](https://jitpack.io/) can be used

## Notes and propositions
* For ONT-API integration there is a special kind of `OWLDocumentSource`: `ru.avicomp.d2rq.D2RQGraphDocumentSource` 
* It is also assumed that `ru.avicomp.d2rq.MappingFilter` can be used to filter the default database schema
* [ONT-MAP](https://github.com/avicomp/ont-map) can be used to transform DB RDF data into a more suitable form

## Tests
For running tests please configure postgres and mysql databases. 
DB sql-dumps to prepare environment are located in the `doc/example` directory.
Also, there is a property-file with test db-settings: `api/src/test/resources/db.properties`
 
## License 
Apache License Version 2.0