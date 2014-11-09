package com.toinfinityandbeyond.pankaj.careless;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.*;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.toinfinityandbeyond.pankaj.careless.dbhelper.DBHelper;
import com.toinfinityandbeyond.pankaj.careless.dbmodel.CareLessTrip;


public class CareLessNewTrip extends Activity implements OnClickListener
{
    private EditText trpDate;
    private AutoCompleteTextView actfrm;
    private AutoCompleteTextView actto;
    private TextView lstFrm;
    private TextView lstTo;
    private TextView lstTrpDate;
    private Button newTrpBtn;
    private Button lstTripBtn;
    private static long lstTripId=0;
    DBHelper db;
    private DatePickerDialog trpDateDatePickerDlg;
    private SimpleDateFormat dateFormatter;
    public   static  String frm;
    public   static  String to;
    private  static  String dt;
    private static final String LOG_TAG = "CareLess";
    private static final String PLACES_API_BASE = "http://api.sandbox.amadeus.com/v1.2/airports";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete?";
    //private static final String OUT_JSON = "/json";
    private static final String API_KEY = "9FVBpJDSBogQAEIi1PVrHn8AX1cznZMm";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper((getApplicationContext()));

        findViewsById();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        setCurrentDateOnView();
        setDateTimeField();
        actfrm.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
        actto.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
        newTrpBtn.setOnClickListener(this);
        lstTripBtn.setOnClickListener(this);
    }



    /** A method to download json data from url */
    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE);
            sb.append("apikey=" + API_KEY);
            sb.append("&term=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try
        {

            // Create a JSON object hierarchy from the results
            JSONArray jsnArray = new JSONArray(jsonResults.toString());
            //JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(jsnArray.length());
            for (int i = 0; i < jsnArray.length(); i++) {
                JSONObject jsnObj = jsnArray.getJSONObject(i);
                resultList.add(jsnObj.getString("label"));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }

    private void findViewsById()
    {
        trpDate = (EditText) findViewById(R.id.date_entry_fld);
        trpDate.setInputType(InputType.TYPE_NULL);
        actfrm = (AutoCompleteTextView) findViewById(R.id.frm_entry_fld);
        actto = (AutoCompleteTextView) findViewById(R.id.to_entry_fld);
        newTrpBtn = (Button) findViewById(R.id.new_trp_btn);
        lstTripBtn = (Button) findViewById(R.id.lst_trip_btn);
        lstFrm = (TextView) findViewById(R.id.frm_lst_trp);
        lstTo = (TextView) findViewById(R.id.to_lst_trip);
        lstTrpDate = (TextView) findViewById(R.id.date_lst_trip);
    }

    private void setCurrentDateOnView()
    {
        int year,month,day;
        final Calendar newCalendar = Calendar.getInstance();
        year = newCalendar.get(Calendar.YEAR);
        month = newCalendar.get(Calendar.MONTH);
        day = newCalendar.get(Calendar.DAY_OF_MONTH);

        trpDate.setText(new StringBuilder().append(year).append("-").append(month + 1).append("-")
                .append(day).append(" "));
    }

    private void setDateTimeField()
    {
        trpDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        trpDateDatePickerDlg = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                trpDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View view)
    {
        if(view == trpDate)
            trpDateDatePickerDlg.show();
        else if (view.getId() == R.id.new_trp_btn)
        {
            frm = this.actfrm.getEditableText().toString();
            to = this.actto.getEditableText().toString();
            dt = this.trpDate.getEditableText().toString();
            CareLessTrip trp = new CareLessTrip(frm,to,dt);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(newTrpBtn.getWindowToken(),0);
            //lstTripId = db.insertTrip(trp);

            Intent intent=new Intent(this,CareLessArlnList.class);

            intent.putExtra("from",frm);
            intent.putExtra("to",to);
            intent.putExtra("date",dt);
            startActivity(intent);
            Log.i("TRIP_ID","ID"+lstTripId);
        }
        else if (view.getId() == R.id.lst_trip_btn)
        {
            CareLessTrip trp;
            trp = db.getLstTripOnDate(lstTripId);
            lstFrm.setText(trp.getFrom());
            lstTo.setText(trp.getTo());
            lstTrpDate.setText(trp.getDate());
        }
    }

    /*public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
