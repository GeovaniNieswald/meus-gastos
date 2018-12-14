package com.geovaninieswald.meusgastos.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QrCodeScannerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private Context context;

    private SurfaceHolder holderAux;

    private final int RC_CAMERA = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        toolbar = findViewById(R.id.toolbarID);
        surfaceView = findViewById(R.id.camerapreview);

        toolbar.setTitle("Leitor de QR Code");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = QrCodeScannerActivity.this;

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                holderAux = holder;

                if (ActivityCompat.checkSelfPermission(QrCodeScannerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QrCodeScannerActivity.this, new String[]{Manifest.permission.CAMERA}, RC_CAMERA);
                    return;
                }

                iniciarCamera(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();

                if (qrcodes.size() != 0) {
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("data", qrcodes.valueAt(0).displayValue);
                    ((Activity) context).setResult(RESULT_OK, resultIntent);
                    ((Activity) context).finish();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RC_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    iniciarCamera(holderAux);
                } else {
                    Utils.mostrarMensagemCurta(QrCodeScannerActivity.this, "Você precisa dar permissão para o aplicativo");
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void iniciarCamera(SurfaceHolder holder) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraSource = new CameraSource.Builder(QrCodeScannerActivity.this, barcodeDetector).setRequestedPreviewSize(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height()).setAutoFocusEnabled(true).build();
                cameraSource.start(holder);
            }
        } catch (IOException e) {
            // Tratar
        }
    }
}
