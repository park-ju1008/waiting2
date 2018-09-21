package com.example.juyoung.waiting2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.juyoung.waiting2.R;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    Context context;
    ArrayList<Uri> image_uris;

    public ImageAdapter(Context context, ArrayList<Uri> image_uris) {
        this.context = context;
        this.image_uris = image_uris;
    }

    @NonNull
    @Override
    //뷰홀더를 생성하고 뷰를 붙여주는 부분
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_image_item,parent,false);
        return new ViewHolder(view);
    }

    //재활용 되는 뷰가 호출하여 실행되는 메소드 뷰 홀더를 전달하고 어댑터는 position의 데이터를 결합
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.img.setImageURI(image_uris.get(position));
    }

    @NonNull


    @Override
    public int getItemCount() {
        return image_uris.size();
    }

    public void setImage_uris(ArrayList<Uri> image_uris) {
        this.image_uris = image_uris;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img=(ImageView) itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Config config=new Config();
                    config.setSelectionLimit(5);
                    ImagePickerActivity.setConfig(config);
                    Intent intent  = new Intent(context, ImagePickerActivity.class);
                    ((Activity)context).startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);
                }
            });
        }
    }



}
