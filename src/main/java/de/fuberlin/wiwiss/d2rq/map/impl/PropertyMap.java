package de.fuberlin.wiwiss.d2rq.map.impl;

import de.fuberlin.wiwiss.d2rq.D2RQException;
import de.fuberlin.wiwiss.d2rq.algebra.Relation;
import de.fuberlin.wiwiss.d2rq.map.Database;

/**
 * @author J&ouml;rg Hen&szlig;
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
@SuppressWarnings("WeakerAccess")
public class PropertyMap extends ResourceMap {
    private Database database;

    public PropertyMap(String uriPattern, Database database) {
        super(null, true);
        setURIPattern(uriPattern);
        this.database = database;
    }

    @Override
    protected Relation buildRelation() {
        return relationBuilder(database.connectedDB()).buildRelation();
    }

    @Override
    public void validate() throws D2RQException {
        // Nothing to validate
    }

    @Override
    public String toString() {
        return String.format("d2rq:dynamicProperty \"%s\"", this.uriPattern);
    }
}