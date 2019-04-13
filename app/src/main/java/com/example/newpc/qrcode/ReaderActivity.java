package com.example.newpc.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ReaderActivity extends AppCompatActivity {
    private Button scan_btn;
    private TextView tvNama , tvTipe , tvEmail  , tvMessage;
    private ImageView imgLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        tvNama = (TextView) findViewById(R.id.tvNama);
        tvTipe = (TextView) findViewById(R.id.tvTipe);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        final Activity activity = this;
        AndroidNetworking.initialize(getApplicationContext());
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                imgLogo.setImageResource(R.drawable.logo);
                tvTipe.setText("");
                tvEmail.setText("");
                tvNama.setText("");
                tvMessage.setText("");
                //Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                //tvTipe.setText(result.getContents());
                postStatus(result.getContents());

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void postStatus(String kode){
        Rx2AndroidNetworking.post("http://arinur203.000webhostapp.com/update.php")
                .addBodyParameter("kode", kode)
                .build()
                .getJSONObjectObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.d("ReaderActivity" , e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JSONObject response) {
                        try {
                            int success = response.getInt("success");


                            if (success == 0){
                                String message = response.getString("message");
                                imgLogo.setImageResource(R.drawable.xicon);
                                tvTipe.setText("");
                                tvEmail.setText("");
                                tvNama.setText("");
                                tvMessage.setText(message);
                                tvMessage.setTextColor(Color.parseColor("#FFFF0D00"));
                            }else if (success == 1){
                                String message = response.getString("message");
                                String nama = response.getString("nama");
                                String tipe = response.getString("id_kegiatan");
                                String email = response.getString("email");
                                imgLogo.setImageResource(R.drawable.checklist);
                                tvTipe.setText("Tiket"+ getString(R.string.tab) + "     : " +tipe);
                                tvEmail.setText("Email" + getString(R.string.tab) + "     : " +email);
                                tvNama.setText("Nama" + getString(R.string.tab) + "   : " +nama);
                                tvMessage.setText(message);
                                tvMessage.setTextColor(Color.parseColor("#FF3F51B5"));
                            }else if (success == 2){
                                String message = response.getString("message");
                                String tipe = response.getString("id_kegiatan");
                                String nama = response.getString("nama");
                                String email = response.getString("email");
                                imgLogo.setImageResource(R.drawable.xicon);
                                tvTipe.setText("Tiket"+ getString(R.string.tab) + "     : " +tipe);
                                tvEmail.setText("Email" + getString(R.string.tab) + "     : " +email);
                                tvNama.setText("Nama" + getString(R.string.tab) + "   : " +nama);
                                tvMessage.setText(message);
                                tvMessage.setTextColor(Color.parseColor("#FFFF0D00"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("ReaderActivity" , response.toString());
                    }
                });
       /* AndroidNetworking.post("http://arinur203.000webhostapp.com/update.php")
                .addBodyParameter("kode", kode)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");


                            if (success == 0){
                                String message = response.getString("message");
                                imgLogo.setImageResource(R.drawable.xicon);
                                tvTipe.setText("");
                                tvEmail.setText("");
                                tvNama.setText("");
                                tvMessage.setText(message);
                                tvMessage.setTextColor(Color.parseColor("#FFFF0D00"));
                            }else if (success == 1){
                                String message = response.getString("message");
                                String nama = response.getString("nama");
                                String tipe = response.getString("tipe");
                                String email = response.getString("email");
                                imgLogo.setImageResource(R.drawable.checklist);
                                tvTipe.setText("Tiket"+ getString(R.string.tab) + "     : " +tipe);
                                tvEmail.setText("Email" + getString(R.string.tab) + "     : " +email);
                                tvNama.setText("Nama" + getString(R.string.tab) + "   : " +nama);
                                tvMessage.setText(message);
                                tvMessage.setTextColor(Color.parseColor("#FF3F51B5"));
                            }else if (success == 2){
                                String message = response.getString("message");
                                String tipe = response.getString("tipe");
                                String nama = response.getString("nama");
                                String email = response.getString("email");
                                imgLogo.setImageResource(R.drawable.xicon);
                                tvTipe.setText("Tiket"+ getString(R.string.tab) + "     : " +tipe);
                                tvEmail.setText("Email" + getString(R.string.tab) + "     : " +email);
                                tvNama.setText("Nama" + getString(R.string.tab) + "   : " +nama);
                                tvMessage.setText(message);
                                tvMessage.setTextColor(Color.parseColor("#FFFF0D00"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("ReaderActivity" , response.toString());
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d("ReaderActivity" , error.toString());
                    }
                });*/
    }
}
