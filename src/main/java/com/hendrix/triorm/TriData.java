package com.hendrix.triorm;

import android.database.sqlite.SQLiteDatabase;

import com.hendrix.triorm.interfaces.IId;

import java.io.Serializable;

/**
 * Base model for 3D database. extend this.
 * also, it is recommended to use the {@link com.hendrix.triorm.annotations.TriTable}
 * annotation for meta-data embedding.
 *
 * @see com.hendrix.triorm.annotations.TriTable
 *
 * @author Tomer Shalev
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class TriData implements Serializable, IId {
    private static final long serialVersionUID = 0L;

    transient static TriOrm triOrm = null;

    /**
     * the identifier of the data
     */
    private String  _id             = null;
    /**
     * the type of the data
     */
    private String  _type           = null;
    /**
     * the created/modified time of the data
     */
    private long    _time_created   = 0L;
    /**
     * the table name
     */
    private String  _tableName      = null;

    public TriData() {
        this(null);
    }

    protected TriData(String id) {
        _time_created     = System.currentTimeMillis();
        _id               = (id == null) ? String.valueOf(Math.abs(_time_created / 1000)) : id;

        //System.out.println(_id);
    }

    /**
     * get the identifier
     *
     * @return the identifier
     */
    public String getId() { return _id; }

    /**
     * query identifier existence
     *
     * @return {@code true/false} if id was set
     */
    @Override
    public boolean hasId() {
        return getId()!=null;
    }

    /**
     * set the string identifier
     *
     * @param id the identifier
     */
    public void setId(String id) {
        _id = id;
    }

    /**
     * get the type field of the data
     *
     * @return the type
     */
    public String getType() { return _type; }

    /**
     * set the type field of the data
     *
     * @param type the type
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * get the time creation/modification of the data
     *
     * @return the time
     */
    public long getTimeCreated() { return _time_created; }

    /**
     * set the time creation/modification of the data
     *
     * @param time_created the time
     */
    public void setTimeCreated(long time_created) {
        _time_created = time_created;
    }

    /**
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "TriData:: (id->" + getId() + ", type->" + getType() +", time_created->" + getTimeCreated() + ")";
    }

    /**
     * save the object into the database.
     */
    public void save() {
        save(SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * save with conflict behaviour resolution.
     *
     * @param conflictAlgorithm choose from
     * <ul>
     *     <li/>{@link android.database.sqlite.SQLiteDatabase#CONFLICT_ABORT}
     *     <li/>{@link android.database.sqlite.SQLiteDatabase#CONFLICT_FAIL}
     *     <li/>{@link android.database.sqlite.SQLiteDatabase#CONFLICT_IGNORE}
     *     <li/>{@link android.database.sqlite.SQLiteDatabase#CONFLICT_NONE}
     *     <li/>{@link android.database.sqlite.SQLiteDatabase#CONFLICT_REPLACE}
     *     <li/>{@link android.database.sqlite.SQLiteDatabase#CONFLICT_ROLLBACK}
     * </ul>
     */
    public void save(int conflictAlgorithm) {
        TriData data = this;

        TriTable<TriData> table = (TriTable<TriData>) triOrm.getTable(getClass());

        table.addDataWithConflict(data, conflictAlgorithm);
    }

    /**
     * delete the object from the database.
     */
    public void delete() {
        TriData data = this;

        TriTable<TriData> table = (TriTable<TriData>) triOrm.getTable(getClass());

        table.delete(this);
    }

}
