package com.example.juyoung.waiting2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juyoung.waiting2.activity.BranchActivity;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.Shop;

import java.util.ArrayList;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.ViewHolder> {
    final static int INTENT_REQUEST_BOOKMARK=5;
    Context context;
    ArrayList<Shop> items;
    ArrayList<Integer> list_id;
    public BookMarkAdapter(Context context, ArrayList<Shop> items,ArrayList<Integer> list_id) {
        this.context = context;
        this.items = items;
        this.list_id=list_id;
    }

    @NonNull
    @Override
    public BookMarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookmark_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookMarkAdapter.ViewHolder holder, int position) {
        //위에서 만들어준 뷰홀더를 onBindViewHolder 메소드를 통해서 시스템이 나에게 넘겨주면 이곳에서 값을 세팅한다.
        holder.brand_view.setImageURI(Uri.parse(items.get(position).getMainImage()));
        holder.brand_view.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.MULTIPLY);
        holder.name_view.setText(items.get(position).getName());
        holder.explanation_view.setText(items.get(position).getExplanation());
        holder.waiting_num_view.setText(((Integer) items.get(position).getWaiting_num()).toString());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<Shop> items) {
        this.items = items;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView brand_view;
        private TextView name_view;
        private TextView explanation_view;
        private TextView waiting_num_view;

        public ViewHolder(View itemView) {
            super(itemView);
            brand_view=(ImageView)itemView.findViewById(R.id.brandView);
            name_view = (TextView) itemView.findViewById(R.id.name_textView);
            explanation_view = (TextView) itemView.findViewById(R.id.explanation_textView);
            waiting_num_view = (TextView) itemView.findViewById(R.id.waitingNum_textView);
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,BranchActivity.class);
                   intent.putExtra("id",items.get(getAdapterPosition()).getId());
                   intent.putExtra("position",getAdapterPosition());
                    ((Activity)context).startActivityForResult(intent,INTENT_REQUEST_BOOKMARK);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    final CharSequence[] dialogitems = {"삭제","공유"};
                    AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(view.getContext());
                    //제목셋팅
                    alertDialogBuilder.setTitle("즐겨찾기");
                    //AlertDialog 셋팅
                    alertDialogBuilder.setItems(dialogitems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                items.remove(getAdapterPosition());
                                list_id.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                            }
                            else{

//                                FeedTemplate params = FeedTemplate
//                                        .newBuilder(ContentObject.newBuilder(items.get(getAdapterPosition()).getName(),
//                                                "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg",
//                                                LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
//                                                        .setMobileWebUrl("https://developers.kakao.com").build())
//                                                .setDescrption(items.get(getAdapterPosition()).getLocation())
//                                                .build())
//                                        .setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
//                                                .setSharedCount(30).setViewCount(40).build())
//                                        .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
//                                                .setWebUrl("'https://developers.kakao.com")
//                                                .setMobileWebUrl("'https://developers.kakao.com")
//                                                .setAndroidExecutionParams("key1=value1")
//                                                .setIosExecutionParams("key1=value1")
//                                                .build()))
//                                        .build();
//                                KakaoLinkService.getInstance().sendDefault(context, params, new ResponseCallback<KakaoLinkResponse>() {
//                                    @Override
//                                    public void onFailure(ErrorResult errorResult) {
//                                        Logger.e(errorResult.toString());
//                                    }
//
//                                    @Override
//                                    public void onSuccess(KakaoLinkResponse result) {
//                                        // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
//                                    }
//                                });

                            }
                        }
                    }).show();
                    return false;
                }
            });
        }

    }


}

