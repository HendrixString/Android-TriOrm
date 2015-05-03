package com.hendrix.triorm;

import android.content.Context;

import com.hendrix.triorm.utils.SReflection;
import com.hendrix.triorm.utils.SReflection.Meta;

import java.util.HashMap;

/**
 * immutable controller for 2D database.
 * may only be built with {@code SqlSerialData.Builder}
 * <li>use {@code this.getTable(identifier)} to get the table by id
 *
 * @see TriDatabase.Builder
 *
 * @author Tomer Shalev
 *
 */
@SuppressWarnings("UnusedDeclaration")
public class TriDatabase
{
    /**
     * the context
     */
    private Context													    _ctx 			  = null;
    /**
     * the data base name
     */
    private String													    _dbName 		= null;
    /**
     * the version of the database
     */
    private int															    _version		=	0;

    private HashMap<String, TriTable>           _mapTables 	= null;

    private TriDatabase(Builder builder)
    {
        _version    = builder._version;
        _ctx        = builder._ctx;
        _dbName     = builder._dbName;
        _mapTables  = builder._mapTables;

        TriOrm.instance().addTables(_mapTables);
    }

    /**
     * get a table by it's id
     *
     * @param tableName     the table id/name
     * @param type          the type of Class the table handles
     * @param <T>           the type of Class the table handles
     *
     * @return the table
     */
    @SuppressWarnings("unchecked")
    public <T extends TriData> TriTable<T> getTable(String tableName, Class<T> type)
    {
        return (TriTable<T>)_mapTables.get(tableName);
    }

    /**
     * @return database name
     */
    public String getDatabaseName() {
        return _dbName;
    }

    /**
     * @return database version
     */
    public int version() {
        return _version;
    }

    /**
     * builder for immutable controller for {@link TriDatabase}
     *
     * @author Tomer Shalev
     */
    public static class Builder
    {
        private Context													_ctx 			      = null;
        private String													_dbName 		    = null;
        private int															_version		    =	1;

        private HashMap<String, TriTable> _mapTables 	= null;

        /**
         * @param ctx a context
         */
        public Builder(Context ctx) {
            _ctx = ctx;

            _mapTables = new HashMap<>();
        }

        /**
         * build the database
         *
         * @return {@link TriDatabase} instance
         */
        public TriDatabase build() {
            return new TriDatabase(this);
        }

        /**
         * set another context
         *
         * @see TriDatabase.Builder
         */
        public Builder context(Context ctx) {
            _ctx = ctx;

            return this;
        }

        /**
         * set a version
         *
         * @see TriDatabase.Builder
         */
        public Builder Version(int v) {
            _version = v;

            return this;
        }

        /**
         * set the name of the database.
         * this is optional if you are using {@link #addTable(Class)}, which forces the usage of
         * {@link com.hendrix.triorm.annotations.TriTable} annotations.
         *
         * @see TriDatabase.Builder
         */
        public Builder name(String name) {
            _dbName = name;

            return this;
        }

        /**
         * add a table to the database if it was not already added in the past
         *
         * @param tableName the table name
         * @param type      the Class type of the object to store, must implement {@link java.io.Serializable}
         * @param <T>       the Class type of the object to store, must implement {@link java.io.Serializable}
         *
         * @see TriDatabase
         * @see TriTable
         * @see java.io.Serializable
         * @see TriDatabase.Builder
         *
         * @deprecated not really deprecated, but I advocate using {@link #addTable(Class)} with {@link com.hendrix.triorm.annotations.TriTable} class annotations.
         */
        public <T extends TriData> Builder addTable(String tableName, Class<T> type)
        {
            if(_mapTables.containsKey(tableName))
                return this;

            TriTable<T> triTable = new TriTable<>(_ctx, _dbName, tableName, _version);

            _mapTables.put(tableName, triTable);

            return this;
        }

        /**
         * add a table to the database if it was not already added in the past using {@link com.hendrix.triorm.annotations.TriTable}
         * annotation.
         *
         * @param type      the Class type of the object to store, must implement {@link java.io.Serializable}
         * @param <T>       the Class type of the object to store, must implement {@link java.io.Serializable}
         *
         * @see TriDatabase
         * @see TriTable
         * @see java.io.Serializable
         * @see TriDatabase.Builder
         *
         * @throws java.lang.RuntimeException if meta database name is different from current database name
         */
        public <T extends TriData> Builder addTable(Class<T> type)
        {
            SReflection.Meta meta = SReflection.extractMetadata(type);

            // guaranteed uniqueness because of packages
            String uniqueClassName = type.getName();

            if(_mapTables.containsKey(uniqueClassName))
                return this;

            validateDataBaseName(meta);

            TriTable<T> triTable = new TriTable<>(_ctx, meta.getDbName(), meta.getTableName(), _version);

            _mapTables.put(uniqueClassName, triTable);

            return this;
        }

        /**
         * sets and validate the name of the database
         *
         * @param meta the {@link com.hendrix.triorm.utils.SReflection.Meta} of the table
         *
         * @throws java.lang.RuntimeException if meta database name is different from current database name
         */
        private void validateDataBaseName(Meta meta) {
            if(_dbName == null) {
                _dbName = meta.getDbName();
                return;
            }

            if(!_dbName.equals(meta.getDbName()))
                throw new RuntimeException("Table name " + meta.getDbName() + " is not consistent with current name " + _dbName + "caused by the following class" + meta.getTableType().getName());
        }
    }


}