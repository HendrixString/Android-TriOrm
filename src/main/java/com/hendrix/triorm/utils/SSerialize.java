package com.hendrix.triorm.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * static methods for serializing and deserialize
 * @author Tomer Shalev
 */
public class SSerialize {

	/*
	 * You can use DeflatorOutputStream and InflatorInputStream to compress the stream.
			If you can I would suggest using a more compact serialization format such as JSon, 
			or Exernalizable or your own binary format. This is because default Java Serialization is relatively verbose. 
			e.g. one Integer uses over 80 bytes.
	 * 
	 */

    private SSerialize() {
    }

    /**
     * Serialize a <code>Serializable</code> object -> <code>ByteArray</code> -> <code>Base64 Encoded</code>
     * @param obj <code>Serializable</code> object
     * @return Base64 Encode
     */
    static public String serialize(Object obj)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( obj );
            oos.close();
        } catch (IOException e) {
            // TODO: handle exception
        }

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT );
    }

    /**
     * Deserialize a <code>Base64 Encoded</code> String -> <code>ByteArray</code> -> <code>Object</code>
     * @param str <code>Base64 Encoded</code> String
     * @return <code>String</code>
     */
    static public Object deserialize(String str)
    {
        byte [] data 					= Base64.decode(str, Base64.DEFAULT);

        ObjectInputStream ois	=	null;
        Object o 							= null;

        try {

            ois 									= new ObjectInputStream(new ByteArrayInputStream(  data ) );
            o   									= ois.readObject();
            ois.close();

        } catch (IOException e) {
            // TODO: handle exception
        }
        catch (ClassNotFoundException e) {
            // TODO: handle exception
        }

        return o;
    }

}
