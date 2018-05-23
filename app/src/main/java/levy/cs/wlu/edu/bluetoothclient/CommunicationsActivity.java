/*
Bluetooth communications activity

Works with BluetoothConnection to provide simple interaction with a sever over a Bluetooth socket:
seek bar (slider) sends serialized values to server; activity checks for available responses from
server.

Copyright 2018  Emily Boyes, Gunnar Bowman, Trip Calihan, Simon D. Levy, Shepherd Sims

MIT License
 */

package levy.cs.wlu.edu.bluetoothclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

public class CommunicationsActivity extends AppCompatActivity {


    String mDeviceAddress;
    BluetoothCommunicationsTask mBluetoothConnection;
    String mMessageFromServer = "";

    TextView mMessageTextView;
    SeekBar mSpeedSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_communications);

        // Retrieve the address of the bluetooth device from the BluetoothListDeviceActivity
        Intent newint = getIntent();
        mDeviceAddress = newint.getStringExtra(BluetoothDeviceListActivity.EXTRA_ADDRESS);

        // Create a connection to this device
        mBluetoothConnection = new BluetoothCommunicationsTask(this, mDeviceAddress);
        mBluetoothConnection.execute();

        mMessageTextView = (TextView)findViewById(R.id.serverReplyText);

        mSpeedSeekBar = (SeekBar)findViewById(R.id.seekBar);

        mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser==true) {
                    for (byte b : String.valueOf(progress).getBytes()) {
                        mBluetoothConnection.write(b);
                    }
                    mBluetoothConnection.write((byte)'.');

                    while (mBluetoothConnection.available() > 0) {

                        char c = (char)mBluetoothConnection.read();

                        if (c == '.') {

                            if (mMessageFromServer.length() > 0) {
                                mMessageTextView.setText(mMessageFromServer);
                                mMessageFromServer = "";
                            }
                        }
                        else {
                            mMessageFromServer += c;
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothConnection.disconnect();
    }

}