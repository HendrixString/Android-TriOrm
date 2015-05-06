package com.hendrix.triorm.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Helper class utilities for {@code serializing} and {@code deserialize} {@code Typed} objects into/from
 * {@code byte arrays} and {@code base64 encoded} strings.
 *
 * @author Tomer Shalev
 */
@SuppressWarnings("UnusedDeclaration")
public class SSerialize {

    private SSerialize() {
    }

    /**
     * Serialize a {@link Serializable} object -> {@code byte[]} -> {@code Base64 Encoded string}
     *
     * @param obj {@link Serializable} object
     * @param <T> parameter type that extends {@link Serializable}
     *
     * @return {@code Base64 Encoded string}
     */
    static public<T extends Serializable> String serialize(T obj)
    {
        return Base64.encodeToString(serializeToByteArray(obj), Base64.DEFAULT );
    }

    /**
     * Serialize a {@link Serializable} object -> {@code byte[]}
     *
     * @param obj {@link Serializable} object
     * @param <T> parameter type that extends {@link Serializable}
     *
     * @return {@code byte array}
     */
    static public<T extends Serializable> byte[] serializeToByteArray(T obj)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( obj );
            oos.close();
        } catch (IOException e) {
            // TODO: handle exception
        }

        return baos.toByteArray();
    }

    /**
     * DeSerializes a {@code Base64 Encoded string} -> {@code byte[]} -> {@link Serializable} Typed Object.
     *
     * @param str {@code Base64 Encoded string}
     * @param <T> parameter type that extends {@link Serializable}
     *
     * @return {@link Serializable} Typed Object.
     */
    static public <T extends Serializable> T deserialize(String str)
    {
        byte[] data 					= Base64.decode(str, Base64.DEFAULT);

        return deserialize(data);
    }

    /**
     * De-Serializes a {@code byte[]} -> {@link Serializable} Typed Object.
     *
     * @param data byte array
     * @param <T> parameter type that extends {@link Serializable}
     *
     * @return {@link Serializable} Typed Object.
     */
    @SuppressWarnings("unchecked")
    static public <T extends Serializable> T deserialize(byte [] data)
    {
        ObjectInputStream ois;
        T o 							= null;

        try {

            ois 									= new ObjectInputStream(new ByteArrayInputStream(  data ) );
            o   									= (T)ois.readObject();
            ois.close();

        } catch (IOException | ClassNotFoundException e) {
            // TODO: handle exception
        }

        return o;
    }

}
