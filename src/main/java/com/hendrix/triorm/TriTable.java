package com.hendrix.triorm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hendrix.triorm.query.TriQuery;
import com.hendrix.triorm.query.TriQuery.ORDER;
import com.hendrix.triorm.utils.SSerialize;

import java.util.ArrayList;

/**
 * simple 3D <b>SQL</b> table carrier with {@code (id, type, data, time_created)} rows, that serializes/deserialize to/from database.<br/>
 * <ul>
 *      <li>use {@link #addData(TriData)}, {@link #addDataWithConflict(TriData, int)}  to add data.
 *      <li>use {@link #getData(String)} to get a single data by identifier.
 *      <li>use {@link #delete(String)}, {@link #delete(TriData)}  to delete data.
 *      <li>use {@link #getQueryBuilder()} to get the query builder.
 *      <li>there are also other query methods, but all are based on {@link #getQueryBuilder()}.
 * </ul>
 *
 * <b>Notes:</b>
 *
 * the recommended way to access a table is with {@link TriOrm} object.
 * <ul>
 *      <li>use {@link TriOrm#query(Class)} to get {@link #getQueryBuilder()} of a table.
 *      <li>use {@link TriOrm#load(Class, String)} to get a single Data of a table by identifier.
 *      <li>use {@link TriOrm#table(Class)} to get {@link com.hendrix.triorm.TriTable} reference of the class type.
 * </ul>
 *
 * @param <T> the Class type of the object to store, must implement {@link com.hendrix.triorm.TriData}
 *
 * @author Tomer Shalev
 */
@SuppressWarnings("UnusedDeclaration")
public class TriTable<T extends TriData> extends SQLiteOpenHelper
{
    private TriQuery.Builder<T> _queryBuilder = null;

    // Database Version
    protected int DATABASE_VERSION = 1;

    // Database Name
    protected String DATABASE_NAME;

    // Table name
    protected String TABLE_NAME;

    /**
     * get the query builder
     *
     * @return the {@link TriQuery.Builder} reference
     */
    synchronized public TriQuery.Builder<T> getQueryBuilder() {
        return _queryBuilder.reset();
    }

    /**
     * get the database name
     *
     * @return the database name
     */
    public String DATABASE_NAME() {
        return DATABASE_NAME;
    }

    /**
     * get the table name
     *
     * @return the table name
     */
    public String TABLE_NAME() {
        return TABLE_NAME;
    }

    /**
     * enum describing the columns of the table
     *
     * {@code {KEY_ID, KEY_TYPE, KEY_DATA, KEY_CREATED}}
     */
    public enum Columns {
        KEY_ID("id", 0), KEY_TYPE("type", 1), KEY_DATA("data", 2), KEY_CREATED("time_created", 3);

        private Columns(String key, int index) {
            _index  = index;
            _key    = key;
        }

        private int     _index;
        private String  _key;

        public int index() {
            return  _index;
        }

        public String key() {
            return _key;
        }
    }

    /**
     * A new table
     *
     * @param context       Android's context
     * @param databaseName  name of the database to be created or loaded
     * @param tableName     the name of the table to be loaded
     * @param version       version number
     */
    public TriTable(Context context, String databaseName, String tableName, int version)
    {
        super(context, databaseName, null, version);

        DATABASE_VERSION		    =	version;

        DATABASE_NAME 			    = databaseName;
        TABLE_NAME 					    = tableName;

        onCreate(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE_COMMAND   = "CREATE TABLE IF NOT EXISTS "
          + TABLE_NAME + "("
          + Columns.KEY_ID.key()      + " TEXT PRIMARY KEY, "
          + Columns.KEY_TYPE.key()    + " STRING, "
          + Columns.KEY_DATA.key()    + " TEXT, "
          + Columns.KEY_CREATED.key() + " INTEGER" + ")";

        db.execSQL(CREATE_TABLE_COMMAND);

        _queryBuilder                 = new TriQuery.Builder<>(this);
        _queryBuilder.flagCacheQuery  = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    /**
     * add/replace new/older data, or update an older one with the correct conflict algorithm
     *
     * @param data the data
     */
    public void addData(T data)
    {
        addDataWithConflict(data, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * add new data, or update an older one with the correct conflict algorithm
     *
     * @param data              the data
     * @param conflictAlgorithm for example <code>SQLiteDatabase.CONFLICT_REPLACE</code>
     *
     */
    public void addDataWithConflict(T data, int conflictAlgorithm)
    {
        SQLiteDatabase 	db 			= this.getWritableDatabase();

        ContentValues 	values 	= new ContentValues();

        values.put(Columns.KEY_ID.key(),      data.getId());
        values.put(Columns.KEY_TYPE.key(),    data.getType());
        values.put(Columns.KEY_DATA.key(),    SSerialize.serialize(data));
        values.put(Columns.KEY_CREATED.key(), data.getTimeCreated());

        if(data.getId() == null)
            throw new NullPointerException("data.getId() = null");

        // Inserting Row
        db.insertWithOnConflict(TABLE_NAME, null, values, conflictAlgorithm);
        db.close();
    }

    /**
     * general get data
     *
     * @return {@link ArrayList} of data
     */
    synchronized public ArrayList<T> getData( String idFrom, String idTo,
                                              String type, long createdFrom, long createdTo)
    {
        return _queryBuilder.reset().idFrom(idFrom).idTo(idTo).type(type).timeCreatedFrom(createdFrom).timeCreatedTo(createdTo).ORDER(Columns.KEY_CREATED, ORDER.DESC).build().query();
    }

    /**
     * Select data by it's identifier
     *
     * @param id the id of the data
     * @return the data
     */
    @SuppressWarnings("unchecked")
    public T getData(String id)
    {
        SQLiteDatabase 	db 			= this.getReadableDatabase();

        Cursor 					cursor 	= db.query(TABLE_NAME, new String[] { Columns.KEY_DATA.key() }, Columns.KEY_ID.key() + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if(cursor==null || (cursor.getCount()==0))
            return null;

        cursor.moveToFirst();

        T res                   = (T) SSerialize.deserialize(cursor.getString(0));

        cursor.close();

        return res;
    }

    /**
     * Select data set by it's type
     *
     * @param type the type of the data
     *
     * @return {@link ArrayList} of data
     */
    public ArrayList<T> getDataByType(String type)
    {
        return _queryBuilder.reset().type(type).build().query();
    }

    /**
     * Select data from a window of identifiers. useful when ids are timestamps.
     * requires QA.
     *
     * @param idFrom    starting id
     * @param idTo      last id
     *
     * @return {@link ArrayList} of data
     */
    public ArrayList<T> getDataBetweenId(String idFrom, String idTo)
    {
        return _queryBuilder.reset().idFrom(idFrom).idTo(idTo).build().query();
    }

    /**
     * Select data from a window of time_created with projected type requires QA.
     *
     * @param date_from time_created start
     * @param date_to   time_created end
     * @param type      the type
     *
     * @return {@link ArrayList} of data
     */
    public ArrayList<T> getDataBetweenDateWithType(long date_from, long date_to, String type)
    {
        return _queryBuilder.reset().timeCreatedFrom(date_from).timeCreatedTo(date_to).type(type).ORDER(Columns.KEY_CREATED, ORDER.DESC).build().query();
    }

    /**
     * Select data from a window of time_created with projected type requires QA.
     *
     * @param date_from time_created start
     * @param date_to   time_created end
     *
     * @return {@link ArrayList} of data
     */
    public ArrayList<T> getDataBetweenDate(long date_from, long date_to)
    {
        return _queryBuilder.reset().timeCreatedFrom(date_from).timeCreatedTo(date_to).ORDER(Columns.KEY_CREATED, ORDER.DESC).build().query();
    }

    /**
     * Select data from a window of identifiers with casted type. useful when ids are timestamps.
     *
     * @param idFrom    starting id
     * @param idTo      last id
     * @param type      the type
     *
     * @return {@link ArrayList} of data
     */
    public ArrayList<T> getDataBetweenIdWithType(String idFrom, String idTo, String type)
    {
        return _queryBuilder.reset().idFrom(idFrom).idTo(idTo).type(type).build().query();
    }

    /**
     * Select latest data with limit
     *
     * @param limit the max amount of latest objects
     *
     * @return {@link ArrayList} of data
     */
    public ArrayList<T> getAllData(int limit)
    {
        return _queryBuilder.reset().ORDER(Columns.KEY_ID, ORDER.DESC).LIMIT(limit).build().query();
    }

    /**
     * update an already existing data by identifier
     *
     * @param id    the id of the data
     * @param data  the updated data
     *
     * @return the number of rows affected
     */
    public int updateData(String id, T data)
    {
        SQLiteDatabase 	db 			= this.getWritableDatabase();

        ContentValues 	values 	= new ContentValues();

        values.put(Columns.KEY_ID.key(),      data.getId());
        values.put(Columns.KEY_TYPE.key(),    data.getType());
        values.put(Columns.KEY_DATA.key(),    SSerialize.serialize(data));
        values.put(Columns.KEY_CREATED.key(), data.getTimeCreated());

        // updating row
        return db.update(TABLE_NAME, values, Columns.KEY_ID.key() + " = ?", new String[] { id });
    }

    /**
     * delete data by identifier
     *
     * @param id the id of the data
     */
    public void delete(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, Columns.KEY_ID.key() + " = ?", new String[] { id });
        db.close();
    }

    /**
     * delete data by identifier
     *
     * @param data the data of the data
     */
    public void delete(T data)
    {
        delete(data.getId());
    }

    /**
     * delete all of the data
     */
    public void deleteAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    /**
     * grab the amount of rows
     *
     * @return the count
     */
    public int getDataCount()
    {
        String 					countQuery 	= "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase 	db 					= this.getReadableDatabase();
        Cursor 					cursor 			= db.rawQuery(countQuery, null);

        int result                  = cursor.getCount();

        cursor.close();

        // return count
        return result;
    }

}