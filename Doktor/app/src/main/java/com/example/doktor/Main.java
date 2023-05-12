package com.example.doktor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class Main extends Activity implements MqttCallback {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private static final String TAG = "ServerBest";

    boolean notify;
    Vibrator vibrator;

    MqttAndroidClient client;
    String globalIP;
    String Phonenumber;

    TextView pr, pri, summ;

    boolean save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainnn);
        pr = findViewById(R.id.product);
        pri = findViewById(R.id.price);
        summ = findViewById(R.id.sum);

        setIP();


        notify = PrefConfig.loadNotify(getApplicationContext());

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        save = PrefConfig.loadTime(this);


        //MQTTConnect options : setting version to MQTT 3.1.1
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("admin");
        options.setPassword("".toCharArray());

        String mqttUrl = "tcp://" + globalIP + ":1883";
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(getApplicationContext(), mqttUrl,
                        clientId);
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(getApplicationContext(), "Üstünlikli baglanyldy", Toast.LENGTH_SHORT).show();
                    //Subscribing to a topic car/gpio/status on broker mqtt.flespi.io
                    client.setCallback(Main.this);
                    final String subTopic = "#";

                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(subTopic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                Toast.makeText(getApplicationContext(), "Serwere baglanylmady" + subTopic, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
//                            Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
//                            Snackbar.make(requireActivity().findViewById(R.id.constraint), R.string.no_internet, Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Internet baglanyşygy ýok", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // haysy topikden okamalydygyny kesgitleyan yeri
        if (topic.equals("product")) {
           pr.setText(new String(message.getPayload()));
        }
        else if (topic.equals("price")){
            pr.setText(new String(message.getPayload()));
        }
        else if (topic.equals("sum")){
            summ.setText(new String(message.getPayload()));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


    private void setIP() {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefs.putString(getString(R.string.mqtt_host), Utils.getIPAddress(true));
        prefs.apply();
        globalIP = Utils.getIPAddress(true);
    }

    protected void sendSMSMessage() {
        SmsManager smsManager=SmsManager.getDefault();
        smsManager.sendTextMessage(Phonenumber, null, "Howsala ! Yangyn Doredi... ", null, null);
        //Toast.makeText(getApplicationContext(), "Sms habar ugradyldy.",
                //Toast.LENGTH_LONG).show();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage("+99362850229", null, "sms", null, null);
//                    Toast.makeText(getApplicationContext(), "Sms habar ugradyldy.",
//                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Näsazlyklar ýüze çykdy täzeden synanyşyň.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
}


