package db61b;
import java.util.HashMap;

/** A collection of Tables, indexed by name.
 *  @author Chris Sreesangkom */
class Database {
    /** An empty database. */
    public Database() {
        tableMap = new HashMap<String, Table>();

    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        if (tableMap.containsKey(name)) {
            return tableMap.get(name);
        }
        return null;
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        tableMap.put(name, table);
    }

    /** Mapping between table name and table. */
    private HashMap<String, Table> tableMap;
}
