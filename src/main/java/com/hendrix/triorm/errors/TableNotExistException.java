package com.hendrix.triorm.errors;

import com.hendrix.triorm.TriData;

/**
 * Exception that is raised when trying to access a table that was not registered.
 *
 * @author Tomer Shalev
 */
public class TableNotExistException extends RuntimeException {
    public TableNotExistException(Class type) {
        super("TriOrm - table for type " + type.getName() + " does not exist! add it.");
    }

}
