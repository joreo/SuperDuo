package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import it.jaschke.alexandria.services.BookService;
import me.dm7.barcodescanner.zxing.ZXingScannerView;



public class BarcodeScanner extends Activity implements ZXingScannerView.ResultHandler {

    public static final String EXTRA_BAR_CODE = "barCode";
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(this);
        setContentView(scannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        String ean = result.getText();
        Log.e("contents pre", ean);
        if (ean.length() == 10 && !ean.startsWith("978")) {
            ean = "978" + ean;
        }

        Intent bookIntent = new Intent(this, BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        Log.e("text post:", ean);
        bookIntent.setAction(BookService.FETCH_BOOK);
        this.startService(bookIntent);

        finish();
    }
}