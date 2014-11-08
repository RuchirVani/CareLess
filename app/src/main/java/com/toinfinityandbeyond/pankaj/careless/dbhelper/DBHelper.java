package com.toinfinityandbeyond.pankaj.careless.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.toinfinityandbeyond.pankaj.careless.dbmodel.CareLessTrip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pankaj on 6/11/14.
 */
public class DBHelper extends SQLiteOpenHelper
{
    //LogTag,DbVersion,DbName
    private static final String LOG = "DBHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CareLessDB";

    //Table Names
    private static final String TABLE_TRIP = "trip";

    //Common Column Names
    private static final String KEY_ID = "id";

    //trip table column names
    private static final String KEY_FROM = "trpFrm";
    private static final String KEY_TO = "trpTo";
    private static final String KEY_DATE = "trpDate";

    //trip table create
    private static final String CREATE_TABLE_TRIP = "CREATE TABLE "
            + TABLE_TRIP + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FROM
            + " TEXT," + KEY_TO + " TEXT," + KEY_DATE + " TEXT" + ")";

    public DBHelper(Context cntxt)
    {
        super(cntxt,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // creating required tables
        db.execSQL(CREATE_TABLE_TRIP);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);

        onCreate(db);
    }

    //add a trip
    public long insertTrip(CareLessTrip trip)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FROM,trip.getFrom());
        values.put(KEY_TO,trip.getTo());
        values.put(KEY_DATE,trip.getDate());

        //insert new row
        long tripId = db.insert(TABLE_TRIP,null,values);

        return tripId;
    }

    //fetch single trip data based on tripId
    public CareLessTrip getLstTripOnDate(long Id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String slctQuery = "SELECT * FROM " + TABLE_TRIP + " WHERE " + KEY_ID + " = " + Id;
        Log.e(LOG, slctQuery);

        Cursor cr = db.rawQuery(slctQuery,null);

        if(cr != null)
            cr.moveToFirst();

        CareLessTrip trip = new CareLessTrip();
        trip.setId(cr.getInt(cr.getColumnIndex(KEY_ID)));
        trip.setFrom(cr.getString(cr.getColumnIndex(KEY_FROM)));
        trip.setTo(cr.getString(cr.getColumnIndex(KEY_TO)));
        trip.setDate(cr.getString(cr.getColumnIndex(KEY_DATE)));

        return trip;
    }

    //fetch all trip data based on tripDate
    public List<CareLessTrip> getTripsOnDate(long tripDate)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String slctQuery = "SELECT * FROM " + TABLE_TRIP + " WHERE " + KEY_DATE + " = " + tripDate;
        Log.e(LOG, slctQuery);

        Cursor cr = db.rawQuery(slctQuery,null);

        List<CareLessTrip> trips = new ArrayList<CareLessTrip>();

        if(cr.moveToFirst())
        {
            do
            {
                CareLessTrip trip = new CareLessTrip();
                trip.setId(cr.getInt(cr.getColumnIndex(KEY_ID)));
                trip.setFrom(cr.getString(cr.getColumnIndex(KEY_FROM)));
                trip.setTo(cr.getString(cr.getColumnIndex(KEY_TO)));
                trip.setDate(cr.getString(cr.getColumnIndex(KEY_DATE)));

                trips.add(trip);
            }
            while(cr.moveToNext());
        }

        return trips;
    }

    //fetch all trip data for dates less than or equal to currentDate
    public List<CareLessTrip> getTripsToday()
    {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();

        String slctQuery = "SELECT * FROM " + TABLE_TRIP + " WHERE " + KEY_DATE + " <= " + date;
        Log.e(LOG, slctQuery);

        Cursor cr = db.rawQuery(slctQuery,null);

        List<CareLessTrip> trips = new ArrayList<CareLessTrip>();

        if(cr.moveToFirst())
        {
            do
            {
                CareLessTrip trip = new CareLessTrip();
                trip.setId(cr.getInt(cr.getColumnIndex(KEY_ID)));
                trip.setFrom(cr.getString(cr.getColumnIndex(KEY_FROM)));
                trip.setTo(cr.getString(cr.getColumnIndex(KEY_TO)));
                trip.setDate(cr.getString(cr.getColumnIndex(KEY_DATE)));

                trips.add(trip);
            }
            while(cr.moveToNext());
        }

        return trips;
    }
}
