package com.carlospinan.googlemobilevision;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.carlospinan.googlemobilevision.views.CameraSurfaceView;
import com.carlospinan.googlemobilevision.views.FaceDetectionView;

public class MainActivity extends AppCompatActivity {

    private CameraSurfaceView cameraSurfaceView;
    private FaceDetectionView faceDetectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        faceDetectionView = (FaceDetectionView) findViewById(R.id.faceDetectionView);

        final Button loadCameraButton = (Button) findViewById(R.id.loadCameraButton);
        loadCameraButton.setVisibility(View.GONE);

        cameraSurfaceView.setListener(new CameraSurfaceView.CameraSurfaceListener() {
            @Override
            public void onPictureTaken(Bitmap bitmap) {
                loadCameraButton.setVisibility(View.VISIBLE);
                faceDetectionView.setVisibility(View.VISIBLE);
                cameraSurfaceView.setVisibility(View.GONE);
                faceDetectionView.setContent(bitmap);
            }
        });

        cameraSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cameraSurfaceView.isEnabled()) {
                    cameraSurfaceView.setEnabled(false);
                    cameraSurfaceView.captureImage();
                    return true;
                }
                return false;
            }
        });

        loadCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceDetectionView.release();
                faceDetectionView.setVisibility(View.GONE);
                cameraSurfaceView.setVisibility(View.VISIBLE);
                cameraSurfaceView.setEnabled(true);
                loadCameraButton.setVisibility(View.GONE);
            }
        });
    }

}
