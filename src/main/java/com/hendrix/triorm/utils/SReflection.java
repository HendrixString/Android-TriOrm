package com.hendrix.triorm.utils;

import com.hendrix.triorm.TriData;
import com.hendrix.triorm.annotations.TriTable;
import com.hendrix.triorm.errors.TableAnnotationException;

import java.lang.annotation.Annotation;

/**
 * a Helper class for {@code Reflections} utilities
 *
 * @author Tomer Shalev
 */
@SuppressWarnings("UnusedDeclaration")
public class SReflection {

    private SReflection() {
    }

    /**
     * extract the {@link com.hendrix.triorm.annotations.TriTable} annotation from a {@link com.hendrix.triorm.TriData}
     *
     * @param cls the class type of the extended {@link com.hendrix.triorm.TriData}
     * @param <T> the type of the extended {@link com.hendrix.triorm.TriData}
     *
     * @return a {@link com.hendrix.triorm.utils.SReflection.Meta}
     *
     * @throws com.hendrix.triorm.errors.TableAnnotationException if the class is not annotated, or has more than one
     *                                                            {@link com.hendrix.triorm.annotations.TriTable} annotation
     */
    public static <T extends TriData> Meta  extractMetadata(Class<T> cls) {
        Annotation[] annotations = cls.getAnnotations();
        Meta<T> meta = null;

        boolean flagCompatibleAnnotationFound = false;

        for(Annotation annotation : annotations){
            if(annotation instanceof TriTable){
                if(flagCompatibleAnnotationFound)
                    throw new TableAnnotationException();

                flagCompatibleAnnotationFound = true;

                TriTable triTable = (TriTable) annotation;

                meta = new Meta<>(triTable.dbName(), triTable.tableName(), cls);
             }
        }

        if(!flagCompatibleAnnotationFound)
            throw new TableAnnotationException();

        return meta;
    }

    /**
     * a class representing the meta data represented by {@link com.hendrix.triorm.annotations.TriTable} annotation
     */
    public static class Meta<T extends TriData> {
        private String db_name = null;
        private String table_name = null;
        private Class<T> type = null;

        /**
         *
         * @param db_name       the name of the database
         * @param table_name    the name of the table
         * @param type          the type of the table
         */
        private Meta(String db_name, String table_name, Class<T> type) {
            this.db_name = db_name;
            this.table_name = table_name;
            this.type = type;
        }


        /**
         *
         * @return the name of the database
         */
        public String getDbName() {
            return db_name;
        }

        /**
         *
         * @return the name of the table
         */
        public String getTableName() {
            return table_name;
        }

        /**
         *
         * @return the name of the table
         */
        public Class<T> getTableType() {
            return type;
        }

    }

    public static String logicName(Meta meta){
        return meta.db_name + meta.table_name;
    }

}
