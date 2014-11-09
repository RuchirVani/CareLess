package com.toinfinityandbeyond.pankaj.careless;
/**
 * Created by pankaj on 8/11/14.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class CareLessArlnList extends  Activity implements View.OnClickListener
{
    private ProgressDialog pDialog;
    ArrayList<String> deprtsAtList;
    ArrayList<String> arrvsAtList;
    ArrayList<String> airLineList;
    ArrayList<String> flgtNoList;
    ArrayList<String> priceList;
    private TextView frmTitle;
    private TextView toTitle;
    private TextView flghtNm1;
    private TextView flghtNm2;
    private TextView flghtNm3;
    private TextView dptTm1;
    private TextView dptTm2;
    private TextView dptTm3;
    private TextView arrTm1;
    private TextView arrTm2;
    private TextView arrTm3;
    private TextView prize1;
    private TextView prize2;
    private TextView prize3;
    private RelativeLayout rlLyot1;
    private RelativeLayout rlLyot2;
    private RelativeLayout rlLyot3;
    private static final String LOG_TAG = "CareLess";
    private static final String FLIGHT_SEARCH_API_BASE = "http://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";
    private static final String  DIRECT_FLIGHT = "&direct=true";
    private static final String  FLIGHT_CLASS = "&travel_class=ECONOMY";
    private static final String  NUM_RSLTS = "&number_of_results=3";
    private static final String API_KEY = "9FVBpJDSBogQAEIi1PVrHn8AX1cznZMm";
    private static final String FLIGHT_ORGN = "origin=";
    private static final String FLIGHT_DEST = "&destination=";
    private static final String FLIGHT_DEPT_DT = "&departure_date=";
    private static String frm = null;
    private static String to = null;
    private static String dt = null;
    private static String url=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flghtlist_activity);
        findViewsById();
        Log.i("FlightList","new activity");
        //Calling Async task for JSON
        Intent intent=getIntent();
        Log.i("LogIntent","log");
        frm = intent.getStringExtra("from").substring(intent.getStringExtra("from").length()-4,intent.getStringExtra("from").length()).substring(0,3);
        to = intent.getStringExtra("to").substring(intent.getStringExtra("to").length()-4,intent.getStringExtra("to").length()).substring(0,3);
        Log.i(frm,to);
        dt = intent.getStringExtra("date");
        frmTitle.setText(frm);
        toTitle.setText(to);
        url = FLIGHT_SEARCH_API_BASE + FLIGHT_ORGN + frm + FLIGHT_DEST + to + FLIGHT_DEPT_DT + dt + DIRECT_FLIGHT + FLIGHT_CLASS + NUM_RSLTS + "&apikey=" + API_KEY;
        new GetArlnsInfo().execute();
    }

    private void findViewsById()
    {
        flghtNm1 = (TextView) findViewById(R.id.flght_nm_1);
        flghtNm2 = (TextView) findViewById(R.id.flght_nm_2);
        flghtNm3 = (TextView) findViewById(R.id.flght_nm_3);
        dptTm1 = (TextView) findViewById(R.id.dept_tm_1);
        dptTm2 = (TextView) findViewById(R.id.dept_tm_2);
        dptTm3 = (TextView) findViewById(R.id.dept_tm_3);
        arrTm1 = (TextView) findViewById(R.id.arrv_tm_1);
        arrTm2 = (TextView) findViewById(R.id.arrv_tm_2);
        arrTm3 = (TextView) findViewById(R.id.arrv_tm_3);
        prize1 = (TextView) findViewById(R.id.prc_1);
        prize2 = (TextView) findViewById(R.id.prc_2);
        prize3 = (TextView) findViewById(R.id.prc_3);
        rlLyot1 = (RelativeLayout) findViewById(R.id.flght_lst_1);
        rlLyot2 = (RelativeLayout) findViewById(R.id.flght_lst_2);
        rlLyot3 = (RelativeLayout) findViewById(R.id.flght_lst_3);
        frmTitle = (TextView) findViewById(R.id.frm_title);
        toTitle = (TextView) findViewById(R.id.to_title);
    }

    @Override
    public void onClick(View v)
    {

    }

    private class GetArlnsInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("preexec","exece");
            // Showing progress dialog
            pDialog = new ProgressDialog(CareLessArlnList.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            StringBuilder jsonStr = new StringBuilder();
            Log.i("doinbackgrnd","entered");
            // Making a request to url and getting response
            //String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            try {
                URL fnlUrl = new URL(url);
                Log.i("urlRequest","entered");
                HttpURLConnection conn = (HttpURLConnection) fnlUrl.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                int read;
                char[] buff= new char[1024];
                while((read = in.read(buff)) != -1)
                {
                    jsonStr.append(buff,0,read);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    Log.i("jasonparser","entered");
                    JSONObject jsnObj = new JSONObject(jsonStr.toString());
                    JSONArray jsnArray = jsnObj.getJSONArray("results");
                    //JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                    // Extract the Place descriptions from the results
                    deprtsAtList = new ArrayList<String>(jsnArray.length());
                    arrvsAtList = new ArrayList<String>(jsnArray.length());
                    flgtNoList = new ArrayList<String>(jsnArray.length());
                    airLineList = new ArrayList<String>(jsnArray.length());
                    for (int i = 0; i < jsnArray.length(); i++) {
                        //JSONObject jsnObj = jsnArray.getJSONObject(i);
                        for(int j=0;j<jsnArray.getJSONObject(i).getJSONArray("itineraries").length();j++)
                        {
                            for(int k=0;k<jsnArray.getJSONObject(i).getJSONArray("itineraries").getJSONObject(j).getJSONObject("outbound").getJSONArray("flights").length();k++)
                            {
                                deprtsAtList.add(k, jsnArray.getJSONObject(i).getJSONArray("itineraries").getJSONObject(j).getJSONObject("outbound").getJSONArray("flights").getJSONObject(k).getString("departs_at"));
                                arrvsAtList.add(k, jsnArray.getJSONObject(i).getJSONArray("itineraries").getJSONObject(j).getJSONObject("outbound").getJSONArray("flights").getJSONObject(k).getString("arrives_at"));
                                flgtNoList.add(k, jsnArray.getJSONObject(i).getJSONArray("itineraries").getJSONObject(j).getJSONObject("outbound").getJSONArray("flights").getJSONObject(k).getString("flight_number"));
                                airLineList.add(k, jsnArray.getJSONObject(i).getJSONArray("itineraries").getJSONObject(j).getJSONObject("outbound").getJSONArray("flights").getJSONObject(k).getString("operating_airline"));
                                //priceList.add(k,jsnArray.getJSONObject(i).getJSONObject("fare").getJSONObject("total_price").toString());
                            }
                        }
                    }
                    Log.i("dfg",airLineList.get(1));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i("abc",airLineList.get(1));
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.i("onPostExecute","Execute");
            flghtNm1.setText(airLineList.get(0)+" - "+flgtNoList.get(0));
            flghtNm2.setText(airLineList.get(1)+" - "+flgtNoList.get(1));
            flghtNm3.setText(airLineList.get(2)+" - "+flgtNoList.get(2));
            dptTm1.setText(deprtsAtList.get(0));
            dptTm2.setText(deprtsAtList.get(1));
            dptTm3.setText(deprtsAtList.get(2));
            arrTm1.setText(arrvsAtList.get(0));
            arrTm2.setText(arrvsAtList.get(1));
            arrTm3.setText(arrvsAtList.get(2));
            prize1.setText("879.60");
            prize1.setText("1236.54");
            prize1.setText("2314.54");

        }
    }
}
