package db61b;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

/** Test of the Table class.
 *  @author Chris Sreesangkom
 */
public class TableTest {

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

    /** This test makes sure the constructor creates a table with
     *  only one row storing the column titles and test add
     *  method to make sure it only adds when the rows are
     *  different. */
    @Test
    public void testTableConstructorAndAdd() {
        String[] columnTitles = {"Col1", "Col2", "Col3"};
        Table table = new Table(columnTitles);

        for (int i = 0; i < columnTitles.length; i++) {
            assertEquals(columnTitles[i], table.getTitle(i));
        }

        String[] row1 = {"11", "12", "13"};

        assertEquals(true, table.add(row1));
        assertEquals(2, table.size());
        assertEquals(false, table.add(row1));
        assertEquals(2, table.size());
    }

    /** This test checks if get and findColumn function
     *  is working properly. */
    @Test
    public void testGetAndFindColumn() {
        Table petTable = getPetTable();

        assertEquals("Chris", petTable.get(1, 0));
        assertEquals("USA", petTable.get(3, 2));

        assertEquals(1, petTable.findColumn("Pet"));
        assertEquals(petTable.getTitle(0), petTable.get(0, 0));
        assertEquals("Country",
                petTable.getTitle(petTable.findColumn("Country")));
    }

    /** This test checks if single table select method is
     *  working. This one also checks if Condition and Column
     *  class are working as well.*/
    @Test
    public void testSingleSelectMethod() {
        Table petTable = getPetTable();

        ArrayList<String> singleColName = new ArrayList<>();
        singleColName.add("Pet");
        singleColName.add("Name");
        Column countryCol = new Column("Country", petTable);
        Column petCol = new Column("Pet", petTable);

        ArrayList<Condition> singleCond = new ArrayList<>();
        singleCond.add(new Condition(countryCol, "=", "USA"));
        Table singleSelect1 = petTable.select(singleColName, singleCond);
        for (int i = 0; i < singleColName.size(); i++) {
            assertEquals(singleColName.get(i), singleSelect1.getTitle(i));
        }
        assertEquals(2, singleSelect1.size());
        assertEquals("Rat", singleSelect1.get(1, 0));
        assertEquals("Bob", singleSelect1.get(1, 1));

        ArrayList<Condition> twoCond = new ArrayList<>();
        twoCond.add(new Condition(countryCol, "=", "Germany"));
        twoCond.add(new Condition(petCol, "=", "Rat"));
        Table singleSelect2 = petTable.select(singleColName, twoCond);
        for (int i = 0; i < singleColName.size(); i++) {
            assertEquals(singleColName.get(i), singleSelect2.getTitle(i));
        }
        assertEquals(1, singleSelect2.size());
    }

    /** This test checks if single table select method is
     *  working. This one also checks if Condition and Column
     *  class are working as well. This include conditon
     *  which takes in two columns. And select without
     *  any condtion at all.*/
    @Test
    public void testDoubleSelectMethod() {
        Table petTable = getPetTable();
        Table parentsTable = getParentsTable();

        ArrayList<String> doubleColName = new ArrayList<>();
        doubleColName.add("Name");
        doubleColName.add("Pet");
        doubleColName.add("Mom");

        Column dadCol = new Column("Dad", petTable, parentsTable);
        ArrayList<Condition> doubleCond = new ArrayList<>();
        doubleCond.add(new Condition(dadCol, "<", "Jack"));
        Table doubleSelect1 = petTable.select(parentsTable,
                doubleColName, doubleCond);
        for (int i = 0; i < doubleColName.size(); i++) {
            assertEquals(doubleColName.get(i), doubleSelect1.getTitle(i));
        }
        assertEquals(2, doubleSelect1.size());
        assertEquals("Chris", doubleSelect1.get(1, 0));
        assertEquals("Unicorn", doubleSelect1.get(1, 1));
        assertEquals("Jill", doubleSelect1.get(1, 2));


        Table doubleSelect2 = petTable.select(parentsTable,
                doubleColName, null);
        for (int i = 0; i < doubleColName.size(); i++) {
            assertEquals(doubleColName.get(i), doubleSelect2.getTitle(i));
        }
        assertEquals(3, doubleSelect2.size());
        assertEquals("Chris", doubleSelect2.get(1, 0));
        assertEquals("Unicorn", doubleSelect2.get(1, 1));
        assertEquals("Jill", doubleSelect2.get(1, 2));
        assertEquals("Jojo", doubleSelect2.get(2, 0));
        assertEquals("Platypus", doubleSelect2.get(2, 1));
        assertEquals("Jane", doubleSelect2.get(2, 2));


        Column nameCol = new Column("Name", petTable, parentsTable);
        doubleCond.add(new Condition(nameCol, "=", nameCol));
        Table doubleSelect3 = petTable.select(parentsTable,
                doubleColName, doubleCond);
        for (int i = 0; i < doubleColName.size(); i++) {
            assertEquals(doubleColName.get(i), doubleSelect3.getTitle(i));
        }
        assertEquals(2, doubleSelect3.size());
        assertEquals("Chris", doubleSelect3.get(1, 0));
        assertEquals("Unicorn", doubleSelect3.get(1, 1));
        assertEquals("Jill", doubleSelect3.get(1, 2));
    }

    /** Tests the reading of a table. */
    @Test
    public void testReadTable() {
        String[] columns = {"cat", "mouse"};
        Table dogTable = new Table(columns);
        String[] row1 = {"meow", "woof"};
        String[] row2 = {"maow", "ruff"};
        dogTable.add(row1);
        dogTable.add(row2);

        Table readTable = Table.readTable("dog");

        for (int i = 0; i < columns.length; i++) {
            assertEquals(dogTable.getTitle(i), readTable.getTitle(i));
            for (int j = 0; j < dogTable.size(); j++) {
                assertEquals(dogTable.get(j, i), readTable.get(j, i));
            }
        }
    }

    /** Tests writing the table by overwriting
     *  dog table and rereading it. */
    @Test
    public void testWriteTable() {
        String[] columns = {"cat", "mouse"};
        Table dogTable = new Table(columns);
        String[] row1 = {"meow", "woof"};
        String[] row2 = {"maow", "ruff"};
        dogTable.add(row1);
        dogTable.add(row2);

        dogTable.writeTable("dog");

        Table readTable = Table.readTable("dog");
        for (int i = 0; i < columns.length; i++) {
            assertEquals(dogTable.getTitle(i), readTable.getTitle(i));
            for (int j = 0; j < dogTable.size(); j++) {
                assertEquals(dogTable.get(j, i), readTable.get(j, i));
            }
        }
    }

}
