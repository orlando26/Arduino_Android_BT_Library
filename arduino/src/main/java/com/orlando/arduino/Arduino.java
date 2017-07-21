package com.orlando.arduino;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by orlando on 21/11/16.
 */
public class Arduino {
    Activity activity;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket;
    private ConnectAsyncTask connectAsyncTask;
    private BluetoothDevice device;
    private boolean conected = false;

    public Arduino(Activity activity){
        this.activity = activity;
        //Get Bluettoth Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Check smartphone support Bluetooth
        if(mBluetoothAdapter == null){
            //Device does not support Bluetooth
            Toast.makeText(activity, "Not support bluetooth", Toast.LENGTH_SHORT).show();
            activity.finish();
        }

        // Check Bluetooth enabled
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 1);
        }
    }
    public boolean getConected(){
        return conected;
    }

    public Set<BluetoothDevice> getDevices(){
        return mBluetoothAdapter.getBondedDevices();
    }

    public void connect(){

        // Make the connection
        if(conected == false){
            // Instance AsyncTask
            connectAsyncTask = new ConnectAsyncTask();
            // Create the BT device object and search by address
            Set<BluetoothDevice> btDevices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice dev: btDevices ) {
                if(dev.getName().equals("HC-05")){
                    device = dev;
                    break;
                }
            }
            //device = mBluetoothAdapter.getRemoteDevice("93:D3:31:70:79:C8");
            connectAsyncTask.execute(device);
        }else{
            Toast.makeText(activity, "Device is connected", Toast.LENGTH_SHORT).show();
        }
    }
    public void disconnect(){
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conected = false;
    }


    public void write(String command){
        OutputStream mmOutStream = null;

        try {

            if(btSocket.isConnected()){
                mmOutStream = btSocket.getOutputStream();
                mmOutStream.write(new String(command).getBytes());
            }

        } catch (IOException e) {

        } catch(NullPointerException npe){

        }
    }
    class ConnectAsyncTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {

        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... device) {

            mmDevice = device[0];

            try {

                String mmUUID = "00001101-0000-1000-8000-00805F9B34FB";
                mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(mmUUID));
                mmSocket.connect();

            } catch (Exception e) { }

            return mmSocket;
        }

        @Override
        protected void onPostExecute(BluetoothSocket result) {

            btSocket = result;
            conected = true;


        }
    }
}
