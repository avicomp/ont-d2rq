package de.fuberlin.wiwiss.d2rq.assembler;

import de.fuberlin.wiwiss.d2rq.D2RQException;
import de.fuberlin.wiwiss.d2rq.map.MappingFactory;
import de.fuberlin.wiwiss.d2rq.vocab.D2RQ;
import org.apache.jena.assembler.Assembler;
import org.apache.jena.assembler.Mode;
import org.apache.jena.assembler.assemblers.AssemblerBase;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * A Jena assembler that builds ModelD2RQs.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class D2RQAssembler extends AssemblerBase {

    @Override
    public Object open(Assembler ignore, Resource description, Mode ignore2) {
        if (!description.hasProperty(D2RQ.mappingFile)) {
            throw new D2RQException("Error in assembler specification " + description + ": missing property d2rq:mappingFile");
        }
        if (!description.getProperty(D2RQ.mappingFile).getObject().isURIResource()) {
            throw new D2RQException("Error in assembler specification " + description + ": value of d2rq:mappingFile must be a URI");
        }
        String mappingFileURI = ((Resource) description.getProperty(D2RQ.mappingFile).getObject()).getURI();
        String resourceBaseURI = null;
        Statement stmt = description.getProperty(D2RQ.resourceBaseURI);
        if (stmt != null) {
            if (!stmt.getObject().isURIResource()) {
                throw new D2RQException("Error in assembler specification " + description + ": value of d2rq:resourceBaseURI must be a URI");
            }
            resourceBaseURI = ((Resource) stmt.getObject()).getURI();
        }
        return MappingFactory.load(mappingFileURI, null, resourceBaseURI).getDataModel();
    }
}
