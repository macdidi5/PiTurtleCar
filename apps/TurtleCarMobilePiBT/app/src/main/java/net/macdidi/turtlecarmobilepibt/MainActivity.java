package net.macdidi.turtlecarmobilepibt;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    // App's Bluetooth service UUID
    private static final UUID SerialPortServiceClass_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT = 2;

    private BluetoothAdapter bluetoothAdapter;
    private boolean enableBluetooth = false;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private boolean isConnected = false;

    private JoyStickView joy_stick_view;
    private WebView webview;

    private Handler handler;
    private String webcamIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get BluetoothAdapter object
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If device does not support Bluetooth
        if (bluetoothAdapter == null) {
            finishNoBluetooth();
        }

        joy_stick_view = (JoyStickView) findViewById(R.id.joy_stick_view);

        JoyStickView.CallBack callBack = new JoyStickView.CallBack() {

            @Override
            public void control(ControlType action) {
                byte[] actionMessage = new byte[]{(byte)action.getCode()};

                if (connectedThread != null) {
                    connectedThread.write(actionMessage);
                }
            }

        };

        joy_stick_view.setCallBack(callBack);
        joy_stick_view.setEnabled(false);

        webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                webview.loadUrl("http://" + webcamIP + ":8080/javascript_simple.html");
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        // If device does not enable Bluetooth
        if (!enableBluetooth) {
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                enableBluetooth = true;
                // Start android system setting for enable Bluetooth
                Intent intent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public void onStop() {
        enableBluetooth = false;

        // If Bluetooth connected
        if (isConnected) {
            // Send stop command 'E' to Pi
            //connectedThread.write(new byte[]{(byte)'E'});
            // Stop connected thread
            connectedThread.cancel();
            // Disable Bluetooth
            //bluetoothAdapter.disable();
        }

        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                // Android system setting for enable Bluetooth
                case REQUEST_ENABLE_BT:
                    if (!bluetoothAdapter.isEnabled()) {
                        finishNoBluetooth();
                    }

                    break;
                // Select mbed device
                case REQUEST_CONNECT:
                    // Read Bluetooth device's address
                    String address = data.getStringExtra(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    processConnect(address);
                    break;
            }
        }
    }

    public void clickLight(View view) {
        if (isConnected) {
            connectedThread.write(new byte[]{(byte)'M'});
        }
    }

    private void processConnect(String address) {
        // Create BluetoothDevice using address
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Create and start ConnectThread
        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    private void finishNoBluetooth() {
        // Dialog for device does not support or enable Bluetooth
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.bluetooth_error_msg)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                finish();
                            }
                        });

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_connect) {
            // Start select device activity
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(intent, REQUEST_CONNECT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket bluetoothSocket;

        public ConnectThread(BluetoothDevice bluetoothDevice) {
            BluetoothSocket bluetoothSocket = null;

            try {
                // Create bluetooth socket object
                bluetoothSocket = bluetoothDevice.
                        createRfcommSocketToServiceRecord(
                                SerialPortServiceClass_UUID);
            }
            catch (IOException e) {
                Log.e(this.getName(), getString(R.string.create_error_msg));
            }

            this.bluetoothSocket = bluetoothSocket;
        }

        public void run() {
            setName("ConnectThread");

            // Stop bluetooth discover
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try {
                // Connect selected bluetooth device
                bluetoothSocket.connect();
            }
            catch (IOException e) {
                try {
                    bluetoothSocket.close();
                }
                catch (IOException e2) {
                    Log.e(this.getName(),
                            getString(R.string.close_error_msg));
                }

                return;
            }

            synchronized (MainActivity.this) {
                connectThread = null;
            }

            connected(bluetoothSocket);
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            }
            catch (IOException e) {
                Log.e(this.getName(), getString(R.string.close_error_msg));
            }
        }
    }

    public synchronized void connected(BluetoothSocket bluetoothSocket) {
        // Clear connect thread
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Clear connected thread
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Create and start connected thread
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
        // Send start command 'I' to Pi
        connectedThread.write(new byte[]{(byte)'I'});
        isConnected = true;

        joy_stick_view.setEnabled(true);
    }

    private class ConnectedThread extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
            }
            catch (IOException e) {
                Log.e(this.getName(), getString(R.string.stream_error_msg));
            }

            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        public void run() {
            // Create BufferedReader object
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);

            while (true) {
                try {
                    // Read and process data from Pi (IP Address)
                    webcamIP = br.readLine();

                    if (!webcamIP.substring(0, 1).matches("[0-9]")) {
                        webcamIP = webcamIP.substring(1);
                    }

                    handler.sendEmptyMessage(0);
                }
                catch (IOException e) {
                    Log.e(this.getName(), getString(R.string.disconnect_msg));
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
            }
            catch (IOException e) {
                Log.e(this.getName(), getString(R.string.write_error_msg));
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            }
            catch (IOException e) {
                Log.e(this.getName(), getString(R.string.close_error_msg));
            }
        }
    }

}
