package db61b;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

/** Test of the Table class.
 *  @author Chris Sreesangkom
 */
public class ConditionTest {

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

    /** Test for Condition.test method when Conditoin is contrcuted
     *  with one column and one literal */
    @Test
    public void testConditionTestOneColumn() {

        Table petTable = getPetTable();

        ArrayList<String> singleColName = new ArrayList<>();
        singleColName.add("Pet");
        singleColName.add("Name");
        Column countryCol = new Column("Country", petTable);
        Column petCol = new Column("Pet", petTable);

        ArrayList<Condition> singleCond = new ArrayList<>();
        singleCond.add(new Condition(countryCol, "=", "USA"));
        assertEquals(true, Condition.test(singleCond, 3));
        assertEquals(false, Condition.test(singleCond, 2));
        assertEquals(false, Condition.test(singleCond, 1));
        assertEquals(false, Condition.test(singleCond, 0));

        ArrayList<Condition> twoCond = new ArrayList<>();
        twoCond.add(new Condition(countryCol, "=", "Germany"));
        twoCond.add(new Condition(petCol, "=", "Rat"));
        assertEquals(false, Condition.test(twoCond, 3));
        assertEquals(false, Condition.test(twoCond, 2));
        assertEquals(false, Condition.test(twoCond, 1));
        assertEquals(false, Condition.test(twoCond, 0));
    }

    /** Test for Condition.test method when Conditoin is contrcuted
     *  with two columns */
    @Test
    public void testConditionTestTwoColumns() {
        Table petTable = getPetTable();

        ArrayList<String> singleColName = new ArrayList<>();
        singleColName.add("Pet");
        singleColName.add("Name");
        Column countryCol = new Column("Country", petTable);
        Column petCol = new Column("Pet", petTable);

        ArrayList<Condition> singleCond = new ArrayList<>();
        singleCond.add(new Condition(countryCol, "=", petCol));
        assertEquals(false, Condition.test(singleCond, 3));
        assertEquals(false, Condition.test(singleCond, 2));
        assertEquals(false, Condition.test(singleCond, 1));
        assertEquals(false, Condition.test(singleCond, 0));

        ArrayList<Condition> twoCond = new ArrayList<>();
        twoCond.add(new Condition(countryCol, "=", countryCol));
        twoCond.add(new Condition(petCol, "=", petCol));
        assertEquals(true, Condition.test(twoCond, 3));
        assertEquals(true, Condition.test(twoCond, 2));
        assertEquals(true, Condition.test(twoCond, 1));
        assertEquals(true, Condition.test(twoCond, 0));
    }

}
