package edu.arizona.group5;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * 
 * Gets and stores data from the local database (previous connections).
 * 
 * @author Jordan Smith, William Snider
 * 
 */
public class DataHelper {
    private static final String DATABASE_NAME = "laser_pointer.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_CONNECTIONS = "connections";
    private Context context;
    private SQLiteDatabase db;
    static DataHelper sInstance;
    private SQLiteStatement insertStmt;

    private static final String INSERT_CONNECTION = "insert into "
	    + TABLE_CONNECTIONS + " (host, port) VALUES (?, ?);";

    public DataHelper(Context context) {
	this.context = context;
	OpenHelper openHelper = new OpenHelper(this.context);
	db = openHelper.getWritableDatabase();
    }

    public void insertConnection(String n, String p) {
	insertStmt = db.compileStatement(INSERT_CONNECTION);
	insertStmt.bindString(1, n);
	insertStmt.bindString(2, p);
	insertStmt.executeInsert();
    }

    public void deleteConnection(String nameMovie) {
	db.delete(TABLE_CONNECTIONS, "host=?",
		new String[] { nameMovie.toString() });
    }

    public List<Connection> selectAllConnections() {
	List<Connection> list = new ArrayList<Connection>();
	Cursor c = db.query(TABLE_CONNECTIONS, new String[] { "host", "port" },
		null, null, null, null, null);
	if (c != null & c.getCount() > 0) {
	    if (c.moveToFirst()) {
		do {
		    Connection connection = null;
		    for (int i = 0; i < c.getColumnCount(); i++) {
			if (!c.getString(0).equals("")
				&& !c.getString(1).equals("")) {
			    connection = new Connection(c.getString(0),
				    Integer.parseInt(c.getString(1)));
			}
		    }
		    list.add(connection);
		} while (c.moveToNext());
		c.close();
	    }
	}
	return list;
    }

    public void updateConnection(String host, String port) {
	ContentValues values = new ContentValues();
	values.put("port", port);
	db.update(TABLE_CONNECTIONS, values, "host=?", new String[] { host });
    }

    public List<String> selectAll() {
	List<String> list = new ArrayList<String>();
	list.clear();
	Cursor cursor = this.db.query(TABLE_CONNECTIONS,
		new String[] { "host" }, null, null, null, null, null);
	if (cursor.moveToFirst()) {
	    do {
		list.add(new String(cursor.getString(0)));
	    } while (cursor.moveToNext());
	    if (cursor != null && !cursor.isClosed()) {
		cursor.close();
	    }
	}
	return list;
    }

    public List<String> searchByHost(String host) {
	List<String> list = new ArrayList<String>();
	list.clear();
	Cursor cursor = this.db.query(TABLE_CONNECTIONS, new String[] { "host",
		"port" }, null, null, null, null, null);
	if (cursor.moveToFirst()) {
	    do {
		String search = new String(cursor.getString(0));
		if (search.equals(host))
		    list.add(new String(cursor.getString(0) + ","
			    + cursor.getString(1)));
	    } while (cursor.moveToNext());
	    if (cursor != null && !cursor.isClosed()) {
		cursor.close();
	    }
	}
	return list;
    }

    private static class OpenHelper extends SQLiteOpenHelper {
	OpenHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	    db.execSQL("CREATE TABLE "
		    + TABLE_CONNECTIONS
		    + " (_id integer primary key autoincrement, host text not null, port text) ");
	}

	@Override
	public void
		onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIONS);
	    onCreate(db);
	}
    }
}
