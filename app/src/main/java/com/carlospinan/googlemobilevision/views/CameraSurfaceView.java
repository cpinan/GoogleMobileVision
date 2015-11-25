package com.carlospinan.googlemobilevision.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.carlospinan.googlemobilevision.utils.Constants;
import com.carlospinan.googlemobilevision.utils.Utils;

import java.io.IOException;

/**
 * @author Carlos Piñan
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraView";

    public interface CameraSurfaceListener {
        void onPictureTaken(Bitmap bitmap);
    }

    private Camera camera;
    private boolean isInPreview;
    private Camera.PictureCallback rawCallback;
    private Camera.ShutterCallback shutterCallback;
    private Camera.PictureCallback jpegCallback;
    private CameraSurfaceListener listener;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            rawCallback = new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.d(TAG, "rawCallback");
                }
            };

            shutterCallback = new Camera.ShutterCallback() {
                public void onShutter() {
                    Log.d(TAG, "shutterCallback");
                }
            };
            jpegCallback = new Camera.PictureCallback() {
                public void onPictureTaken(byte[] bitmapData, Camera camera) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, options);
                    options.inSampleSize = Utils.calculateInSampleSize(options, Constants.PHOTO_MAX_SIZE, Constants.PHOTO_MAX_SIZE);
                    options.inJustDecodeBounds = false;
                    Matrix matrix = new Matrix();
                    matrix.postScale(-1, 1);
                    matrix.postRotate(Constants.ROTATION);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, options);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    if (listener != null) {
                        listener.onPictureTaken(bitmap);
                    }
                    Log.d(TAG, "jpegCallback");
                }
            };
            getHolder().addCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (isInPreview) {
            stopCamera();
        }
    }

    public void startCamera() {
        try {
            isInPreview = true;
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            camera.setDisplayOrientation(Constants.ROTATION);
            camera.setPreviewDisplay(getHolder());
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopCamera() {
        try {
            isInPreview = false;
            camera.stopPreview();
            camera.release();
            camera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void captureImage() {
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    public void setListener(CameraSurfaceListener listener) {
        this.listener = listener;
    }

}
