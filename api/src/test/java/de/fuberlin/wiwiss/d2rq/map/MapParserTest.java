package de.fuberlin.wiwiss.d2rq.map;

import de.fuberlin.wiwiss.d2rq.D2RQException;
import de.fuberlin.wiwiss.d2rq.D2RQTestHelper;
import de.fuberlin.wiwiss.d2rq.helpers.MappingTestHelper;
import de.fuberlin.wiwiss.d2rq.vocab.D2RQ;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Assert;
import org.junit.Test;
import ru.avicomp.ontapi.jena.vocabulary.OWL;

/**
 * Test {@link MapParser}.
 * Created by @ssz on 06.10.2018.
 */
public class MapParserTest {

    @Test
    public void testFixLegacyPropertyBridges() {
        String ns = "http://x#";
        String column = "Persons.URI";
        String pattern = ns + "@@Persons.Type@@";

        Model m = ModelFactory.createDefaultModel()
                .setNsPrefixes(MappingFactory.MAPPING).setNsPrefix("test", ns);
        Resource op = m.createResource(ns + "op", MapParser.LegacyD2RQ.ObjectPropertyBridge)
                .addProperty(D2RQ.column, column).addProperty(D2RQ.pattern, pattern);
        Resource dp = m.createResource(ns + "dp", MapParser.LegacyD2RQ.DataPropertyBridge);
        D2RQTestHelper.print(m);

        Mapping map = MappingFactory.wrap(m);
        Assert.assertEquals(0, map.listPropertyBridges().count());

        long size = m.size();
        MapParser.fixLegacyPropertyBridges(m);

        D2RQTestHelper.print(map.asModel());
        Assert.assertEquals(size, m.size());
        Assert.assertFalse(m.containsResource(MapParser.LegacyD2RQ.DataPropertyBridge));
        Assert.assertFalse(m.containsResource(MapParser.LegacyD2RQ.ObjectPropertyBridge));
        Assert.assertEquals(2, map.listPropertyBridges().count());
        PropertyBridge opb = MappingTestHelper.findPropertyBridge(map, op);
        PropertyBridge dpb = MappingTestHelper.findPropertyBridge(map, dp);
        Assert.assertNull(opb.getPattern());
        Assert.assertNull(opb.getColumn());
        Assert.assertNull(dpb.getPattern());
        Assert.assertNull(dpb.getColumn());
        Assert.assertEquals(column, opb.getURIColumn());
        Assert.assertEquals(pattern, opb.getURIPattern());
        Assert.assertNull(dpb.getURIColumn());
        Assert.assertNull(dpb.getURIPattern());

        MapParser.fixLegacyPropertyBridges(m);
        Assert.assertEquals(size, m.size());

    }

    @Test
    public void testFixLegacyAdditionalProperties() {
        String schemaNS = "http://x#";
        String mapNS = "http://y#";
        Resource iri = ResourceFactory.createResource("http://annotation.semanticweb.org/iswc2003/");

        Model m = ModelFactory.createDefaultModel()
                .setNsPrefixes(MappingFactory.MAPPING)
                .setNsPrefix("map", mapNS)
                .setNsPrefix("", schemaNS);
        Resource a = m.createResource(mapNS + "SeeAlsoStatement", D2RQ.AdditionalProperty)
                .addProperty(D2RQ.propertyName, RDFS.seeAlso)
                .addProperty(D2RQ.propertyValue, iri);
        Resource c = m.createResource(mapNS + "PersonsClassMap", D2RQ.ClassMap)
                .addProperty(D2RQ.clazz, m.createResource(schemaNS + "Person"))
                .addProperty(MapParser.LegacyD2RQ.additionalProperty, a);

        D2RQTestHelper.print(m);
        Mapping map = MappingFactory.wrap(m);
        Assert.assertEquals(1, map.listClassMaps().count());
        Assert.assertEquals(1, map.listAdditionalProperties().count());
        Assert.assertEquals(0, map.listPropertyBridges().count());

        long size = m.size();
        MapParser.fixLegacyAdditionalProperty(m);

        Assert.assertEquals(size + 3, m.size());
        Assert.assertFalse(m.containsResource(MapParser.LegacyD2RQ.additionalProperty));
        D2RQTestHelper.print(m);
        Assert.assertEquals(1, map.listClassMaps().count());
        Assert.assertEquals(1, map.listAdditionalProperties().count());
        Assert.assertEquals(1, map.listPropertyBridges().count());

        ClassMap cm = MappingTestHelper.findClassMap(map, c);
        Assert.assertEquals(1, cm.listPropertyBridges().count());

        PropertyBridge p = map.listPropertyBridges().findFirst().orElseThrow(AssertionError::new);
        Assert.assertEquals(p, cm.listPropertyBridges().findFirst().orElseThrow(AssertionError::new));
        Assert.assertEquals(iri, p.getConstantValue());
        Assert.assertEquals(RDFS.seeAlso, p.listProperties().findFirst().orElseThrow(AbstractMethodError::new));

        MapParser.fixLegacyAdditionalProperty(m);
        Assert.assertEquals(size + 3, m.size());
    }

    @Test
    public void testFixLegacyReferences() {
        String schemaNS = "http://x#";
        String mapNS = "http://y#";

        Model m = ModelFactory.createDefaultModel()
                .setNsPrefixes(MappingFactory.MAPPING)
                .setNsPrefix("owl", OWL.NS)
                .setNsPrefix("map", mapNS)
                .setNsPrefix("", schemaNS);

        Resource mc = m.createResource(mapNS + "PersonsClassMap", D2RQ.ClassMap);
        Resource mp = m.createResource(mapNS + "SeeAlsoBridge", D2RQ.PropertyBridge);
        Resource sc = m.createResource(schemaNS + "SomeClass", OWL.Class)
                .addProperty(MapParser.LegacyD2RQ.classMap, mc);
        Resource sp = m.createResource(schemaNS + "SomeProperty", OWL.DatatypeProperty)
                .addProperty(MapParser.LegacyD2RQ.propertyBridge, mp);

        D2RQTestHelper.print(m);

        Mapping map = MappingFactory.wrap(m);
        ClassMap cm = MappingTestHelper.findClassMap(map, mc);
        PropertyBridge pb = MappingTestHelper.findPropertyBridge(map, mp);
        Assert.assertEquals(1, map.listClassMaps().count());
        Assert.assertEquals(1, map.listPropertyBridges().count());
        Assert.assertEquals(0, cm.listClasses().count());
        Assert.assertEquals(0, pb.listProperties().count());

        long size = m.size();
        MapParser.fixLegacyReferences(m);

        Assert.assertEquals(size, m.size());
        Assert.assertFalse(m.containsResource(MapParser.LegacyD2RQ.classMap));
        Assert.assertFalse(m.containsResource(MapParser.LegacyD2RQ.propertyBridge));
        D2RQTestHelper.print(m);

        Assert.assertEquals(1, map.listClassMaps().count());
        Assert.assertEquals(1, map.listPropertyBridges().count());
        Assert.assertEquals(1, cm.listClasses().count());
        Assert.assertEquals(1, pb.listProperties().count());
        Assert.assertEquals(sc, cm.listClasses().findFirst().orElseThrow(AssertionError::new));
        Assert.assertEquals(sp, pb.listProperties().findFirst().orElseThrow(AssertionError::new));

        MapParser.fixLegacyReferences(m);
        Assert.assertEquals(size, m.size());

    }

    @Test(expected = D2RQException.class)
    public void validateDistinctMembers() {
        Mapping m = MappingFactory.create()
                .createDatabase("x").getMapping()
                .createClassMap("x").getMapping();
        MapParser.checkDistinctMapObjects(m.asModel());
    }

    @Test(expected = D2RQException.class)
    public void validateExternalResources() {
        Model m = ModelFactory.createDefaultModel();
        m.createResource("x", m.createResource(D2RQ.NS + "XClass"));
        MapParser.checkVocabulary(m);
    }
}