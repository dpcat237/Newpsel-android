package com.dpcat237.nps.behavior;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Class that represents our connection to the phone.
 */
public class PhoneConnection {
    private static final String TAG = "PhoneConnection";
    private GoogleApiClient googleApiClient;
    private boolean isConnected;
    private String phoneNode;

    public void setup(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle connectionHint) {
                    Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(
                            new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                                @Override
                                public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                    for (Node node : getConnectedNodesResult.getNodes()) {
                                        phoneNode = node.getId();
                                        break;
                                    }
                                    isConnected = true;
                                }
                            });
                }
                @Override
                public void onConnectionSuspended(int cause) {
                    isConnected = false;
                }
            })
            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {
                    isConnected = false;
                }
            })
            .addApi(Wearable.API)
            .build();
    }

    public void start() {
        googleApiClient.connect();
    }

    public void stop() {
        googleApiClient.disconnect();
    }

    public void sendMessage(Message msg) {
        if (!isConnected) {
            throw new IllegalStateException("Cannot send message while not connected to phone.");
        } else {
            Log.d(TAG, "tut: sendMessage "+msg.getPath());
            Wearable.MessageApi.sendMessage(googleApiClient, phoneNode, msg.getPath(), msg.getPayload());

            //PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(googleApiClient, phoneNode, msg.getPath(), msg.getPayload());
            /*messageResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    Status status = sendMessageResult.getStatus();
                    Log.d(TAG, "tut: Status: " + status.toString());
                    if (status.getStatusCode() != WearableStatusCodes.SUCCESS) {
                        Log.d(TAG, "tut: not SUCCESS");
                    }
                }
            });*/
        }
    }

    public static class Message {
        private final String path;
        private final byte[] payload;

        public Message(String path) {
            this.path = path;
            this.payload = null;
        }

        public Message(String path, byte[] payload) {
            this.path = path;
            this.payload = payload;
        }

        public String getPath() {
            return path;
        }

        public byte[] getPayload() {
            return payload;
        }
    }
}
