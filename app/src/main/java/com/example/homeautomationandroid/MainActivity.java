package com.example.homeautomationandroid;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    String address = null;
    String name = null;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket = null;
    Set<BluetoothDevice> pairedDevice;
    Switch light_1_sw;
    TextView deviceName;
    ImageButton imageButton;
    static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceName = findViewById(R.id.deviceNameTv);
        imageButton = findViewById(R.id.imageButton);
        light_1_sw = findViewById(R.id.switch1);

        try {
            bluetoothConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        light_1_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(MainActivity.this, "checked", Toast.LENGTH_SHORT).show();
                    turnOnLed("on");
                }
                else{
                    turnOffLed("of");
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                try{
                    startActivityForResult(intent,1);
                }catch (ActivityNotFoundException a){
                    Toast.makeText(MainActivity.this, "Speech not support in this device", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK && data != null){
                    ArrayList<String> s = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //deviceName.setText(s.get(0));
                    String ss = s.get(0);
                    //Toast.makeText(this, "Try again not recognize"+ss, Toast.LENGTH_SHORT).show();
                    //if(ss == "on"){
                        turnOnLed(ss);
                    //}else if(ss == "off"){
                    //    turnOffLed(ss);
                    //}else {
                    //    Toast.makeText(this, "Try again not recognize "+ss, Toast.LENGTH_SHORT).show();
                   // }
                }
        }
    }

    private void turnOffLed(String s) {
        if (bluetoothSocket != null){

            try {
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(s.getBytes());
                Toast.makeText(MainActivity.this, "sent succ", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "sent unsucc"+e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void turnOnLed(String s) {
        //Toast.makeText(this, "Socket not null", Toast.LENGTH_SHORT).show();


                   // Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
        if (bluetoothSocket != null){

                    try {
                        //bluetoothSocket.getOutputStream().write("1".toString().getBytes());
                       OutputStream outputStream = bluetoothSocket.getOutputStream();
                       outputStream.write(s.getBytes());
                       //outputStream.flush();
                        Toast.makeText(MainActivity.this, "sent succ", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "sent unsucc"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
}

    private void bluetoothConnect() throws IOException {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//mobile bluetooth device
        try{
            address = bluetoothAdapter.getAddress();
            pairedDevice = bluetoothAdapter.getBondedDevices();
            if (pairedDevice.size()>0){
                for (BluetoothDevice bt : pairedDevice){
                    address = bt.getAddress().toString();
                    name = bt.getName().toString();
                    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
        }

        Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
        deviceName.setText(name+" "+address) ;

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);//connects bluetooth device and check its available or not
        if (device.getBondState() == device.BOND_BONDED){
            bluetoothSocket =  device.createRfcommSocketToServiceRecord(uuid);//create a RCCOMM connection
            bluetoothSocket.connect();
            Toast.makeText(this, "Bond succ"+bluetoothSocket, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Bond unsucc", Toast.LENGTH_SHORT).show();
        }
    }
}
