package example.zxing;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.net.MalformedURLException;
import java.net.URL;

public class KujialeBarcodeScanActivity extends Activity {
    private ScanManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        findViewById(R.id.zxing_barcode_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                finish();
            }
        });

        capture = new ScanManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
        capture.setInterceptor(new ScanManager.ResultInterceptor() {
            @Override
            public boolean onInterceptor(final BarcodeResult rawResult) {
                URL url;
                try {
                    url = new URL(rawResult.getText());
                } catch (MalformedURLException e) {
                    Log.e("pengtao", "xxx", e);
                    showInvalidScanDialog();
                    return true;
                }
                Log.e("pengtao", "result = " + url.getHost());
                return true;
            }
        });
    }

    private void showInvalidScanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                barcodeScannerView.resume();
                capture.decode();
            }
        });
        builder.setTitle("扫码无效");
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
