package com.example.bzappv01;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    String[] partes ={"","",""};

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView temp= findViewById(R.id.valueTemp);
        TextView hum= findViewById(R.id.valueHum);
        TextView gas= findViewById(R.id.valueGas);

        final String host = "c7c798df03f14332b2a301a7a18c47c7.s1.eu.hivemq.cloud";
        final String username = "";
        final String password = "";
        final String[] payload = {""};



        // create an MQTT client
        final Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildBlocking();
        System.out.println("Connecting....");
        // connect to HiveMQ Cloud with TLS and username/pw
            client.connectWith()
                    .simpleAuth()
                    .username(username)
                    .password(UTF_8.encode(password))
                    .applySimpleAuth()
                    .send();

        System.out.println("Connected successfully");

        // subscribe to the topic "my/test/topic"
        client.subscribeWith()
                .topicFilter("blitzenz/BZ-1/out")
                .send();

        // set a callback that is called when a message is received (using the async API style)

        client.toAsync().publishes(ALL, publish -> {
            payload[0] = UTF_8.decode(publish.getPayload().get()).toString();
            System.out.println("Received message: " +
                        publish.getTopic() + " -> " +
                    payload[0]);
            runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    partes = payload[0].split(",");
                    temp.setText(partes[0].substring(0,4)+"ºC");
                    String[] tempColors= {"#8AE9FF", "#8AFFB5", "#95FF8A", "#C8FF8A", "#FFF58A"};
                    String[] lvlsTemp = {"-20", "10", "15", "20","25"};
                    int nivelGas;
                    for (nivelGas=0; Integer.parseInt(partes[2])>Integer.parseInt(lvls[nivelGas]) || nivelGas>lvls.length ; nivelGas++);
                    hum.setText(partes[1].substring(0,2)+"%");
                    String[] airQual= {"Excelente", "Buena", "Pobre", "Advertencia", "Peligro"};
                    String[] lvlsGas = {"400", "1000", "2000", "5000","99999999"};
                    int nivelGas;
                        for (nivelGas=0; Integer.parseInt(partes[2])>Integer.parseInt(lvlsGas[nivelGas]) || nivelGas>lvls.length ; nivelGas++);
                    gas.setText(airQual[nivelGas]+" ("+partes[2]+" PPM)");
                }
            });
        });

        // publish a message to the topic "my/test/topic"
            client.publishWith()
                    .topic("my/test/topic")
                    .payload(UTF_8.encode("hola"))
                    .send();

    }
}