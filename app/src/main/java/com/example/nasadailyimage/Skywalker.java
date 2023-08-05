package com.example.nasadailyimage;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Matrix;
/**
 * @author Jinsheng Gu
 */
public class Skywalker extends Welcome {
    String chosenDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleCommonComponents();

        TextView tvChooseDate = findViewById(R.id.tvchoose);
        TextView tvDate = findViewById(R.id.tvDate);
        //add event listner to textview to choose date
        tvChooseDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            chosenDate = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                            tvDate.setText("Date chosen:\n" + chosenDate);

                            // hide visible components generated from previous searches
                            TextView tvUrl = findViewById(R.id.tvUrl);
                            tvUrl.setText("");
                            Button btnLink = findViewById(R.id.btnLink);
                            btnLink.setVisibility(View.INVISIBLE);
                            Button btnSave = findViewById(R.id.btnSave);
                            btnSave.setVisibility(View.INVISIBLE);

                            // check the chosen date

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date cDate = sdf.parse(chosenDate);
                                if(cDate.after(new Date())){
                                    //toast
                                    Toast.makeText(Skywalker.this,"You can not choose a date after today!",Toast.LENGTH_LONG).show();
                                }else {
                                    // build url dynamically
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("https://api.nasa.gov/planetary/apod?api_key=");
                                    sb.append("Q4KdW6HDALQTwRQvMULi43VLAi3fxSexhyJRViAY&date=");
                                    sb.append(chosenDate);
                                    // execute async request to NASA api
                                    new NasaInfo().execute(sb.toString());
                                }

                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    /**
     * class to execute async task
     */
    public class NasaInfo extends AsyncTask<String, Integer, String> {
        byte[] image = null;
        String hdurl = "";
        String title = "";

        @Override
        protected String doInBackground(String... url) {
            String requestUrl = url[0];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                try {
                    publishProgress(i);
                    Thread.sleep(30);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                URL hUrl = new URL(requestUrl);
                final URLConnection urlConnection = hUrl.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(sb.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                hdurl = jsonObject.getString("url");
                title = jsonObject.getString("title");
                image = handleImage(hdurl, title);


            } catch (JSONException | MalformedURLException e) {
                throw new RuntimeException(e);
            }
            return hdurl;
        }

        @Override
        protected void onProgressUpdate(Integer... integers) {
            //activate progressbar
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setProgress(integers[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            TextView tvUrl = findViewById(R.id.tvUrl);
            tvUrl.setText("Universe Found:\n" + hdurl);

            Button btnLink = findViewById(R.id.btnLink);
            btnLink.setText("Enjoy your universe");
            btnLink.setVisibility(View.VISIBLE);

            Button btnSave = findViewById(R.id.btnSave);
            btnSave.setText("save your universe into archive");
            btnSave.setVisibility(View.VISIBLE);

            btnLink.setOnClickListener((click) -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(hdurl));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            });

            MyDbHelper myDbHelper = MyDbHelper.getInstance(Skywalker.this);

            btnSave.setOnClickListener((click) -> {
                Archive archive = new Archive(title, chosenDate, hdurl, image);
                myDbHelper.addArchive(archive);
                Intent intent = new Intent(Skywalker.this, MyArchive.class);
                startActivity(intent);
            });
        }

        /**
         *
         * @param url
         * @param title
         * @return
         * @throws JSONException
         * @throws MalformedURLException
         */
        private byte[] handleImage(String url, String title) throws JSONException, MalformedURLException {
            //get url of a cat image
            Bitmap bitmap;
            InputStream inputStream;
            try {
                //get image from target url
                inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2,bitmap.getHeight()/2, false);
                //save image to local storage
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                new_bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
                return outputStream.toByteArray();
                // Log.i("Cat image: ", id +".jpeg was downloaded");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}