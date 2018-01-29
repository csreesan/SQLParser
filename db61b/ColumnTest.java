package db61b;

import org.junit.Test;
import static org.junit.Assert.*;

/** Test of the Column class.
 *  @author Chris Sreesangkom
 */
public class ColumnTest {

    /** Returns a table for test. */
    private Table getPetTable() {
        String[] petColumns = {"Name", "Pet", "Country"};
        Table petTable = new Table(petColumns);
        String[] petRow1 = {"Chris", "Unicorn", "Thailand"};
        String[] petRow2 = {"Jojo", "Platypus", "Germany"};
        String[] petRow3 = {"Bob", "Rat", "USA"};

        petTable.add(petRow1);
        petTable.add(petRow2);
        petTable.add(petRow3);

        return petTable;
    }

    /** Returns a table for test. */
    private Table getParentsTable() {
        String[] parentsColumns = {"Name", "Mom", "Dad"};
        Table parentsTable = new Table(parentsColumns);
        String[] parentsRow1 = {"John", "Jane", "Bill"};
        String[] parentsRow2 = {"Chris", "Jill", "Bill"};
        String[] parentsRow3 = {"Jojo", "Jane", "Jack"};

        parentsTable.add(parentsRow1);
        parentsTable.add(parentsRow2);
        parentsTable.add(parentsRow3);

        return parentsTable;
    }

    /** Tests contructor and getFrom method of Column */
    @Test
    public void testGetFrom() {
        Table parentsTable = getParentsTable();
        Table petTable = getPetTable();

        Column dogCol = new Column("Pet", petTable, parentsTable);
        assertEquals("Unicorn", dogCol.getFrom(1, 2));

        Column dadCol = new Column("Dad", petTable, parentsTable);
        assertEquals("Bill", dadCol.getFrom(1, 1));
        assertEquals("Bill", dadCol.getFrom(2, 1));
        assertEquals("Bill", dadCol.getFrom(1, 2));
        assertEquals("Jack", dadCol.getFrom(1, 3));
        assertEquals("Jack", dadCol.getFrom(2, 3));

    }

}
