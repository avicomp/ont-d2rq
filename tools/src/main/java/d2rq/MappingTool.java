package d2rq;

import d2rq.utils.ArgDecl;
import d2rq.utils.CommandLine;
import de.fuberlin.wiwiss.d2rq.SystemLoader;
import de.fuberlin.wiwiss.d2rq.map.MapParser;
import de.fuberlin.wiwiss.d2rq.map.Mapping;
import de.fuberlin.wiwiss.d2rq.mapgen.MappingGenerator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Command line interface for {@link MappingGenerator}.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MappingTool extends CommandLineTool {
    private final static Logger LOGGER = LoggerFactory.getLogger(MappingTool.class);

    MappingTool(PrintStream console) {
        super(console);
    }

    @Override
    public void usage() {
        console.println("usage: generate-mapping [options] jdbcURL");
        console.println();
        printStandardArguments(false);
        console.println("  Options:");
        printConnectionOptions();
        console.println("    -o outfile.ttl  Output file name (default: stdout)");
        console.println("    -v              Generate RDFS+OWL vocabulary instead of mapping file");
        console.println("    -b baseURI      Base URI for RDF output");
        console.println("    --verbose       Print debug information");
        console.println();
        throw new Exit(1);
    }

    private ArgDecl baseArg = new ArgDecl(true, "b", "base");
    private ArgDecl outfileArg = new ArgDecl(true, "o", "out", "outfile");
    private ArgDecl vocabAsOutput = new ArgDecl(false, "v", "vocab");

    @Override
    public void initArgs(CommandLine cmd) {
        cmd.add(baseArg);
        cmd.add(outfileArg);
        cmd.add(vocabAsOutput);
    }

    @Override
    public void run(CommandLine cmd, SystemLoader loader) throws IOException {
        if (cmd.numItems() == 1) {
            loader.setJdbcURL(cmd.getItem(0));
        }

        PrintStream out;
        if (cmd.contains(outfileArg)) {
            File f = new File(cmd.getArg(outfileArg).getValue());
            LOGGER.info("Writing to " + f);
            loader.setSystemBaseURI(MapParser.absolutizeURI(f.toURI().toString() + "#"));
            out = new PrintStream(new FileOutputStream(f));
        } else {
            LOGGER.info("Writing to stdout");
            out = System.out;
        }
        if (cmd.hasArg(baseArg)) {
            loader.setSystemBaseURI(cmd.getArg(baseArg).getValue());
        }

        Mapping generator = loader.build();
        try {
            Model model = cmd.contains(vocabAsOutput) ? generator.getVocabularyModel() : generator.asModel();
            RDFDataMgr.write(out, model, RDFLanguages.TURTLE);
        } finally {
            loader.close();
        }
    }
}