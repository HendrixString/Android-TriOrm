package com.hendrix.triorm.errors;

/**
 * @author Tomer Shalev
 */
public class TableAnnotationException extends RuntimeException {
    public TableAnnotationException() {
        super("TriOrm - table annotation problem. make sure the class is annotated, and only once.");
    }
}
