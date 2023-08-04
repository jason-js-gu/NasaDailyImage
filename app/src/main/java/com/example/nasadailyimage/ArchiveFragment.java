package com.example.nasadailyimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ArchiveFragment extends Fragment {
    List<Archive> photos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_archive, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable
    Bundle savedInstanceState) {
        ListView listView = view.findViewById(R.id.archiveLayout);
        //retrieve photos from database
        MyDbHelper myDbHelper = MyDbHelper.getInstance(getContext());
        photos = myDbHelper.getAllPhotos();
        //System.out.println("num-photos:"+photos.size());
        //set list adapter
        ListAdapter listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        //set click listner to open the photo in the browser
        listView.setOnItemClickListener((p,b,position,id)->{
            Archive archive = (Archive) listAdapter.getItem(position);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(archive.getHdurl()));
            startActivity(intent);
        });

        //set long click listner to delete the photo
        listView.setOnItemLongClickListener((p,b,position,id)->{
            Archive archive = (Archive) listAdapter.getItem(position);
            photos.remove(archive);
            myDbHelper.deleteArchive(archive.getDate());
            listAdapter.notifyDataSetChanged();
            return true;
        });
    }

    private class ListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object getItem(int i) {
            return photos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Archive archive = (Archive) getItem(i);
            LayoutInflater inflater = getLayoutInflater();
            if(view == null){
                view = inflater.inflate(R.layout.activity_item,viewGroup, false);
            }
            ImageView imageView = view.findViewById(R.id.ivPhoto);
            Bitmap bmp = BitmapFactory.decodeByteArray(archive.getPhoto(),0,archive.getPhoto().length);
            imageView.setImageBitmap(bmp);
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            TextView tvDate = view.findViewById(R.id.tvDate);
            tvTitle.setText("Title:\n" + archive.getName());
            tvDate.setText("Date:\n" + archive.getDate());
            return view;
        }
    }

}