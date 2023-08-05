package com.example.nasadailyimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsFragment extends Fragment {

    private Bundle dataFromActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve data
        dataFromActivity = getArguments();
        Archive archive = new Archive();
        if(getArguments() != null) {
            archive = (Archive) dataFromActivity.getSerializable("archive");
        }
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        //update UI
        TextView tView = view.findViewById(R.id.tvTitle);
        tView.setText(archive.getName());
        TextView tView1 = view.findViewById(R.id.tvDate);
        tView1.setText(archive.getDate());
        ImageView iv = view.findViewById(R.id.ivPhoto);
        Bitmap bmp = BitmapFactory.decodeByteArray(archive.getPhoto(),0,archive.getPhoto().length);
        iv.setImageBitmap(bmp);
        TextView tView2 = view.findViewById(R.id.tvHdurl);
        tView2.setText(archive.getHdurl());
        return view;
    }
}