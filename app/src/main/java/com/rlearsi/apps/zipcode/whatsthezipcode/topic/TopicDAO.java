package com.rlearsi.apps.zipcode.whatsthezipcode.topic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.rlearsi.apps.zipcode.whatsthezipcode.DbGateway;
import com.rlearsi.apps.zipcode.whatsthezipcode.DbHelper;
import com.rlearsi.apps.zipcode.whatsthezipcode.MyDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TopicDAO {

    private static DbGateway gw;
    private Date datetime = new Date();

    public TopicDAO(Context ctx) {
        gw = DbGateway.getInstance(ctx);
    }

    boolean save(String cep, String uf, String cidade, String bairro, String logradouro) {

        String stringDt = MyDate.datetimeToInt(String.valueOf(datetime), "date_int");
        String stringTm = MyDate.datetimeToInt(String.valueOf(datetime), "time_int");

        ContentValues cv = new ContentValues();
        cv.put("zipcode", cep);
        cv.put("uf", uf);
        cv.put("city", cidade);
        cv.put("neighborhood", bairro);
        cv.put("logradouro", logradouro);
        cv.put("country", "BR");
        cv.put("active", 1);
        cv.put("dt", stringDt);
        cv.put("tm", stringTm);

        return gw.getDatabase().insert(DbHelper.TABLE_TOPICS, null, cv) > 0;

    }

    boolean inactive(String cep) {

        ContentValues cv = new ContentValues();
        cv.put("active", 0);

        return gw.getDatabase().update(DbHelper.TABLE_TOPICS, cv, "zipcode=?", new String[]{cep + ""}) > 0;

    }

    boolean reactivate(String cep) {

        ContentValues cv = new ContentValues();
        cv.put("active", 1);

        return gw.getDatabase().update(DbHelper.TABLE_TOPICS, cv, "zipcode=?", new String[]{cep + ""}) > 0;

    }

    public void deleteInactive() {

        gw.getDatabase().delete(DbHelper.TABLE_TOPICS, "active=?", new String[]{"0"});

    }

    public List<Topic> returnList(boolean all) {

        List<Topic> topics = new ArrayList<>();

        String limit = (all) ? "" : "LIMIT 10";

        String SQL_ORDER = " ORDER BY ID DESC";
        Cursor cursor = gw.getDatabase().rawQuery("SELECT ID, zipcode, uf, city, neighborhood, logradouro, active, dt, tm FROM " + DbHelper.TABLE_TOPICS + " WHERE active = 1 " + SQL_ORDER + " " + limit, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            String cep = cursor.getString(cursor.getColumnIndex("zipcode"));
            String uf = cursor.getString(cursor.getColumnIndex("uf"));
            String cidade = cursor.getString(cursor.getColumnIndex("city"));
            String bairro = cursor.getString(cursor.getColumnIndex("neighborhood"));
            String logradouro = cursor.getString(cursor.getColumnIndex("logradouro"));
            int active = cursor.getInt(cursor.getColumnIndex("active"));
            topics.add(new Topic(id, cep, uf, cidade, bairro, logradouro, 1, active));
        }
        cursor.close();

        return topics;

    }

    Topic returnTo(String cepX) {

        String SQL = "SELECT ID, zipcode, uf, city, neighborhood, logradouro, active, dt, tm FROM " + DbHelper.TABLE_TOPICS + "";
        Cursor cursor = gw.getDatabase().rawQuery(SQL + " WHERE zipcode = \"" + cepX + "\" LIMIT 1", null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            String cep = cursor.getString(cursor.getColumnIndex("zipcode"));
            String uf = cursor.getString(cursor.getColumnIndex("uf"));
            String cidade = cursor.getString(cursor.getColumnIndex("city"));
            String bairro = cursor.getString(cursor.getColumnIndex("neighborhood"));
            String logradouro = cursor.getString(cursor.getColumnIndex("logradouro"));
            int active = cursor.getInt(cursor.getColumnIndex("active"));
            cursor.close();

            return new Topic(id, cep, uf, cidade, bairro, logradouro, 1, active);

        }

        return null;
    }

    public Topic existCep(int id, String cep, String uf, String cidade, String bairro, String logradouro) {

        int active = 0;
        int count = 0;
        Cursor cursor = gw.getDatabase().rawQuery("SELECT ID, active FROM " + DbHelper.TABLE_TOPICS + " WHERE zipcode = \"" + cep + "\" LIMIT 1", null);

        if (cursor.moveToFirst()) {

            count = cursor.getCount();

            active = cursor.getInt(cursor.getColumnIndex("active"));
            cursor.close();

        }

        return new Topic(id, cep, uf, cidade, bairro, logradouro, count, active);
    }

}