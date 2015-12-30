package com.broken.brokenserver;

import android.content.Intent;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity
{

    int port, state;
    ServerSocket server;
    Socket con;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Log.w("Status","the activity has started");
        state = 1;

        new giveData().execute("just");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    @Override
    protected void onStop() {
        super.onStop();
        try {
            con.close();
        } catch (Exception e) {

        }

    }

    private class giveData extends AsyncTask<String, String, String> {
        TextView text = (TextView) findViewById(R.id.textView);
        protected String doInBackground(String... Params) {

            try {

                publishProgress(" \nstarting server...\n ");
                server = new ServerSocket(9000);
                publishProgress(" started server on port "+server.getLocalPort()+" And IP "+server.getLocalSocketAddress().toString());
                while (true) {

                    con = server.accept();
                    publishProgress(" Client has Connected\n ");
                    Log.w("Connect","Connected to the client\n");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    PrintWriter printer = new PrintWriter(con.getOutputStream());
                    publishProgress(" Buffers Initialized\n ");
                    publishProgress("The client's address is "+con.getRemoteSocketAddress().toString());
                    boolean eof = false;
                    String message;
                    publishProgress("\n Reading Message ");
                    message = reader.readLine();

                    publishProgress(message);
                    printer.println("ack"+con.getLocalAddress().getHostAddress());
                    printer.close();
                    reader.close();
                    con.close();



                }
            } catch (Exception e) {
                publishProgress(e.toString());
                StackTraceElement[] traceEle=e.getStackTrace();
                for(int i=0;i<traceEle.length;i++) {

                    publishProgress("Exception on line " + traceEle[0].getLineNumber());
                }

            }

            return server.getInetAddress().getHostAddress();
        }


        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);
            if(values[0].contains("CALL"))
            {
                Intent intent= new Intent(Intent.ACTION_CALL,Uri.parse(values[0].replace("CALL","tel")));
                try
                {
                    startActivity(intent);
                }
                catch (Exception exe)
                {
                    Log.e("error","could not call"+ exe.toString());
                }
            }
            text.setText(text.getText() + values[0]);

            }
        }

        protected void onPostExecute(String result) {
            TextView text = (TextView) findViewById(R.id.textView);
            text.setText(text.getText() + result);


        }

    }



