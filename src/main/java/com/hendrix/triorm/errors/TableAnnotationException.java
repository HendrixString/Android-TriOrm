package com.hendrix.triorm.errors;

/**
 * Meta-data extraction exception.
 *
 * @see com.hendrix.triorm.annotations.TriTable
 *
 * @author Tomer Shalev
 */
public class TableAnnotationException extends RuntimeException {
    public TableAnnotationException() {
        super("TriOrm - table annotation problem. make sure the class is annotated, and only once.");
    }

}
