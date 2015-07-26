package com.hendrix.triorm.query;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hendrix.triorm.TriData;
import com.hendrix.triorm.TriTable;
import com.hendrix.triorm.TriTable.Columns;
import com.hendrix.triorm.utils.SSerialize;

import java.util.ArrayList;

/**
 * a {@code SQL} query builder according to the identifier, type, time_created fields of {@link com.hendrix.triorm.TriTable.Columns}
 *
 * @param <T> the data type
 *
 * @author Tomer Shalev
 */
@SuppressWarnings("UnusedDeclaration")
public class TriQuery<T extends TriData> {

    private String              _rawQueryString = null;
    private TriTable            _ssd            = null;
    /**
     * limit
     */
    private int                 _limit          = Integer.MAX_VALUE;
    /**
     * order by column
     */
    private Columns              _by            = null;
    /**
     * order of results
     */
    private ORDER               _order          = ORDER.NONE;

    /**
     * enum for describing order {@code {DESC, ASC, NONE}}
     */
    public enum ORDER{DESC, ASC, NONE}

    private TriQuery(Builder builder) {
        update(builder);
    }

    /**
     * update a recycled builder
     *
     * @param builder the builder that was recycled
     */
    private void update(Builder builder) {
        _rawQueryString = builder._rawQueryString;
        _ssd            = builder._ssd;
        _limit          = builder._limit;
        _by             = builder._by;
        _order          = builder._order;
    }

    /**
     * perform the query
     *
     * @return {@link java.util.ArrayList} of data
     */
    @SuppressWarnings("unchecked")
    public ArrayList<T> query()
    {
        SQLiteDatabase db 			= _ssd.getReadableDatabase();

        ArrayList<T> listData   = new ArrayList<>();

        String orderBy          = null;

        if(_order != ORDER.NONE && _by!=null) {
            orderBy               = _by.key() + " " + _order.name();
        }

        String limit            = (_limit==Integer.MAX_VALUE) ? null : String.valueOf(_limit);

        Cursor cursor 	        = db.query(_ssd.TABLE_NAME(), new String[] {Columns.KEY_DATA.key() }, _rawQueryString, null, null, null, orderBy, limit);

        int a  = cursor.getCount();

        if(cursor==null || (cursor.getCount()==0))
            return listData;

        if (cursor.moveToFirst()) {
            do {
               // String data 			  = cursor.getString(0); *****
                byte[] data 			  =                 cursor.getBlob(0);

                Object dd = SSerialize.deserialize(data);

                listData.add((T) SSerialize.deserialize(data));
            } while (cursor.moveToNext());
        }

        //cursor.close();

        return listData;
    }

    /**
     * the builder of the query
     *
     * @param <E> the data type
     *
     * @see com.hendrix.triorm.query.TriQuery
     *
     * @author Tomer Shalev
     */
    public static class Builder<E extends TriData> {

        /**
         * recycle the TriQuery object?
         */
        public boolean flagCacheQuery = false;

        private String              _idFrom             = null;
        private String              _idTo               = null;
        private String              _type               = null;
        private String              _rawQueryString     = null;
        private long                _time_created_from  = -1L;
        private long                _time_created_to    = -1L;

        /**
         * order by column
         */
        private Columns             _by                 = null;
        /**
         * order of results
         */
        private ORDER               _order              = ORDER.NONE;
        /**
         * limit of results
         */
        private int                 _limit              = Integer.MAX_VALUE;

        /**
         * the table on which to perform the query
         */
        private TriTable            _ssd                = null;
        /**
         * the cached query
         *
         * @see #flagCacheQuery
         */
        private TriQuery<E>         _cachedQuery        = null;

        /**
         * @param ssd the table on which to perform the query
         */
        public Builder(TriTable ssd) {
            _ssd = ssd;
        }

        /**
         * build the query
         *
         * @return a {@link com.hendrix.triorm.query.TriQuery} instance
         */
        public TriQuery<E> build(){
            buildString();
            //reset();

            TriQuery<E> query = (flagCacheQuery) ? (_cachedQuery==null ? _cachedQuery=new TriQuery<>(this) : _cachedQuery) : new TriQuery<E>(this);

            if(flagCacheQuery)
                query.update(this);

            return query;
        }

        /**
         * build the {@code SQL} query string
         *
         * @return the {@code SQL} string
         */
        public String buildString()
        {
            String id_selection, type_selection, created_selection, query = "";
            boolean isFirst       = true;

            if(_idFrom!=null && _idTo!=null) {
                id_selection      = "(" + Columns.KEY_ID.key() + " BETWEEN '" + _idFrom + "' AND '" + _idTo + "')";
                query             = id_selection;
                isFirst           = false;
            }

            if(_type!=null) {
                type_selection    = "(" + Columns.KEY_TYPE.key() + " = '" + _type + "')";
                query             = (!isFirst) ? query + " AND " + type_selection : type_selection;
                isFirst           = false;
            }

            if(_time_created_from>=0 && _time_created_to>=0) {
                created_selection = "(" + Columns.KEY_CREATED.key() + " BETWEEN '" + Long.toString(_time_created_from) + "' AND '" + Long.toString(_time_created_to) + "')";
                query             = (!isFirst) ? query + " AND " + created_selection : created_selection;
                isFirst           = false;
            }

            if(isFirst) {
                // query=null will force SELECT *.. ->from the android api
                query             = null;
            }

            return (_rawQueryString = query);
        }

        /**
         * @return a printable representation of the {@code SQL} query {@code SELECTION} string
         */
        @Override
        public String toString() {
            return _rawQueryString;
        }

        /**
         * reset the builder for recycling purposes. always user reset if.
         *
         * @return the reset recycled builder
         *
         * @see com.hendrix.triorm.query.TriQuery.Builder
         */
        public Builder<E> reset() {
            _idFrom         = null;
            _idTo           = null;
            _type           = null;
            _rawQueryString = null;
            _by             = null;
            _order          = ORDER.NONE;
            _limit          = Integer.MAX_VALUE;
            _time_created_from = -1L;
            _time_created_to = -1L;

            return this;
        }

        /**
         * set starting id for query
         *
         * @param from the starting identifier
         *
         * @return the Builder
         *
         * @see com.hendrix.triorm.query.TriQuery.Builder
         */
        public Builder<E> idFrom(String from) {
            _idFrom = from;

            return this;
        }

        /**
         * set destination id for query
         *
         * @param to the destination identifier
         *
         * @return the Builder
         *
         * @see com.hendrix.triorm.query.TriQuery.Builder
         */
        public Builder<E> idTo(String to) {
            _idTo = to;

            return this;
        }

        /**
         * set the type for query
         *
         * @param type the type
         *
         * @return the Builder
         *
         * @see com.hendrix.triorm.query.TriQuery.Builder
         */
        public Builder<E> type(String type) {
            _type = type;

            return this;
        }

        /**
         * set the starting creation time for query
         *
         * @param from the starting creation time
         *
         * @return the Builder
         *
         * @see com.hendrix.triorm.query.TriQuery.Builder
         */
        public Builder<E> timeCreatedFrom(long from) {
            _time_created_from  = from;

            return this;
        }

        /**
         * set the destination creation time for query
         *
         * @param to the destination creation time
         *
         * @return the Builder
         *
         * @see com.hendrix.triorm.query.TriQuery.Builder
         */
        public Builder<E> timeCreatedTo(long to) {
            _time_created_to  = to;

            return this;
        }

        /**
         * set the order for query
         *
         * @param by    order by which column
         * @param order the order
         *
         * @return the Builder
         *
         * @see com.hendrix.triorm.query.TriQuery.Builder
         */
        public Builder<E> ORDER(Columns by, ORDER order)
        {
            _by     = by;
            _order  = order;

            return this;
        }

        /**
         * set the limit of the query result
         *
         * @param limit the limit of the query
         *
         * @return the Builder
         *
         * @see com.hendrix.triorm.query.TriQuery.Builder
         */
        public Builder<E> LIMIT(int limit)
        {
            _limit = limit;

            return this;
        }

    }

}
