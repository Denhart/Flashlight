package dk.denhart.flashlight;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.View.OnClickListener;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.ImageButton;

import java.io.IOException;

public class MyActivity extends Activity implements Callback {
    private boolean isLighOn = false;
    private Camera camera;
    SurfaceHolder mHolder;
    Parameters p;


    @Override
    protected void onStart() {
        super.onStart();
        SurfaceView preview = (SurfaceView)findViewById(R.id.PREVIEW);
        mHolder = preview.getHolder();
        mHolder.addCallback(this);
        camera = Camera.open();
        p = camera.getParameters();
    }


    protected void onRestart(){
        super.onRestart();
        if (camera != null) {
            camera.release();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            camera.open().reconnect();
        }catch (Exception e){

        }
        if(isLighOn)
            turnOnFlash();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ImageButton button = (ImageButton) findViewById(R.id.btnToggle);
        getActionBar().hide();
        Context context = this;
        PackageManager pm = context.getPackageManager();
        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MyActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("This device is not supported.");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (isLighOn) {
                    turnOffFlash();
                } else {
                    turnOnFlash();
                }
            }
        });
    }

    private void turnOnFlash(){
        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        camera.setParameters(p);
        camera.startPreview();
        isLighOn = true;

    }
    private void turnOffFlash() {
        p.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(p);
        camera.stopPreview();
        isLighOn = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        mHolder = holder;
        try {
            camera.setPreviewDisplay(mHolder);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        camera.stopPreview();
        mHolder = null;
    }
}
