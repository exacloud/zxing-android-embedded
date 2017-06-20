package example.zxing;

import android.app.Activity;

import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * @author dq
 */
public class ScanManager extends CaptureManager {
    private ResultInterceptor mResultInterceptor;

    public interface ResultInterceptor{
        boolean onInterceptor(BarcodeResult rawResult);
    }


    public ScanManager(final Activity activity, final DecoratedBarcodeView barcodeView) {
        super(activity, barcodeView);
    }

    public void setInterceptor(ResultInterceptor interceptor) {
        mResultInterceptor = interceptor;
    }

    @Override
    protected void returnResult(BarcodeResult rawResult) {
        if (mResultInterceptor != null && mResultInterceptor.onInterceptor(rawResult)) {
            return;
        }
        super.returnResult(rawResult);
    }
}
