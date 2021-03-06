package de.fuberlin.wiwiss.d2rq.d2rq_sdb;

import de.fuberlin.wiwiss.d2rq.map.MappingFactory;
import de.fuberlin.wiwiss.d2rq.utils.JenaModelUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sdb.SDBFactory;
import org.apache.jena.sdb.Store;
import org.apache.jena.sdb.StoreDesc;
import org.apache.jena.sdb.layout2.index.StoreTriplesNodesIndexHSQL;
import org.apache.jena.sdb.sql.JDBC;
import org.apache.jena.sdb.sql.SDBConnection;
import org.apache.jena.sdb.store.DatabaseType;
import org.apache.jena.sdb.store.LayoutType;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Test for loading the sql-data in the derby-database and the turtle-data in the sdb
 *
 * @author Herwig Leimer
 */
public abstract class LoadDataTest {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LoadDataTest.class);

    // directories and data-files and config-files
    static final String CURR_DIR = JenaModelUtils.getRelativeResourcePath("/d2rq_sdb");
    private static final String DATA_DIR = "dataset";
    private static final String CONFIG_DIR = "config";
    private static final String FILENAME_TTL_DATA = "dataset.ttl.zip";
    private static final String FILENAME_SQL_DATA = "dataset.sql.zip";
    private static final String MAPPING_FILE_HSQL = "d2r-hsql-mapping.n3";
    // sdb-config
    private static final String SDB_URL = "jdbc:hsqldb:mem:sdbdata";
    private static final String SDB_USER = "sa";
    private static final String SDB_PASS = "";
    Model sdbDataModel;
    // hsql-config
    private static final String HSQL_DRIVER_NAME = "org.hsqldb.jdbcDriver";
    private static final String HSQL_URL = "jdbc:hsqldb:mem:hsqldata;create=true";
    private static final String HSQL_USER = "sa";
    private static final String HSQL_PASS = "";

    Model hsqlDataModel;


    /**
     * Constructor
     * Inits the database and loads the data
     */
    public LoadDataTest() {
        initDatabases();
    }

    /**
     * Inits the databases.
     * The init-process can be managed with the boolean
     * flags loadDerbyData and loadSDBData
     */
    private void initDatabases() {
        try {
            boolean loadHsqlData = true;
            if (loadHsqlData) {
                createHsqlDatabase();
                Assert.assertNotNull("Hsql-DataModel is not null", hsqlDataModel);
            }

            boolean loadSDBData = true;
            if (loadSDBData) {
                createSemanticDatabase();
                Assert.assertNotNull("SDBDataModel is not null", sdbDataModel);
                Assert.assertTrue("There is some data in the SDBDataModel", sdbDataModel.size() > 0);
            }
            LOGGER.debug("-----------------------------------------------------------");
        } catch (SQLException | IOException e) {
            throw new AssertionError("initDatabase", e);
        }
    }

    /**
     * Creates a new in-memory-hsql-database and puts all data that
     * the files in the zip-archive contain into.
     */
    private void createHsqlDatabase() throws IOException, SQLException {
        Connection hsqlConnection;
        File zipFile;
        ZipEntry entry;
        ZipInputStream zipInputStream;
        String sqlData;
        Statement statement;

        try {
            Class.forName(HSQL_DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e.getMessage());
        }

        hsqlConnection = DriverManager.getConnection(HSQL_URL, HSQL_USER, HSQL_PASS);

        // load all data from dataset.ttl.zip
        zipFile = new File(CURR_DIR + "/" + DATA_DIR + "/" + FILENAME_SQL_DATA);
        zipInputStream = new ZipInputStream(new FileInputStream(zipFile));

        try {
            // so that only one ttl file could be into the zip-file
            while ((entry = zipInputStream.getNextEntry()) != null) {
                LOGGER.debug("Loading Data from " + entry.getName());
                sqlData = convertStreamToString(zipInputStream);
                statement = hsqlConnection.createStatement();
                statement.execute(sqlData);
                statement.close();
            }
        } finally {
            zipInputStream.close();
        }

        hsqlDataModel = MappingFactory.load(CURR_DIR + "/" + CONFIG_DIR + "/" + MAPPING_FILE_HSQL, "N3", "http://test/").getDataModel();

        LOGGER.debug("Loaded SQL-Data in HSQL-DATABASE!");
    }


    /**
     * Creates a new sdb and put the data from dataset.ttl.zip into.
     */
    private void createSemanticDatabase() throws IOException {
        File zipFile;
        ZipEntry entry;
        ZipInputStream zipInputStream;
        SDBConnection sdbConnection;
        StoreDesc sdbStoreDesc;
        Store sdbStore;

        // create hsql-in-memory-database
        JDBC.loadDriverHSQL();
        sdbConnection = SDBFactory.createConnection(SDB_URL, SDB_USER, SDB_PASS);
        sdbStoreDesc = new StoreDesc(LayoutType.LayoutTripleNodesIndex, DatabaseType.HSQLDB);
        sdbStore = new StoreTriplesNodesIndexHSQL(sdbConnection, sdbStoreDesc);
        sdbStore.getTableFormatter().create();
        sdbDataModel = SDBFactory.connectDefaultModel(sdbStore);

        // load all data from dataset.ttl.zip
        zipFile = new File(CURR_DIR + "/" + DATA_DIR + "/" + FILENAME_TTL_DATA);
        zipInputStream = new ZipInputStream(new FileInputStream(zipFile));

        // NOTE: sdbModel.read closes the inputstream !!!!
        // so that only one ttl file could be into the zip-file
        entry = zipInputStream.getNextEntry();

        if (entry != null) {
            Assert.assertFalse("Entry-Name is not empty", entry.getName().isEmpty());
            sdbDataModel = sdbDataModel.read(zipInputStream, null, "TTL");
            Assert.assertTrue("sdbModel is not emtpy", sdbDataModel.size() > 0);
        }
        zipInputStream.close();

        LOGGER.debug("Loaded " + sdbDataModel.size() + " Tripples into SDB-Database!");
    }


    private String convertStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;
        String line;

        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        stringBuilder = new StringBuilder();


        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
