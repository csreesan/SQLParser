package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger, Chris Sreesangkom
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;
        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _titles = columnTitles;
        _columns = new ValueList[_rowSize];
        for (int i = 0; i < _rowSize; i++) {
            _columns[i] = new ValueList();
        }
        add(_titles);
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _rowSize;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < _rowSize; i++) {
            if (title.equals(getTitle(i))) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of rows in this table. */
    public int size() {
        return _size;
    }

    /** Return the value of column number COL (0 <= COL < columns())
     *  of record number ROW (0 <= ROW < size()). */
    public String get(int row, int col) {
        try {
            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /** Add a new row whose column values are VALUES to me if no equal
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    public boolean add(String[] values) {
        for (int rowIndex = 0; rowIndex < _size; rowIndex++) {
            boolean rowSame = true;
            int rowCompareCol = 0;
            for (int i = 0; i < _rowSize; i++) {
                if (!_columns[i].get(rowIndex).equals(values[i])) {
                    rowSame = false;
                    break;
                }
            }
            if (rowSame) {
                return false;
            }
        }
        for (int i = 0; i < _rowSize; i++) {
            _columns[i].add(values[i]);
        }
        _size++;

        if (_index.size() > 0) {
            for (int i = 0; i < _index.size(); i++) {
                if (compareRows(size() - 1, _index.get(i)) <= 0) {
                    _index.add(i, size() - 1);
                    break;
                }
            }
            if (_index.size() < size() - 1) {
                _index.add(size() - 1);
            }
        } else if (size() == 2 && _index.size() == 0) {
            _index.add(size() - 1);
        }
        return true;
    }

    /** Add a new row whose column values are extracted by COLUMNS from
     *  the rows indexed by ROWS, if no equal row already exists.
     *  Return true if anything was added, false otherwise. See
     *  Column.getFrom(Integer...) for a description of how Columns
     *  extract values. */
    public boolean add(List<Column> columns, Integer... rows) {
        String[] values = new String[_rowSize];
        for (int i = 0; i < _rowSize; i++) {
            values[i] = columns.get(i).getFrom(rows);
        }
        return add(values);
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            String row = input.readLine();
            while (row != null) {
                String[] rowValues = row.split(",");
                table.add(rowValues);
                row = input.readLine();
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = ",";
            output = new PrintStream(name + ".db");
            for (int i = 0; i < size(); i++) {
                for (int j = 0; j < _rowSize; j++) {
                    output.print(_columns[j].get(i));
                    if (j < _rowSize - 1) {
                        output.print(sep);
                    }
                }
                output.println();
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        for (int row : _index) {
            System.out.print(" ");
            for (int j = 0; j < _rowSize; j++) {
                System.out.print(" " + _columns[j].get(row));
            }
            System.out.println();
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        int[] colIndexArr = new int[columnNames.size()];
        for (int i = 0; i < columnNames.size(); i++) {
            colIndexArr[i] = findColumn(columnNames.get(i));
        }

        ArrayList<Integer> rowList = new ArrayList<>();
        for (int r = 1; r < size(); r++) {
            if (Condition.test(conditions, r)) {
                rowList.add(r);
            }
        }

        for (int row : rowList) {
            ArrayList<String> valueList = new ArrayList<>();
            for (int col : colIndexArr) {
                valueList.add(get(row, col));
            }
            String[] values = new String[valueList.size()];
            for (int i = 0; i < valueList.size(); i++) {
                values[i] = valueList.get(i);
            }
            result.add(values);
        }

        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
        ArrayList<Column> common1 = new ArrayList<>();
        ArrayList<Column> common2 = new ArrayList<>();
        for (int i = 0; i < _rowSize; i++) {
            for (int j = 0; j < table2._rowSize; j++) {
                if (getTitle(i).equals(table2.getTitle(j))) {
                    String columnName = getTitle(i);
                    common1.add(new Column(columnName, this));
                    common2.add(new Column(columnName, table2));
                }
            }
        }
        ArrayList<Integer> comRow1 = new ArrayList<>();
        ArrayList<Integer> comRow2 = new ArrayList<>();
        for (int i = 1; i < size(); i++) {
            for (int j = 1; j < table2.size(); j++) {
                if (equijoin(common1, common2, i, j)) {
                    comRow1.add(i);
                    comRow2.add(j);
                }
            }
        }
        ArrayList<Column> columnList = new ArrayList<>();
        for (String columnName : columnNames) {
            columnList.add(new Column(columnName, this, table2));
        }
        for (int i = 0; i < comRow1.size(); i++) {
            if (Condition.test(conditions, comRow1.get(i), comRow2.get(i))) {
                result.add(columnList, comRow1.get(i), comRow2.get(i));
            }
        }
        return result;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elements
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(int k0, int k1) {
        for (int i = 0; i < _columns.length; i += 1) {
            int c = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        for (int i = 0; i < common1.size(); i++) {
            if (!common1.get(i).getFrom(row1).
                    equals(common2.get(i).getFrom(row2))) {
                return false;
            }
        }
        return true;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    private static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order in at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of rows (redundant, but convenient). */
    private int _size;
    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;
}
