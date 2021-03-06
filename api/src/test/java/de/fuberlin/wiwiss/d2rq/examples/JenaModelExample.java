package de.fuberlin.wiwiss.d2rq.examples;

import de.fuberlin.wiwiss.d2rq.map.MappingFactory;
import de.fuberlin.wiwiss.d2rq.vocab.ISWC;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDF;

public class JenaModelExample {

    public static void main(String[] args) {
        // Set up the ModelD2RQ using a mapping file
        Model m = MappingFactory.load(TestConstants.MAPPING).getDataModel();

        // Find anything with an rdf:type of iswc:InProceedings
        StmtIterator paperIt = m.listStatements(null, RDF.type, ISWC.InProceedings);

        // List found papers and print their titles
        while (paperIt.hasNext()) {
            Resource paper = paperIt.nextStatement().getSubject();
            System.out.println("Paper: " + paper.getProperty(DC.title).getString());

            // List authors of the paper and print their names
            StmtIterator authorIt = paper.listProperties(DC.creator);
            while (authorIt.hasNext()) {
                Resource author = authorIt.nextStatement().getResource();
                System.out.println("Author: " + author.getProperty(FOAF.name).getString());
            }
            System.out.println();
        }
        m.close();
    }
}
