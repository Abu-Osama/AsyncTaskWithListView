package com.example.abuosama.asynctaskwithlistview;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class DataBaseWithRecycFragment extends Fragment {


    Button button;
    // ListView listView;
    // ArrayList<Contacts> al;
    MyAsyncTask myAsyncTask;
    RecyclerView recyclerView;
    DataBaseWithRecycFragment dataBaseWithRecycFragment;
    MyDataBase myDataBase;
    MyRecyclerView myRecyclerView;
    Cursor c;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDataBase = new MyDataBase(getActivity());
        myDataBase.open();
    }
    // AsyncFragment.MyAsyncTask myAsyncTask;
    // AsyncFragment.MyAdapter myAdapter;

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

                    myDataBase.insert(name, email, mobile);
                    // myRecyclerView.notifyDataSetChanged();
                }
                myRecyclerView.notifyDataSetChanged();
                c.requery();

            } catch (JSONException e) {
                e.printStackTrace();
            }


            super.onPostExecute(s);
        }
    }


    //create an inner class for custom adapter fot recycler view

    public class MyRecyclerView extends RecyclerView.Adapter<MyRecyclerView.ViewHolder> {


        @Override
        public MyRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = getActivity().getLayoutInflater().inflate(R.layout.row, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            c.moveToPosition(position);
            int sno = c.getInt(0);
            String name = c.getString(1);
            String email = c.getString(2);
            String mobile = c.getString(3);
            //apply data to

            holder.tv1.setText("" + sno);
            holder.tv2.setText(name);
            holder.tv3.setText(email);
            holder.tv4.setText(mobile);

        }

//        @Override
//        public void onBindViewHolder(RecyclerFragment.MyRecyclerView.ViewHolder holder, int position) {
//            //get data from cursor based on position
//
//            c.moveToPosition(position);
//            int sno=c.getInt(0);
//            String name=c.getString(1);
//            String email=c.getString(2);
//            String mobile=c.getString(3);
//            //apply data to
//
//            holder.tv1.setText(""+sno);
//            holder.tv2.setText(name);
//            holder.tv3.setText(email);
//            holder.tv4.setText(mobile);
//
//        }

        @Override
        public int getItemCount() {
            return c.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // here initialize textview
            public TextView tv1, tv2, tv3, tv4;

            public ViewHolder(View itemView) {
                super(itemView);
                tv1 = (TextView) itemView.findViewById(R.id.textView1);
                tv2 = (TextView) itemView.findViewById(R.id.textView2);
                tv3 = (TextView) itemView.findViewById(R.id.textView3);
                tv4 = (TextView) itemView.findViewById(R.id.textView4);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_data_base_with_recyc, container, false);
        button = (Button) v.findViewById(R.id.button1);
        //listView= (ListView) v.findViewById(R.id.listview1);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView1);
        c = myDataBase.queryContacts();
        // al = new ArrayList<Contacts>();
        myAsyncTask = new MyAsyncTask();
        myRecyclerView = new MyRecyclerView();
        recyclerView.setAdapter(myRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        // myAsyncTask=new AsyncFragment.MyAsyncTask();
        //myAdapter=new AsyncFragment.MyAdapter();
        //listView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check internet i f availble

                if (checkInternet()) {

                    if (myAsyncTask.getStatus() == AsyncTask.Status.FINISHED ||
                            myAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {

                        Toast.makeText(getActivity(), "Already running ple wait", Toast.LENGTH_SHORT).show();
                        return;

                    }

                    myAsyncTask.execute("http://api.androidhive.info/contacts/");

                }

                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();


            }
        });


        return v;
    }


}
