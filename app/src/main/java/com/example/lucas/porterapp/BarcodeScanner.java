package com.example.lucas.porterapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarcodeScanner extends AppCompatActivity {
    SurfaceView scanner;

    public static final int CAMERA_PERMISSIONS_REQUEST = 0;

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        getSupportActionBar().setTitle("ID Scanner");

        // assign the camera barcode scanner to scanner
        scanner = (SurfaceView) findViewById(R.id.surface_view_barcode_scanner);
        setupCamera();
    }

    // ---------------------------------------------------------------------------------------------

     /*
     * set up the camera source with the barcode detector.
     * enable autofocus and set the preview size field
     * detect and read barcode, send the result back to the calling activity
     */
    private void setupCamera() {

        // set up barcode detector, and set up the camera using the barcode detector
        BarcodeDetector barcodeReader = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeReader).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();


        // add callback to SurfaceView scanner to control starting and stopping of the camera
        scanner.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                try {
                    // check that camera permission is granted
                    if (ActivityCompat.checkSelfPermission(BarcodeScanner.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //request permission if not already granted
                        ActivityCompat.requestPermissions(BarcodeScanner.this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST);
                    }
                        // start the camera in the SurfaceView scanner
                        cameraSource.start(scanner.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            // stop the camera when the SurfaceView scanner is destroyed
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        // set the barcodeReader to get the detected barcodes from camera
        barcodeReader.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                // add the detected barcodes to the detectedBarcode array
                SparseArray<Barcode> detectedBarcode = detections.getDetectedItems();
                // check that barcodes have been detected
                if(detectedBarcode.size() > 0){
                    // pass the latest barcode from the array to the intent
                    Intent intent = new Intent();
                    intent.putExtra("barcode", detectedBarcode.valueAt(0));
                    // pass the success message to the intent
                    setResult(CommonStatusCodes.SUCCESS, intent);


                    // finish this activity.
                    finish();
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

}
