package com.hendrix.triorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-data annotation for table represented by {@link com.hendrix.triorm.TriData}
 *
 * @author Tomer Shalev
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TriTable {
    /**
     *
     * @return the database name
     */
    public String dbName();

    /**
     *
     * @return the table name inside the database
     */
    public String tableName();
}
