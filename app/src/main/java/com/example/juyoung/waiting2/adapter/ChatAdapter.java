package com.example.juyoung.waiting2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.juyoung.waiting2.Chat;
import com.example.juyoung.waiting2.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    ArrayList<Chat> mChats;
    Context mContext;

    public ChatAdapter(ArrayList<Chat> chats, Context context) {
        mChats = chats;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.rc_chat_itme,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nick.setText(mChats.get(position).getName());
        Log.v("aaa",stringToHex(mChats.get(position).getName()));
        holder.nick.setTextColor(Color.parseColor(stringToHex(mChats.get(position).getName())));
        holder.content.setText(mChats.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nick,content;
        public ViewHolder(View itemView) {
            super(itemView);
            nick=(TextView)itemView.findViewById(R.id.nickname_view);
            content=(TextView)itemView.findViewById(R.id.content_view);
        }
    }

    // 문자열을 헥사 스트링으로 변환하는 메서드
    public static String stringToHex(String s) {
        int len=3;
        String result = "#";

        if(s.length()<3){
            len=s.length();
        }
        for (int i = 0; i < len; i++) {
            result += String.format("%02X", ((int) s.charAt(i))%255);
        }

        for(int i=result.length();i<7;i++){
            result+="0";
        }
        return result;
    }
}
