package pranav.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created on 06-08-2017 at 19:39 by Pranav Raut.
 * For QRCodeProtection
 *
 * @author Pranav
 * @version 0
 */

public abstract class DataBaseHelper<E> extends SQLiteOpenHelper {

    private final SQLiteQuery query;
    private final Context context;

    public DataBaseHelper(Context context, SQLiteQuery query) {
        super(context, query.getDBName(), null, 1);
        this.query = query;
        this.context = context;
        //onUpgrade(getReadableDatabase(), 0, 0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query.getCreateQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(query.getExistsQuery());
        onCreate(db);
    }

    /**
     * @return Contents of database as string[row][col]
     */
    public String[][] getEverythingAsString() {
        String[] strings = query.getNamesToArray();
        Cursor cursor = getReadableDatabase().rawQuery(query.getSelectTableQuery(), null);
        String[][] result = new String[strings.length][(int) getRowCount()];
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast()) {
                for (int i = 0; i < strings.length; i++)
                    result[cursor.getPosition()][i] = cursor.getString(i);
                cursor.moveToNext();
            }
        cursor.close();
        return result;
    }

    public int getID(String col, Object object) {
        Cursor cursor = this.getCursor(query.getSelectItemQuery(col, object));
        if (cursor != null) {
            cursor.moveToFirst();
            int i = cursor.getInt(0);
            cursor.close();
            return i;
        }
        return -1;
    }

    public int getID(String col, String object) {
        Cursor cursor = getCursor(query.getSelectItemQuery(col, object));
        if (cursor != null) {
            cursor.moveToFirst();
            int i = cursor.getInt(0);
            cursor.close();
            return i;
        }
        return -1;
    }

    @Nullable
    public Cursor getCursor(String query, String... values) {
        return getReadableDatabase().rawQuery(query, values);
    }

    public int removeAll() {
        return getWritableDatabase().delete(query.getDBName(), null, null);
    }

    public long getRowCount() {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), query.getDBName());
    }

    @Nullable
    public abstract ArrayList<E> getEverything();

    @Nullable
    public abstract E getItem(int index);

    public int deleteItem(int id) {
        return getWritableDatabase().delete(query.getDBName(),
                "id =? ", new String[]{Integer.toString(id)});
    }

    public int updateItem(int id, E item) {
        return getWritableDatabase().update(query.getDBName(),
                contentVal(item), "id =? ", new String[]{String.valueOf(id)});
    }

    public long insertItem(E item) {
        return getWritableDatabase().insert(query.getDBName(), null, contentVal(item));
    }

    protected abstract ContentValues contentVal(E item);

    public Context getContext() {
        return context;
    }

    public SQLiteQuery getQuery() {
        return query;
    }

    /**
     * Created on 06-08-2017 at 19:49 by Pranav Raut.
     * For QRCodeProtection
     *
     * @author Pranav
     * @version 0
     */
    public static final class SQLiteQuery {
        public static final String TAG = "preons";
        private final static String[] VALUES = {"BLOB", "BOOLEAN", "DATETIME", "INT", "MEDIUMINT", "BIGINT", "FLOAT", "DOUBLE", "CHARACTER", "TEXT"};
        private final String name;
        private final ArrayList<String> names;
        private final ArrayList<String> types = new ArrayList<>();

        public SQLiteQuery(String name) {
            this.names = new ArrayList<>();
            this.name = name;
        }

        public void addCol(@Size(min = 1) String[] names, @dataType String[] types) {
            if (names.length == types.length) for (int i = 0; i < names.length; i++)
                addCol(names[i], types[i]);
            else throw new IllegalArgumentException("size of name and type don't match " +
                    "name: [" + names.length + "] : type [" + types.length + "]");
        }

        public void addCol(@Size(min = 1) String name, @dataType String type) {
            if (Arrays.binarySearch(VALUES, name.toUpperCase()) > 0)
                throw new IllegalAccessError("Cannot use \"" + name + "\"");
            if (names.contains(name))
                throw new IllegalArgumentException("\"" + name + "\" exists try using different one");
            this.types.add(type);
            this.names.add(name);
        }

        @NonNull
        public String getCreateQuery() {
            StringBuilder builder = new StringBuilder(200);
            builder.append("create table ")
                    .append(name).append("(").append("id INTEGER PRIMARY KEY autoincrement, ");
            for (int i = 0; i < names.size(); i++)
                builder.append(names.get(i)).append(" ").append(types.get(i)).append(", ");
            builder = new StringBuilder(builder.substring(0, builder.length() - 2));
            builder.append(")");
            Log.d(TAG, "getCreateQuery: " + builder.toString());
            return builder.toString();
        }

        public String getDBName() {
            return name;
        }

        /**
         * @return the expression for which the class @{@link SQLiteOpenHelper} will drop table creation if the table
         * has last column existing in the table else will create new table
         */
        public String getExistsQuery() {
            return "drop table if exists " + names.get(names.size() - 1);
        }

        public ArrayList<String> getNames() {
            return names;
        }

        public String getSelectItemQuery(String colName, Object value) {
            return getSelectTableQuery() + " WHERE " + colName + " = " + value;
        }

        public String getSelectItemQuery(String colName, String value) {
            return getSelectTableQuery() + " WHERE " + colName + " = " + value;
        }

        public String getSelectItemQuery(int colIndex, Object value) {
            return getSelectTableQuery() + " WHERE " + names.get(colIndex) + " = " + value;
        }

        @NonNull
        public String[] getNamesToArray() {
            return names.toArray(new String[names.size()]);
        }

        public String getSelectTableQuery() {
            return "SELECT * from " + name;
        }
    }
}