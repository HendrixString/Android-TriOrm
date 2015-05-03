package com.hendrix.triorm.interfaces;

@SuppressWarnings("UnusedDeclaration")
public interface IId {
    /**
     * set the string identifier
     *
     * @param id the identifier
     */
    void 		setId(String id);

    /**
     * get the identifier
     *
     * @return the identifier
     */
    String 	getId();

    /**
     * query identifier existence
     *
     * @return {@code true/false} if id was set
     */
    boolean hasId();
}
