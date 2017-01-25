package com.example.abuosama.asynctaskwithlistview;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {

    // 8 required decalred variable
    Button button;
    ListView listView;
    //ArrayList<Contacts> al;
    MyAsyncTask myAsyncTask;
    //MyAdapter myAdapter;
    MyDataBase myDataBase;
    Cursor cursor;
    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // myDataBase=new MyDataBase(getActivity());
//        myDataBase=new MyDataBase(getActivity());
//        myDataBase.open();
        myDataBase = new MyDataBase(getActivity());
        myDataBase.open();
    }

    public boolean checkInternet() {

        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //b. from network manger & get active network information

        //NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        //c.check if network connected or not
        if (networkInfo == null || networkInfo.isConnected() == false) {

            //means there is no internet
            //webview.loadData("<h1>No Internet check internet<h1>", "text/html", null);
            return false;
        }

        return true;
    }


    public class MyAsyncTask extends AsyncTask<String, Void, String> {
        URL myUrl;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        String line;
        StringBuilder stringBuilder;

        @Override
        protected String doInBackground(String... p1) {

            //12 write login for connceting to server  and get json data
            try {
                myUrl = new URL(p1[0]);
                httpURLConnection = (HttpURLConnection) myUrl.openConnection();
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                stringBuilder = new StringBuilder();
                line = bufferedReader.readLine();

                while (line != null) {

                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
                return stringBuilder.toString();//return final result json data to onpost execute


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("b/34", "Url is inpropper");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("b/34", "Network problem ");
            }


            return "sone ting went wrong";
        }

        @Override
        protected void onPostExecute(String s) {

            //reverse josn parsing

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject k = jsonArray.getJSONObject(i);
                    String name = k.getString("name");
                    String email = k.getString("email");
                    JSONObject ph1 = k.getJSONObject("phone");
                    String mobile = ph1.getString("mobile");
                    //push above data to arraylist
//                    Contacts c=new Contacts();
//                    c.setSno(""+(i+1));
//                    c.setName(name);
//                    c.setEmail(email);
//                    c.setMobile(mobile);
                    // al.add(c);
                    // myAdapter.notifyDataSetChanged();
                    myDataBase.insert(name, email, mobile);


                }
                cursor.requery();//for reflecting first time
            } catch (JSONException e) {
                e.printStackTrace();
            }


            super.onPostExecute(s);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_async, container, false);
        button = (Button) v.findViewById(R.id.button1);
        listView = (ListView) v.findViewById(R.id.listview1);
        cursor = myDataBase.queryContacts();
        myAsyncTask = new MyAsyncTask();
        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.row, cursor, new String[]{"_id", "name", "email", "mobile"},
                new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4});
        listView.setAdapter(simpleCursorAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check internet i f availble

                if (checkInternet()) {

//                    if(myAsyncTask.getStatus()== AsyncTask.Status.FINISHED||
//                            myAsyncTask.getStatus()==AsyncTask.Status.FINISHED) {
//
//                        Toast.makeText(getActivity(), "Already running ple wait", Toast.LENGTH_SHORT).show();
//                        return;
//
//                    }

                    myAsyncTask.execute("http://api.androidhive.info/contacts/");

                }

                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();


            }
        });


        return v;
    }


}
