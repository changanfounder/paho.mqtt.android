package org.eclipse.paho.android.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttPingSender;
import org.eclipse.paho.client.mqttv3.internal.ClientComms;

public class HandlerPingSender implements MqttPingSender {
    private static final String TAG = "HandlerPingSender";
    private static final int MSG_SEND_PING = 1;

    private ClientComms comms;
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_SEND_PING) {
                Log.d(TAG, "Sending Ping at: " + System.currentTimeMillis());
                if (comms != null) {
                    comms.checkForActivity();
                }
            }
            return false;
        }
    });

    @Override
    public void init(ClientComms comms) {
        Log.d(TAG, "init HandlerPingSender comms: " + comms);
        this.comms = comms;
    }

    @Override
    public void start() {
        String action = MqttServiceConstants.PING_SENDER + getClientId();
        Log.d(TAG, "start HandlerPingSender " + action);
        if (handler != null && comms != null) {
            handler.removeMessages(MSG_SEND_PING);
            handler.sendEmptyMessageDelayed(MSG_SEND_PING, comms.getKeepAlive());
        }
    }

    @Override
    public void stop() {
        String action = MqttServiceConstants.PING_SENDER + getClientId();
        Log.d(TAG, "stop HandlerPingSender " + action);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void schedule(long delayInMilliseconds) {
        Log.d(TAG, "schedule delayInMilliseconds: " + delayInMilliseconds);
        if (handler != null) {
            handler.removeMessages(MSG_SEND_PING);
            handler.sendEmptyMessageDelayed(MSG_SEND_PING, delayInMilliseconds);
        }
    }

    private String getClientId() {
        return comms != null ? comms.getClient().getClientId() : "";
    }
}
