package de.fuberlin.wiwiss.d2rq.jena;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.graph.Capabilities;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.GraphBase;
import org.apache.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.d2rq.D2RQException;
import de.fuberlin.wiwiss.d2rq.engine.QueryEngineD2RQ;
import de.fuberlin.wiwiss.d2rq.find.FindQuery;
import de.fuberlin.wiwiss.d2rq.find.TripleQueryIter;
import de.fuberlin.wiwiss.d2rq.map.Mapping;
import de.fuberlin.wiwiss.d2rq.pp.PrettyPrinter;

/**
 * A D2RQ virtual read-only Jena graph backed by a non-RDF database.
 *
 * @author Chris Bizer chris@bizer.de
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphD2RQ extends GraphBase implements Graph {
    private static final Log log = LogFactory.getLog(GraphD2RQ.class);
    private static final Capabilities capabilities = new Capabilities() {
        public boolean sizeAccurate() {
            return true;
        }

        public boolean addAllowed() {
            return addAllowed(false);
        }

        public boolean addAllowed(boolean every) {
            return false;
        }

        public boolean deleteAllowed() {
            return deleteAllowed(false);
        }

        public boolean deleteAllowed(boolean every) {
            return false;
        }

        public boolean canBeEmpty() {
            return true;
        }

        public boolean iteratorRemoveAllowed() {
            return false;
        }

        public boolean findContractSafe() {
            return false;
        }

        public boolean handlesLiteralTyping() {
            return true;
        }
    };

    static {
        QueryEngineD2RQ.register();
    }

    private final Mapping mapping;

    /**
     * Creates a new D2RQ graph from a previously prepared {@link Mapping} instance.
     *
     * @param mapping A D2RQ mapping
     * @throws D2RQException If the mapping is invalid
     */
    public GraphD2RQ(Mapping mapping) throws D2RQException {
        this.mapping = mapping;
        getPrefixMapping().setNsPrefixes(mapping.getPrefixMapping());
    }

    @Override
    public void close() {
        mapping.close();
    }

    @Override
    public Capabilities getCapabilities() {
        return capabilities;
    }

    @Override
    public ExtendedIterator<Triple> graphBaseFind(Triple triplePattern) {
        checkOpen();
        if (log.isDebugEnabled()) {
            log.debug("Find: " + PrettyPrinter.toString(triplePattern, getPrefixMapping()));
        }
        FindQuery query = new FindQuery(triplePattern, mapping.compiledPropertyBridges(), null);
        ExtendedIterator<Triple> result = TripleQueryIter.create(query.iterator());
        if (mapping.configuration().getServeVocabulary()) {
            result = result.andThen(mapping.getVocabularyModel().getGraph().find(triplePattern));
        }
        return result;
    }

    @Override
    protected void checkOpen() {
        mapping.connect();
    }

    /**
     * @return The {@link Mapping} this graph is based on
     */
    public Mapping getMapping() {
        return mapping;
    }

    /**
     * Do not allow database excursion
     *
     * @return String
     */
    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
}