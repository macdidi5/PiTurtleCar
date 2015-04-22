package net.macdidi.turtlecarmobilepi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_CONNECT = 0;
    private boolean processMenu = false;

    public static final String TOPIC = "TurtleCar";
    public static final String TOPIC_STATUS = "TurtleCarData";
    public static final int QOS = 0;
    public static final int TIMEOUT = 3;

    private static String clientId = "TurtleCarAndroid";
    private static MqttClient mqttClient;

    private JoyStickView joy_stick_view;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joy_stick_view = (JoyStickView) findViewById(R.id.joy_stick_view);

        JoyStickView.CallBack callBack = new JoyStickView.CallBack() {

            @Override
            public void control(ControlType action) {
                String content = String.valueOf(action.getCode());

                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(QOS);

                try {
                    mqttClient.publish(TOPIC, message);
                }
                catch (MqttException me) {
                    Log.d(getClass().getName(), me.toString());
                }
            }

        };

        joy_stick_view.setCallBack(callBack);
        joy_stick_view.setEnabled(false);

        webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CONNECT) {
                String brokerIp = data.getStringExtra("brokerIp");
                String brokerPort = data.getStringExtra("brokerPort");
                String webcamIp = data.getStringExtra("webcamIp");

                processConnect(brokerIp, brokerPort);
                joy_stick_view.setEnabled(true);
                webview.loadUrl("http://" + webcamIp + ":8080/javascript_simple.html");
            }
        }

        processMenu = false;
    }

    @Override
    public void onDestroy() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
            }
            catch (MqttException me) {
                Log.d(getClass().getName(), me.toString());
            }
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.connect_menu) {
            if (!processMenu) {
                processMenu = true;

                startActivityForResult(new Intent(this, ConnectActivity.class),
                        REQUEST_CONNECT);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void processConnect(String brokerIp, String brokerPort) {
        String broker = "tcp://" + brokerIp + ":" + brokerPort;

        try {
            clientId = clientId + System.currentTimeMillis();

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setConnectionTimeout(TIMEOUT);

            mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            mqttClient.connect(mqttConnectOptions);
            mqttClient.subscribe(TOPIC_STATUS);

            Toast.makeText(this, R.string.connected, Toast.LENGTH_LONG).show();
        }
        catch (MqttException me) {
            Toast.makeText(this, R.string.connect_failure, Toast.LENGTH_LONG).show();
        }
    }

}
