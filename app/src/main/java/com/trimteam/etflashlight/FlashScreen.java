package com.trimteam.etflashlight;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera.Parameters;

import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.camera2.*;
import android.hardware.Camera;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class FlashScreen extends AppCompatActivity {
    private static Camera cam ;
    private static int i = 1;
    private ImageView imageView;
    private CameraCaptureSession mSession;
    private SurfaceTexture mPreviewTexture;
    private CameraManager mCameraManager;
    private String mCameraId;
    private CaptureRequest.Builder mBuilder;
    private RelativeLayout ll;
    // private CameraSupport cameraSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3410114126236036~5068724706");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ll = (RelativeLayout) findViewById(R.id.activityTouch);
        /* {
            cameraSupport=new CameraNew(this);
        } else {
            cameraSupport=new CameraOld();
        }*/
        imageView = (ImageView) findViewById(R.id.imageView);
        Boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {

            AlertDialog alert = new AlertDialog.Builder(FlashScreen.this)
                    .create();
            alert.setTitle("Error !!");
            alert.setMessage("Your device doesn't support flash light!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                    System.exit(0);
                }
            });
            alert.show();
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            cam = Camera.open();
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i == 1) {
                        imageView.setImageResource(R.drawable.light_on);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        Parameters p = cam.getParameters();
                        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        cam.setParameters(p);
                        cam.startPreview();
                        i = 2;

                    } else {
                        imageView.setImageResource(R.drawable.light_off);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        cam.stopPreview();
                        cam.release();
                        i = 1;


                    }

                }
            });

        }
        else{
            cameraSupport();
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void cameraSupport(){
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                try {
                    if (i == 2) {
                        turnOffFlashLight();
                        i = 1;
                    } else {
                        turnOnFlashLight();
                        i=2;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class MyCameraCaptureSessionStateCallback extends CameraCaptureSession.StateCallback
    {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onConfigured(CameraCaptureSession session)
        {
            mSession = session;
            try
            {
                mSession.setRepeatingRequest(mBuilder.build(), null, null);
            }
            catch (CameraAccessException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session)
        {

        }
    }
    public void turnOnFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageView.setImageResource(R.drawable.light_on);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mCameraManager.setTorchMode(mCameraId, true);


            }
            else{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setImageResource(R.drawable.light_on);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    cam = Camera.open();
                    Camera.Parameters p = cam.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    cam.setParameters(p);
                    mPreviewTexture = new SurfaceTexture(0);
                    try {
                        cam.setPreviewTexture(mPreviewTexture);
                    } catch (IOException ex) {
                        // Ignore
                    }
                    cam.startPreview();


                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnOffFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageView.setImageResource(R.drawable.light_off);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mCameraManager.setTorchMode(mCameraId, false);



            }
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setImageResource(R.drawable.light_off);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    cam.stopPreview();
                    cam.release();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(i == 2){
            turnOffFlashLight();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(i==2){
            turnOffFlashLight();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(i==2){
            turnOnFlashLight();
        }
    }
}


/*
 interface CameraSupport {
    CameraSupport open(int cameraId);
    int getOrientation(int cameraId);
}

@SuppressWarnings("deprecation")
 class CameraOld implements CameraSupport {

    private Camera camera;

    @Override
    public CameraSupport open(final int cameraId) {
        this.camera = Camera.open(cameraId);
        return this;
    }

    @Override
    public int getOrientation(final int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        return info.orientation;
    }
}

class CameraNew implements CameraSupport {

    private CameraDevice camera;
    private CameraManager manager;

    public CameraNew(final Context context) {
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public CameraSupport open(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            manager.openCamera(cameraIds[cameraId], new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    CameraNew.this.camera = camera;
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    CameraNew.this.camera = camera;
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    CameraNew.this.camera = camera;
                }
            }, null);
        } catch (Exception e) {
            // TODO handle
        }
        return this;
    }

    @Override
    public int getOrientation(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);
            return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            // TODO handle
            return 0;
        }
    }
}
*/


