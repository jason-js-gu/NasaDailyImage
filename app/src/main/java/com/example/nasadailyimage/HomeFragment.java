package com.example.nasadailyimage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;


public class HomeFragment extends Fragment {
    String chosenDate = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable
                             Bundle savedInstanceState) {

        TextView tvChooseDate = getView().findViewById(R.id.tvchoose);
        TextView tvDate = getView().findViewById(R.id.tvDate);
        //add event listner to textview to choose date


        tvChooseDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this.getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            chosenDate = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                            tvDate.setText("Date chosen:\n" + chosenDate);
                            StringBuilder sb = new StringBuilder();
                            sb.append("https://api.nasa.gov/planetary/apod?api_key=");
                            sb.append("Q4KdW6HDALQTwRQvMULi43VLAi3fxSexhyJRViAY&date=");
                            sb.append(chosenDate);
                            new NasaInfo().execute(sb.toString());
                        }
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    public class NasaInfo extends AsyncTask<String, Integer, String> {
        byte[] image = null;
        String hdurl = "";
        String title = "";

        @Override
        protected String doInBackground(String... url) {
            String requestUrl = url[0];
            StringBuilder sb = new StringBuilder();
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

        }

        @Override
        protected void onPostExecute(String result) {
            TextView tvUrl = getActivity().findViewById(R.id.tvUrl);
            TextView tvLink = getActivity().findViewById(R.id.tvLink);
            TextView tvSave = getActivity().findViewById(R.id.tvSave);
            tvUrl.setText(hdurl);
            tvLink.setText("Click to view the photo");
            tvSave.setText("Click to save the photo into archive");

            MyDbHelper myDbHelper = MyDbHelper.getInstance(getContext());

            tvLink.setOnClickListener((click) -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(hdurl));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            });

            tvSave.setOnClickListener((click) -> {
                Archive archive = new Archive(title, chosenDate, hdurl, image);
                myDbHelper.addArchive(archive);

                ArchiveFragment archiveFragment = new ArchiveFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, archiveFragment).commit();

//                Fragment fragment = new HomeFragment();
//                FragmentManager fm = getChildFragmentManager();
//                FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                fragmentTransaction.replace(R.id.archiveLayout, fragment);
//                fragmentTransaction.commit();
            });


        }


        private byte[] handleImage(String url, String title) throws JSONException, MalformedURLException {
            //get url of a cat image
            Bitmap bitmap;
            InputStream inputStream;
            try {
                //get image from target url
                inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                //save image to local storage
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
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


    //Q4KdW6HDALQTwRQvMULi43VLAi3fxSexhyJRViAY

