package ru.avicomp.ontapi;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.semanticweb.owlapi.model.*;

import ru.avicomp.ontapi.jena.model.OntOPE;
import ru.avicomp.ontapi.jena.model.OntPE;
import ru.avicomp.ontapi.utils.ReadWriteUtils;

/**
 * Test loading filtered db scheme.
 * <p>
 * Created by @szuev on 25.02.2017.
 */
@RunWith(Parameterized.class)
public class FilterTest {
    private static final Logger LOGGER = Logger.getLogger(FilterTest.class);
    private ONTAPITests.ConnectionData data;

    public FilterTest(ONTAPITests.ConnectionData data) {
        this.data = data;
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<ONTAPITests.ConnectionData> getData() {
        return ONTAPITests.ConnectionData.asList();
    }

    @Test
    public void testLoad() throws Exception {
        LOGGER.info("Load full db schema from " + data);
        D2RQGraphDocumentSource source1 = data.toDocumentSource();
        OntologyManager m = OntManagers.createONT();
        OntologyModel o1 = (OntologyModel) m.loadOntologyFromOntologyDocument(source1);
        o1.axioms().forEach(LOGGER::debug);
        source1.close();

        LOGGER.info("Load the restricted model from db (property constraints)");
        OWLDataProperty dp = o1.dataPropertiesInSignature().findAny().orElseThrow(() -> new AssertionError("Can't find any data property."));
        OWLObjectProperty op = o1.objectPropertiesInSignature().findAny().orElseThrow(() -> new AssertionError("Can't find any object property."));

        MappingFilter filter1 = MappingFilter.create(dp, op);
        LOGGER.debug("Constraint properties: " + filter1.properties().collect(Collectors.toList()));
        D2RQGraphDocumentSource source2 = source1.filter(filter1);
        OWLOntologyID id2 = new OWLOntologyID(IRI.create("http://d2rq.example.com"), IRI.create("http://d2rq.example.com/version/1.0"));
        OntologyModel o2 = (OntologyModel) m.loadOntologyFromOntologyDocument(source2);
        o2.applyChange(new SetOntologyID(o2, id2));
        ReadWriteUtils.print(o2.asGraphModel());
        Assert.assertEquals("Expected two ontologies", 2, m.ontologies().count());
        Assert.assertNotNull("Can't find " + id2, m.contains(id2));
        Assert.assertNotEquals(o1.asGraphModel().getBaseGraph(), o2.asGraphModel().getBaseGraph());
        Assert.assertEquals("Expected two classes", 2, o2.asGraphModel().listClasses().count());
        Assert.assertEquals("Expected one data property", 1, o2.asGraphModel().listDataProperties().count());
        Assert.assertEquals("Expected one object property", 1, o2.asGraphModel().listObjectProperties().count());

        LOGGER.info("Load the restricted model from db (class constraints)");
        OntOPE p = o1.asGraphModel().listObjectProperties().findAny().orElseThrow(() -> new AssertionError("Can't find any ont object property."));
        IRI class1 = p.range().map(Resource::getURI).map(IRI::create).findAny().orElseThrow(() -> new AssertionError("Can't find range for " + p));
        IRI class2 = p.domain().map(Resource::getURI).map(IRI::create).findAny().orElseThrow(() -> new AssertionError("Can't find domain for " + p));
        MappingFilter filter2 = MappingFilter.create().includeClass(class1).includeClass(class2);
        LOGGER.debug("Constraint classes: " + filter2.classes().collect(Collectors.toList()));
        D2RQGraphDocumentSource source3 = source1.filter(filter2);
        OWLOntologyID id3 = new OWLOntologyID(IRI.create("http://d2rq.example.com"), IRI.create("http://d2rq.example.com/version/2.0"));
        OntologyModel o3 = (OntologyModel) m.loadOntologyFromOntologyDocument(source3);
        o3.applyChange(new SetOntologyID(o3, id3));
        ReadWriteUtils.print(o3.asGraphModel());
        Assert.assertEquals("Expected three ontologies", 3, m.ontologies().count());
        Assert.assertNotNull("Can't find " + id3, m.contains(id3));
        Assert.assertEquals("Expected two classes", 2, o3.asGraphModel().listClasses().count());
        List<OntPE> props = o3.asGraphModel().ontObjects(OntPE.class).collect(Collectors.toList());
        props.forEach(LOGGER::debug);
        Assert.assertFalse("No properties:", props.isEmpty());
    }

}