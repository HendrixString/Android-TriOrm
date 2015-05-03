package com.hendrix.triorm.errors;

import com.hendrix.triorm.TriData;

/**
 * @author Tomer Shalev
 */
public class TableNotExistException extends RuntimeException {
    public TableNotExistException(Class type) {
        super("TriOrm - table for type " + type.getName() + " does not exist! add it.");
    }
}
