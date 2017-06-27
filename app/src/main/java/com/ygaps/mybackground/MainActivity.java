package com.ygaps.mybackground;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog processDialog;
    MyBackgroundService service;
    String deviceID;
    int pre = 0;
    String name;
    boolean isLoadingFinish = false;

    private TextView tvDay;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        processDialog = new ProgressDialog(this);
        processDialog.setCancelable(false);

        tvDay=(TextView)this.findViewById(R.id.tvDate);
        imageView = (ImageView) this.findViewById(R.id.imageView);

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl("http://yb.ygaps.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = restAdapter.create(MyBackgroundService.class);

        deviceID = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        getImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_background) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getCurDateString(int pre) {
        Calendar c=Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, pre);
        int day=c.get(Calendar.DAY_OF_MONTH);
        int month=c.get(Calendar.MONTH);
        int year=c.get(Calendar.YEAR);
        return year+"-"+month+"-"+day;
    }

    private void getImage(){
        final String curDate = getCurDateString(pre);
        processDialog.show();

        int isDate;
        if(pre == 0)
            isDate = 1;
        else
            isDate = 0;

        Call<MBGResponse> call = service.getX(deviceID, curDate, isDate);
        call.enqueue(new Callback<MBGResponse>() {
            @Override
            public void onResponse(Call<MBGResponse> call, Response<MBGResponse> response) {
                if(response.isSuccessful()){
                    MBGResponse s = response.body();
                    String url = null;
                    if(s.isEmpty() == true){
                        Toast.makeText(MainActivity.this, getString(R.string.text_no_image), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            url = s.getUrl();
                            String[] t = url.split("/");
                            if (t.length > 0) {
                                name = "1photo1day-" + t[t.length - 1];
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                                String camImgFilename = sdf.format(new Date()) + ".jpg";
                                name = "1photo1day-" + camImgFilename;
                            }
                            Picasso.with(MainActivity.this).load(url).into(imageView);
                            isLoadingFinish = true;
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    tvDay.setText(curDate);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("abc", e.toString());
                        }
                    }
                }else{
                    //Hiển thị lỗi
                    Toast.makeText(MainActivity.this, getString(R.string.text_no_image), Toast.LENGTH_SHORT).show();
                }
                processDialog.cancel();
            }

            @Override
            public void onFailure(Call<MBGResponse> call, Throwable t) {
                String s;
                s = "";
                processDialog.cancel();
            }
        });
    }

    public void onSaveAs(View v) {
        if (isLoadingFinish == true) {
            imageView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/onephotooneday");
            dir.mkdirs();

            File file = new File("/sdcard/onephotooneday/" + name);
            try {
                if (file.exists())
                    file.delete();
                file.createNewFile();
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                galleryAddPic(file);
                ostream.close();
                Toast.makeText(MainActivity.this, getString(R.string.text_success_saveas), Toast.LENGTH_SHORT).show();

//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.text_no_image), Toast.LENGTH_SHORT).show();
        }
    }

    private void galleryAddPic(File photo) {
        if (photo != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(photo);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
    }
}
