package ru.avicomp.ontapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.semanticweb.owlapi.model.IRI;

import ru.avicomp.ontapi.jena.Hybrid;
import ru.avicomp.ontapi.jena.impl.OntIndividualImpl;
import ru.avicomp.ontapi.jena.impl.configuration.*;
import ru.avicomp.ontapi.jena.model.OntCE;
import ru.avicomp.ontapi.jena.model.OntIndividual;
import ru.avicomp.ontapi.jena.model.OntStatement;
import ru.avicomp.ontapi.jena.vocabulary.OWL;
import ru.avicomp.ontapi.jena.vocabulary.RDF;

/**
 * Helper class to provide common methods to test D2RQ + ONT API.
 * <p>
 * Created by @szuev on 23.02.2017.
 */
public abstract class ONTAPITests {

    /**
     * Returns the new {@link OntPersonality} based on {@link OntModelConfig#ONT_PERSONALITY_LAX}.
     * The difference is that it does not require owl:NamedIndividual declaration for named individuals.
     * <p>
     * Note!
     * This ont-personality contains factories which link to the initial individual factory
     * (with the requirement for an explicit owl:NamedIndividual type):
     * the following objects are not patched:
     * - {@link ru.avicomp.ontapi.jena.model.OntDisjoint},
     * - {@link ru.avicomp.ontapi.jena.model.OntDisjoint.Individuals},
     * - {@link ru.avicomp.ontapi.jena.model.OntSWRL.IArg}
     * - {@link ru.avicomp.ontapi.jena.model.OntSWRL.Arg}
     * But it is okay since we are not going to use it anywhere expect these tests.
     *
     * @return {@link OntPersonality}
     */
    public static OntPersonality createD2RQPersonality() {
        return createD2RQPersonality(OntModelConfig.ONT_PERSONALITY_LAX);
    }

    private static OntPersonality createD2RQPersonality(OntPersonality from) {
        OntPersonality res = from.copy();
        OntObjectFactory ce = res.getOntImplementation(OntCE.class);
        OntObjectFactory anonymous = res.getOntImplementation(OntIndividual.Anonymous.class);

        OntObjectFactory named = createNamedIndividualFactory(ce);
        OntObjectFactory all = new MultiOntObjectFactory(OntFinder.ANY_SUBJECT, null, named, anonymous);
        res.register(OntIndividual.Named.class, named)
                .register(OntIndividual.class, all);
        return res;
    }

    private static OntObjectFactory createNamedIndividualFactory(OntObjectFactory ce) {
        OntMaker maker = new OntMaker.Default(IndividualImpl.class);
        OntFinder finder = new OntFinder.ByPredicate(RDF.type);
        OntFilter filter = OntFilter.URI
                .and(new OntFilter.HasPredicate(RDF.type))
                .and((s, g) -> Iter.asStream(g.asGraph().find(s, RDF.type.asNode(), Node.ANY)).map(Triple::getObject)
                        .anyMatch(o -> ce.canWrap(o, g)));
        return new CommonOntObjectFactory(maker, finder, filter);
    }

    public enum ConnectionData {
        /**
         * to set up use <a href='file:doc/example/iswc-mysql.sql'>iswc-mysql.sql</a>
         */
        MYSQL,
        /**
         * to set up use <a href='file:doc/example/iswc-postgres.sql'>iswc-postgres.sql</a>
         */
        POSTGRES,;

        private static final Properties PROPERTIES = load("/db.properties");

        public IRI getIRI() {
            return IRI.create(PROPERTIES.getProperty(prefix() + "uri"));
        }

        public String getUser() {
            return PROPERTIES.getProperty(prefix() + "user");
        }

        public String getPwd() {
            return PROPERTIES.getProperty(prefix() + "password");
        }

        private String prefix() {
            return String.format("%s.", name().toLowerCase());
        }

        public D2RQGraphDocumentSource toDocumentSource() {
            return new D2RQGraphDocumentSource(getIRI(), getUser(), getPwd());
        }

        public IRI toIRI(String uri) {
            return IRI.create(MYSQL.equals(this) ? uri : uri.toLowerCase());
        }

        public static List<ConnectionData> asList() {
            return Arrays.asList(values());
        }

        public static Properties load(String file) {
            Properties res = new Properties();
            try (InputStream in = ConnectionData.class.getResourceAsStream(file)) {
                res.load(in);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return res;
        }
    }

    public static class IndividualImpl extends OntIndividualImpl.NamedImpl {
        public IndividualImpl(Node n, EnhGraph m) {
            super(n, m);
        }

        public OntStatement getRoot() {
            OntStatement res = getRoot(RDF.type, OWL.NamedIndividual);
            return res == null ? types().map(r -> getRoot(RDF.type, r)).findFirst().orElse(null) : res;
        }
    }

    public static Graph switchTo(OntologyModel model, Class<? extends Graph> view) {
        Graph chosen = select(model, view);
        Graph prev = ((Hybrid) model.asGraphModel().getBaseGraph()).switchTo(chosen);
        // since content is changed reset all caches:
        model.clearCache();
        return prev;
    }

    public static Graph select(OntologyModel model, Class<? extends Graph> view) {
        Graph graph = model.asGraphModel().getBaseGraph();
        return graph instanceof Hybrid ? ((Hybrid) graph).graphs().filter(view::isInstance).findFirst().orElse(null) : null;
    }
}