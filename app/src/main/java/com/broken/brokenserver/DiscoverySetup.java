package com.broken.brokenserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;

public class DiscoverySetup extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_setup);
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
        bluesOn();

    }

    BluetoothAdapter blues;
    ArrayAdapter devices;
    public void bluesOn() {
        TextView textView = (TextView) findViewById(R.id.textView2);
        blues = BluetoothAdapter.getDefaultAdapter();
        if (blues == null) {

            textView.setText("Started the fuck over");

        }

        if (!blues.isEnabled()) {
            textView.setText("Not Enabled");
            blues.enable();
            textView.setText(textView.getText() + "\n Now Enabled");
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
        }
        Log.w("above bonded","Bonding get");
        Log.w("UUID",UUID.randomUUID().toString());
        Log.w("created UUID",new UUID(123456,56789).toString());
        Set<BluetoothDevice> pairedDevices = blues.getBondedDevices();
        if (pairedDevices.size() > 0) {
            ArrayList<String> pairs = new ArrayList();
            for (BluetoothDevice dev : pairedDevices) {
                pairs.add(dev.getName() + "\nand the address is " + dev.getAddress());
            }
            devices = new ArrayAdapter(this, R.layout.list, pairs);
            ListView placeholder = (ListView) findViewById(R.id.listView);
            placeholder.setAdapter(devices);
            placeholder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

            Log.w("discovery", "Started search...........");
            blues.startDiscovery();
            IntentFilter broad= new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(discover, broad);
            Log.w("discovery", "Started Receiver...........");
            CharSequence bb="Broadcast set";
            Context context = getApplicationContext();
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            Log.w("Shhhhh","Started Listening");
            new listenServer().start();


        }


    }

    private final BroadcastReceiver discover = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action=intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice deviceList=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.clear();
                devices.add(deviceList.getName() + " addresss" + deviceList.getAddress());
                Log.w("Received Broadcast","Added it to the list");


            }


        }


    };

    BluetoothSocket socket;
    BluetoothDevice device;;
    BluetoothServerSocket serverSocket;

    private class listenServer extends Thread
    {


        public listenServer()
        {
            try
            {
                java.util.UUID uuid= new UUID(1234,5678);

                //Set up the server
                serverSocket=blues.listenUsingRfcommWithServiceRecord("Broken", uuid);
            }

            catch(Exception e)
            {
                Log.w("ERROR",e.toString());
            }
        }

        public void run()
        {
            BluetoothSocket socket;
            while (true)
            {
                try
                {

                    Log.w("server", "lISTENING FOR CONNECTION");
                    socket = serverSocket.accept();
                    Log.w("CONNECT", "CONNECTED");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    Log.w("Bufffers", "First buffer created");
                    PrintWriter printer = new PrintWriter(socket.getOutputStream());
                    Log.w("Bufffers", "Second buffer created and about to read");
                    String message = reader.readLine();
                    if (message.startsWith("CALL"))
                    {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(message.replace("CALL", "tel")));
                        try
                        {
                            startActivity(intent);
                        }

                        catch (Exception exe)

                        {
                            Log.e("error", "could not call" + exe.toString());
                        }

                    }

                    Log.w("DATA", "Data read");
                    Log.w("Transmission", message);
                    printer.println("ack");
                    socket.close();
                    reader.close();
                    printer.close();

                }

                catch (Exception e)
                {
                    Log.w("Error", e.toString());
                }
            }


        }


    }

    public void manageConnection(BluetoothSocket socket)
    {

    }








       /* public void onActivityResult(int requestCode, int resultCode,Intent data )
        {
            TextView view=(TextView) findViewById(R.id.textView2);
            view.setText("Result received: "+resultCode+ "request"+requestCode);
        }*/


}




