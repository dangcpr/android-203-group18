package com.example.tinderforit;

import static androidx.core.content.PermissionChecker.checkCallingOrSelfPermission;
import static com.facebook.FacebookSdk.getApplicationContext;
import java.util.Random;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sendbird.calls.AcceptParams;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.CallOptions;
import com.sendbird.calls.DialParams;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;
import com.sendbird.calls.SendBirdVideoView;
import com.sendbird.calls.User;
import com.sendbird.calls.handler.AuthenticateHandler;
import com.sendbird.calls.handler.DialHandler;
import com.sendbird.calls.handler.DirectCallListener;
import com.sendbird.calls.handler.SendBirdCallListener;

import org.webrtc.RendererCommon;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class CallActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    DirectCall mCurentCall = null;

    String APP_ID = "4E01ADEA-4117-498D-B03D-05AD66B6A149";
    String USER_ID = uid;


    String ACCESS_TOKEN = null;

    //Intent intent = getIntent();
    String CALLEE_ID;

    private ThreadLocalRandom rand;
    String UNIQUE_HANDLER_ID = String.valueOf(1000000 + (int)(Math.random() * ((9999999 - 1000000) + 1)));

    Button butConnect;
    Button butMakeCall;
    SendBirdVideoView mVideoViewFullScreen;
    SendBirdVideoView mVideoViewSmall;
    //View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        Intent intent = getIntent();
        CALLEE_ID = intent.getStringExtra("userUID_Call");

        butConnect = (Button) findViewById(R.id.butConnect);
        butConnect.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Run this method when this button is clicked
                        connect();
                    }
                });
        // Make call button
        butMakeCall = (Button) findViewById(
                R.id.butMakeCall);
        butMakeCall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Run this method when this button is clicked
                        makeCall();
                    }
                });

        // Video (remove)
        mVideoViewFullScreen = findViewById(
                R.id.video_view_fullscreen);
        mVideoViewFullScreen.setScalingType(
                RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mVideoViewFullScreen.setZOrderMediaOverlay(false);
        mVideoViewFullScreen.setEnableHardwareScaler(true);
        // Video (my video)
        mVideoViewSmall = findViewById(R.id.video_view_small);
        mVideoViewSmall.setScalingType(
                RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mVideoViewSmall.setZOrderMediaOverlay(true);
        mVideoViewSmall.setEnableHardwareScaler(true);
    }

    /**
     * Hàm kết nối với Sendbird
     */
    private void connect() {
        // Initialize SendBirdCall
        SendBirdCall.init(
                getApplicationContext(), APP_ID);
        // Authenticate user
        authenticateUser();
    }

    /**
     * Xác thực người dùng từ Sendbird
     */
    private void authenticateUser() {
        // The USER_ID below should be
        // unique to your Sendbird application.
        AuthenticateParams params =
                new AuthenticateParams(USER_ID)
                        .setAccessToken(ACCESS_TOKEN);

        // Authenticate user
        SendBirdCall.authenticate(
                params, new AuthenticateHandler() {
                    @Override
                    public void onResult(User user, SendBirdException e) {
                        if (e != null) {
                            // Invalid user!
                            Toast.makeText(CallActivity.this, "Invalid user", Toast.LENGTH_LONG);
                        } else {
                            // User is valid!
                            Toast.makeText(CallActivity.this, "Invalid user", Toast.LENGTH_LONG);

                            waitForCalls();
                            checkPermissions();
                        }
                    }
                });
    }


    /**
     * You need to check if permissions are given
     * for your accessing your camera
     */
    private void checkPermissions() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        //String p
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(deniedPermissions.toArray(new String[0]),
                        REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                Log.e("VideoChat",
                        "[VideoChatActivity] PERMISSION_DENIED");
            }
        }
    }
    DirectCall call = null;
    private void makeCall() {

        if (mCurentCall == null) {
            CallOptions callOptions = new CallOptions()
                    .setLocalVideoView(mVideoViewSmall)
                    .setRemoteVideoView(mVideoViewFullScreen)
                    .setVideoEnabled(true)
                    .setAudioEnabled(true);

            DialParams params = new DialParams(CALLEE_ID);
            params.setVideoCall(true);
            params.setCallOptions(callOptions);

            call = SendBirdCall.dial(params, new DialHandler() {
                @Override
                public void onResult(DirectCall call, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                        // Handle this error!
//                        Toast.makeText(CallActivity.this,
//                                "Error dialing.",
//                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            Toast.makeText(CallActivity.this, "You are calling" , Toast.LENGTH_LONG).show();
        }
        else {
            if (mCurentCall.isOngoing()) {
                mCurentCall.end();
                mCurentCall = null;
                mVideoViewSmall.setVisibility(View.GONE);
                mVideoViewFullScreen.setVisibility(View.GONE);
                // Show Make Call button
                butConnect.setVisibility(View.VISIBLE);
            }
            return;
        }


        call.setListener(new DirectCallListener() {
            @Override
            public void onEstablished(DirectCall call) {
                // Call was established!
                mCurentCall = call;
                setVideoOnceCallIsConnected(call);
                // Hide Make Call button
                butConnect.setVisibility(View.GONE);
                Toast.makeText(CallActivity.this, "You are calling", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onConnected(DirectCall call) {
                // The call was connected to the other device
                Toast.makeText(CallActivity.this, "You was connected", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onEnded(DirectCall call) {
                // This call ended
//                mCurentCall.end();
//                mCurentCall = null;
//                //directCall.end();
//                mVideoViewSmall.setVisibility(View.GONE);
//                mVideoViewFullScreen.setVisibility(View.GONE);
//                // Show Make Call button
//                butConnect.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setVideoOnceCallIsConnected(DirectCall call) {
        mVideoViewSmall.setVisibility(View.VISIBLE);
        mVideoViewFullScreen.setVisibility(View.VISIBLE);
        call.setRemoteVideoView(mVideoViewFullScreen);
        call.setLocalVideoView(mVideoViewSmall);
    }



    private void firebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(CallActivity.this, new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                Log.i("FCM Token", token);
                SendBirdCall.registerPushToken(token, false, e -> {
                    if (e != null) {
                        Log.i("SB: ",
                                "[PushUtils] registerPushToken() => e: " + e.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Wait for remote calls
     */
    private void waitForCalls(){
        SendBirdCall.removeAllListeners();
        SendBirdCall.addListener(UNIQUE_HANDLER_ID, new SendBirdCallListener() {
            @Override
            public void onRinging(DirectCall call) {
                call.setListener(new DirectCallListener() {
                    @Override
                    public void onEstablished(DirectCall call) {
                        Log.i("SB: ", "Call established");
                        mCurentCall = call;
                        // Start to show video
                        setVideoOnceCallIsConnected(call);
                        // Hide Make Call button
                        butConnect.setVisibility(View.GONE);
                        //Toast.makeText(getActivity(), "You have a call", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onConnected(DirectCall call) {
                        Log.i("SB: ", "Call connected to the other peer");
                        mCurentCall = call;
                        Toast.makeText(CallActivity.this, "You were connected" , Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onEnded(DirectCall call) {
                        Log.i("SB: ", "Call ended");
                        Toast.makeText(CallActivity.this, "Call ended", Toast.LENGTH_LONG);
//                        mCurentCall.end();
                        //call.end();
                        mCurentCall = null;
                        // Hide view view
                        mVideoViewSmall.setVisibility(View.GONE);
                        mVideoViewFullScreen.setVisibility(View.GONE);
                        // Show Make Call button
                        butConnect.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onRemoteAudioSettingsChanged(DirectCall call) {
                        Log.i("SB: ", "Remote audio settings changed");
                    }
                });
                CallOptions callOptions = new CallOptions()
                        .setLocalVideoView(mVideoViewSmall)
                        .setRemoteVideoView(mVideoViewFullScreen)
                        .setVideoEnabled(true)
                        .setAudioEnabled(true);
                call.accept(new AcceptParams().setCallOptions(callOptions));
            }
        });
        /** You can define your own sounds for your calls
         *
         SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, R.raw.dialing);
         SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, R.raw.ringing);
         SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, R.raw.reconnecting);
         SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, R.raw.reconnected);
         */
        firebaseToken();
    }

}