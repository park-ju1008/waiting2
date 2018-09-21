package com.example.juyoung.waiting2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juyoung.waiting2.activity.BranchActivity;
import com.example.juyoung.waiting2.MultiInfo;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final int VIEWPAGER_TYPE = 0;
    public static final int FILTER_TYPE = 1;
    public static final int SHOP_TYPE = 2;
    Context context;
    ViewPagerAdapter mPagerAdapter;
    ArrayList<MultiInfo> items;
    MyDataBase db;

    int currentPage=0;

    public SearchAdapter(Context context, ViewPagerAdapter pagerAdapter, ArrayList<MultiInfo> items) {
        this.context = context;
        this.mPagerAdapter = pagerAdapter;
        this.items = items;
        this.db=MyDataBase.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = 0;
        switch (viewType) {
            case VIEWPAGER_TYPE:
                layoutId = R.layout.re_viewpager_item;
                break;
            case FILTER_TYPE:
                layoutId = R.layout.filter_item;
                break;
            case SHOP_TYPE:
                layoutId = R.layout.recyclerview_item;
                break;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEWPAGER_TYPE:
                viewHolder = new ViewPagerViewHolder(view);
                break;
            case FILTER_TYPE:
                viewHolder = new FilterViewHolder(view);
                break;
            case SHOP_TYPE:
                viewHolder = new ShopViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.mainImage_view.setImageURI(items.get(position).getMainImage());
        holder.bind(position);
    }

    @Override
    public int getItemViewType(int position) {

        return items.get(position).type;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    //뷰페이저를 위한 뷰홀더
    public class ViewPagerViewHolder extends ViewHolder {
        ViewPager viewPager;
        LinearLayout mMark;

        public ViewPagerViewHolder(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.viewPager);
            mMark = (LinearLayout) itemView.findViewById(R.id.position_View);
            //현재 페이지를 표시하기 위한 마커 생성
            final int count = mPagerAdapter.getCount();

            for (int i = 0; i < count; i++) {
                ImageView mark = new ImageView(context);
                mark.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mark.setImageResource(R.drawable.not_mark);
                mark.setPadding(0, 0, 10, 0);
                mMark.addView(mark);
            }
            ((ImageView) mMark.getChildAt(0)).setImageResource(R.drawable.mark);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                int mprevPosition = 0;
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    ((ImageView) mMark.getChildAt(mprevPosition)).setImageResource(R.drawable.not_mark);
                    ((ImageView) mMark.getChildAt(position)).setImageResource(R.drawable.mark);
                    mprevPosition = position;
                    currentPage=position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //핸들러 선언
            final Handler handler=new Handler();
            //타이머를 이용하여 일정시간마다 호출해준다 핸들러로 Runnable객체를 넘겨준다.
            Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable(){
                        @Override
                        public void run() {
                            if(currentPage==count){
                                currentPage=0;
                            }
                            viewPager.setCurrentItem(currentPage++,true);
                        }
                    });
                }
            },100,2000);
        }

        @Override
        void bind(int position) {
            if (viewPager.getAdapter() == null)
                viewPager.setAdapter(mPagerAdapter);
        }
    }

    // 필터 레이아웃을 위한 뷰홀더
    public class FilterViewHolder extends ViewHolder {
        TextView sort_view;
        TextView filter_view;

        public FilterViewHolder(View itemView) {
            super(itemView);
            sort_view = (TextView) itemView.findViewById(R.id.sort_View);
            filter_view = (TextView) itemView.findViewById(R.id.filter_View);
            sort_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "뭐지", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        void bind(int position) {

        }
    }


    public class ShopViewHolder extends ViewHolder {
        ImageView mainImage_view;
        TextView name_view;
        TextView region_view;
        TextView explanation_view;
        TextView count_view;
        TextView distance_view;
        TextView waiting_num_view;
        ProgressBar progressBar;

        public ShopViewHolder(View itemView) {
            super(itemView);
            mainImage_view = (ImageView) itemView.findViewById(R.id.mainImage_View);
            name_view = (TextView) itemView.findViewById(R.id.name_View);
            region_view = (TextView) itemView.findViewById(R.id.region_View);
            explanation_view = (TextView) itemView.findViewById(R.id.explanation_View);
            count_view = (TextView) itemView.findViewById(R.id.count_View);
            distance_view = (TextView) itemView.findViewById(R.id.distance_View);
            waiting_num_view = (TextView) itemView.findViewById(R.id.waiting_View);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.lookCountIncrement(items.get(getAdapterPosition()).data.getId());
                    Intent intent = new Intent(context, BranchActivity.class);
                    intent.putExtra("id", items.get(getAdapterPosition()).data.getId());
                    ((Activity) context).startActivity(intent);
                }
            });
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        void bind(final int position) {
            progressBar.setVisibility(View.VISIBLE);
            mainImage_view.setVisibility(View.GONE);
            new AsyncTask<ShopViewHolder, Void, Bitmap>() {
                private ShopViewHolder v;

                @Override
                protected Bitmap doInBackground(ShopViewHolder... params) {
                    v = params[0];
                    return resize(Uri.parse(items.get(position).data.getMainImage()));
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    if (v.getAdapterPosition() == position) {
                        // If this item hasn't been recycled already, hide the
                        // progress and set and show the image
                        v.progressBar.setVisibility(View.GONE);
                        v.mainImage_view.setVisibility(View.VISIBLE);
                        v.mainImage_view.setImageBitmap(result);

                    }
                }
            }.execute(this);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //gradientDrawable은 주어진 배경을 가득 채운다.
                GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.round);
                mainImage_view.setBackground(drawable);
                mainImage_view.setClipToOutline(true);
            }
            name_view.setText(items.get(position).data.getName());
            region_view.setText(items.get(position).data.getRegion());
            explanation_view.setText(items.get(position).data.getExplanation());
            int count=db.getLookCount(items.get(position).data.getId());
            count_view.setText(""+count);
//        holder.distance_view
            waiting_num_view.setText(" " + items.get(position).data.getWaiting_num());
        }
    }


    private Bitmap resize(Uri uri) {
        Bitmap resizeBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        //로컬에 존재하는 파일을 그대로 읽어올때 쓰며 파일경로를 파라미터로 넘겨주면 FileInputStream을 만들어 decodeStream을 한다.
        Bitmap src = BitmapFactory.decodeFile(uri.toString(), options);
        //크기를 재조정 할수 있다.
        Bitmap resized = Bitmap.createScaledBitmap(src, 150, 130, true);

        return resized;
    }

}

