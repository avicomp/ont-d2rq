package de.fuberlin.wiwiss.d2rq.functional_tests;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DC;

import de.fuberlin.wiwiss.d2rq.D2RQTestSuite;
import de.fuberlin.wiwiss.d2rq.jena.ModelD2RQ;
import de.fuberlin.wiwiss.d2rq.map.MappingFactory;
import junit.framework.TestCase;

/**
 * Functional tests that exercise a ModelD2RQ by calling Model API functions. For
 * notes on running the tests, see {@link AllTests}.
 * <p>
 * To see debug information, uncomment the enableDebug() call in the setUp() method.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ModelAPITest extends TestCase {
    private ModelD2RQ model;

    protected void setUp() throws Exception {
        this.model = MappingFactory.load(D2RQTestSuite.ISWC_MAP, "TURTLE", "http://test/").getDataModel();
//		this.model.enableDebug();
    }

    protected void tearDown() throws Exception {
        this.model.close();
    }

    public void testListStatements() {
        StmtIterator iter = this.model.listStatements();
        int count = 0;
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            stmt.toString();
//			dumpStatement(stmt);
            count++;
        }
        assertEquals(358, count);
        //assertEquals(322, count);
    }

    public void testHasProperty() {
        assertTrue(this.model.getResource("http://test/papers/1").hasProperty(DC.creator));
    }

    void dumpStatement(Statement stmt) {
        Resource subject = stmt.getSubject();
        Property predicate = stmt.getPredicate();
        RDFNode object = stmt.getObject();
        System.out.print(subject + " " + predicate + " ");
        if (object instanceof Resource) {
            System.out.print(object);
        } else { // object is a literal
            System.out.print(" \"" + object + "\"");
        }
        System.out.println(" .");
    }
}