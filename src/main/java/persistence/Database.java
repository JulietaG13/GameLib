package persistence;

import org.hsqldb.persist.HsqlProperties;

public class Database {

    // java -cp ./lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:./db/gamelibdb --dbname.0 gamelibdb
    final String dbLocation = "/db/";
    org.hsqldb.server.Server server;

    public void startDBServer() {
        HsqlProperties props = new HsqlProperties();
        props.setProperty("server.database.0", "file:" + dbLocation + "gamelibdb;");
        props.setProperty("server.dbname.0", "gamelibdb");
        server = new org.hsqldb.Server();
        try {
            server.setProperties(props);
        } catch (Exception e) {
            return;
        }
        server.start();
    }

    public void stopDBServer() {
        server.shutdown();
    }

}
