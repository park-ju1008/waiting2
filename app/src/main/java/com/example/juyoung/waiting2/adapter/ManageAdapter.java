package com.example.juyoung.waiting2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juyoung.waiting2.activity.LiveSeatActivityBoss;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.activity.SeatActivityBoss;
import com.example.juyoung.waiting2.Shop;
import com.example.juyoung.waiting2.activity.UpdateActivityBoss;

import java.util.ArrayList;

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.ViewHolder> {
    private final static int INTENT_REQUEST_GET_ITEM=1;
    Context mContext;
    ArrayList<Shop> mShopList;

    public ManageAdapter(Context context, ArrayList<Shop> shopList) {
        mContext = context;
        mShopList = shopList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.re_item_boss,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mMainImage.setImageURI(Uri.parse(mShopList.get(position).getMainImage()));
        holder.mName.setText(mShopList.get(position).getName());
        holder.mRegion.setText(mShopList.get(position).getRegion());
        holder.mExplanation.setText(mShopList.get(position).getExplanation());
    }

    @Override
    public int getItemCount() {
        return mShopList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mMainImage;
        TextView mName;
        TextView mRegion;
        TextView mExplanation;
        public ViewHolder(View itemView) {
            super(itemView);
            mMainImage=(ImageView)itemView.findViewById(R.id.mainImage_View);
            //이미지뷰 모서리 둥글게 처리
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //gradientDrawable은 주어진 배경을 가득 채운다.
                GradientDrawable drawable = (GradientDrawable)mContext.getDrawable(R.drawable.round);
                mMainImage.setBackground(drawable);
                mMainImage.setClipToOutline(true);
            }
            mName=(TextView)itemView.findViewById(R.id.name_View);
            mRegion=(TextView)itemView.findViewById(R.id.region_View);
            mExplanation=(TextView)itemView.findViewById(R.id.explanation_View);

//            itemView.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View view) {
//                    Intent intent=new Intent(mContext,UpdateActivityBoss.class);
//                    int id=mShopList.get(getAdapterPosition()).getId();
//                    intent.putExtra("id",id);
//                    intent.putExtra("position",getAdapterPosition());
//                    ((Activity)mContext).startActivityForResult(intent,INTENT_REQUEST_GET_ITEM);
//                }
//            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    final CharSequence[] dialogitems = {"매장정보 수정하기","좌석도 수정","실시간 좌석 현황판","매장 삭제"};
                    AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(view.getContext());
                    //제목셋팅
                    alertDialogBuilder.setTitle("매장 관리");
                    //AlertDialog 셋팅
                    alertDialogBuilder.setItems(dialogitems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                Intent intent=new Intent(mContext,UpdateActivityBoss.class);
                                int id=mShopList.get(getAdapterPosition()).getId();
                                intent.putExtra("id",id);
                                intent.putExtra("position",getAdapterPosition());
                                ((Activity)mContext).startActivityForResult(intent,INTENT_REQUEST_GET_ITEM);
                            }
                            else if(i==1){
                                Intent intent=new Intent(mContext,SeatActivityBoss.class);
                                int id=mShopList.get(getAdapterPosition()).getId();
                                String name=mShopList.get(getAdapterPosition()).getName();
                                intent.putExtra("id",id);
                                intent.putExtra("name",name);
                                ((Activity)mContext).startActivity(intent);
                            }
                            else if(i==2){
                                Intent intent=new Intent(mContext,LiveSeatActivityBoss.class);
                                int id=mShopList.get(getAdapterPosition()).getId();
                                String name=mShopList.get(getAdapterPosition()).getName();
                                intent.putExtra("id",id);
                                intent.putExtra("name",name);
                                ((Activity)mContext).startActivity(intent);
                            }
                        }
                    }).show();
                    return false;
                }
            });
        }
    }
}
