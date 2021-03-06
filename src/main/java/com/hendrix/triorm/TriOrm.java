package com.hendrix.triorm;

import android.content.Context;

import com.hendrix.triorm.exceptions.TableNotExistException;
import com.hendrix.triorm.query.TriQuery;

import java.util.HashMap;

/**
 * the main singleton for {@code 3D} database access.
 *
 * <ul>
 *      <li>use {@link TriOrm#query(Class)} to get {@link com.hendrix.triorm.query.TriQuery.Builder} of a table.
 *      <li>use {@link TriOrm#load(Class, String)} to get a single Data of a table by identifier.
 *      <li>use {@link TriOrm#table(Class)} to get {@link com.hendrix.triorm.TriTable} reference of the class type.
 *      <li>use {@link com.hendrix.triorm.TriOrm#newDatabase()} to get a new {@link com.hendrix.triorm.TriDatabase.Builder} instance.
 * </ul>
 *
 * TODO:
 * - add a method to get all the tables of a specific database by name.
 *
 * @author Tomer Shalev
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class TriOrm
{
    private static TriOrm                       _instance   = null;
    /**
     * the context
     */
    private Context													    _ctx 			  = null;
    /**
     * class names with package to {@link TriTable} map
     */
    private HashMap<String, TriTable>           _mapTables 	= null;

    private TriOrm() {
        if(_instance != null)
            throw new RuntimeException("TriOrm is a singleton. please use TriOrm.instance() instead");

        TriData.triOrm  = this;
        _mapTables      =  new HashMap<>();
    }

    /**
     * get the representing {@link TriTable}
     *
     * @param type the class type of the table
     * @param <T>  the type itself
     *
     * @return {@link TriTable}
     */
    public static <T extends TriData> TriTable<T> table(Class<T> type) {
        return instance().getTable(type);
    }

    /**
     * load data from the table by identifier.
     *
     * @param type  the class type of the table
     * @param id    the identifier of the data
     * @param <T>   the type itself
     *
     * @return the data
     */
    public static <T extends TriData> T load(Class<T> type, String id) {
        return table(type).getData(id);
    }

    /**
     * get the query builder.
     *
     * @param type the class type of the table
     * @param <T>  the type itself
     *
     * @return the {@link com.hendrix.triorm.query.TriQuery.Builder} instance
     *
     * @see com.hendrix.triorm.query.TriQuery.Builder
     */
    public static <T extends TriData> TriQuery.Builder<T> query(Class<T> type) {
        return table(type).getQueryBuilder();
    }

    /**
     * get a new Database builder object. you can also use:
     * {@code new TriDatabase.Builder(..)} yourself.
     *
     * @return {@link com.hendrix.triorm.TriDatabase.Builder}
     */
    public static TriDatabase.Builder newDatabase() {
        return new TriDatabase.Builder(null);
    }

    /**
     * get the instance. package protected.
     *
     * @return the only {link TriOrm} instance.
     */
    static protected TriOrm instance() {
        return (_instance==null) ? _instance = new TriOrm() : _instance;
    }

    /**
     * add tables to the ORM. this is used by {@link com.hendrix.triorm.TriDatabase} and therefore is protected access.
     *
     * @param tables a Map Collection between class names into {@link com.hendrix.triorm.TriTable}
     */
    protected void addTables(HashMap<String, TriTable> tables) {
        _mapTables.putAll(tables);
    }

    /**
     * get a table by it's identifier from any database that was loaded.
     * table names a are unique, since the class type is used. Therefore,
     * client does not have to specify database name.
     *
     * @param type          the type of Class the table handles
     * @param <T>           the type of Class the table handles
     *
     * @return the table
     *
     * @throws com.hendrix.triorm.exceptions.TableNotExistException if the table was not registered.
     */
    @SuppressWarnings("unchecked")
    protected <T extends TriData> TriTable<T> getTable(Class<T> type)
    {
        TriTable<T> triTable = (TriTable<T>)_mapTables.get(type.getName());

        if(triTable == null)
            throw new TableNotExistException(type);

        return triTable;
    }

}