package com.example.juyoung.waiting2.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juyoung.waiting2.BottomFilterDialog;
import com.example.juyoung.waiting2.BottomFilterDialogListener;
import com.example.juyoung.waiting2.BottomSortDialog;
import com.example.juyoung.waiting2.BottomSortDialogListener;
import com.example.juyoung.waiting2.Shop;
import com.example.juyoung.waiting2.activity.BranchActivity;
import com.example.juyoung.waiting2.MultiInfo;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.activity.SearchActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class SearchAdapter extends RecyclerView.Adapter<ViewHolder> {
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final int VIEWPAGER_TYPE = 0;
    public static final int FILTER_TYPE = 1;
    public static final int SHOP_TYPE = 2;
    public static final int CATE_ALL = 0;
    public static final int CATE_RESTAURANT = 1;
    public static final int CATE_FOODTRUCK = 2;
    public static final int CATE_DESSERT = 3;
    private final int PERMISSON_LOCATION = 14;
    public String[] filter_list = {"음식점", "푸드트럭", "카페", "베이커리/디저트", "한식", "일식", "중식", "양식", "페스트푸드", "치킨", "분식", "술집"};
    int sort_curIndex = 0, filter_curIndex = 0, sel_filter_food;
    FilterViewHolder mFilterViewHolder;
    Context context;
    ViewPagerAdapter mPagerAdapter;
    ArrayList<MultiInfo> items;
    MyDataBase db;

    int currentPage = 0;

    public SearchAdapter(Context context, ViewPagerAdapter pagerAdapter, ArrayList<MultiInfo> items) {
        this.context = context;
        this.mPagerAdapter = pagerAdapter;
        this.items = items;
        sortCheck(this.items);
        this.db = MyDataBase.getInstance(context);
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
                mFilterViewHolder = new FilterViewHolder(view);
                viewHolder = mFilterViewHolder;
                break;
            case SHOP_TYPE:
                viewHolder = new ShopViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //핸들러 선언
            final Handler handler = new Handler();
            //타이머를 이용하여 일정시간마다 호출해준다 핸들러로 Runnable객체를 넘겨준다.
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (currentPage == count) {
                                currentPage = 0;
                            }
                            viewPager.setCurrentItem(currentPage++, true);
                        }
                    });
                }
            }, 100, 2000);
        }

        @Override
        void bind(int position) {
            if (viewPager.getAdapter() == null)
                viewPager.setAdapter(mPagerAdapter);
        }
    }

    // 필터 레이아웃을 위한 뷰홀더
    public class FilterViewHolder extends ViewHolder {
        public TextView sort_view;
        TextView filter_view;

        public FilterViewHolder(final View itemView) {
            super(itemView);
            sort_view = (TextView) itemView.findViewById(R.id.sort_View);
            filter_view = (TextView) itemView.findViewById(R.id.filter_View);
            sort_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final BottomSortDialog bottomSortDialog = BottomSortDialog.getInstance();
                    bottomSortDialog.show(((SearchActivity) context).getSupportFragmentManager(), "" + sort_curIndex);
                    bottomSortDialog.setBottomSortDialogListener(new BottomSortDialogListener() {
                        @Override
                        public void OnItemClicked(int index) {
                            //선택한 정렬 기준의 인덱스가 넘어온다.
                            switch (index) {
                                //조회순
                                case 0:
                                    bottomSortDialog.dismiss();
                                    sort_curIndex = 0;
                                    sort_view.setText(R.string.check_sort);
                                    sortCheck(items);
                                    notifyDataSetChanged();
                                    break;
                                case 1:
                                    bottomSortDialog.dismiss();
                                    sort_curIndex = 1;
                                    sort_view.setText(R.string.waiting_sort);
                                    sortWaiting(items);
                                    notifyDataSetChanged();
                                    break;
                                case 2:
                                    bottomSortDialog.dismiss();
                                    sort_curIndex = 2;
                                    sort_view.setText(R.string.reply_sort);
                                    sortReply(items);
                                    notifyDataSetChanged();
                                    break;
                                case 3:
                                    //위치 권한이 설정 되어있는지 확인
                                    if (checkLocationPermission()) {
                                        bottomSortDialog.dismiss();

                                        //시스템으로부터 위치서비스를 가져옴
                                        //GPS기능을 사용할 수 있는지 확인
                                        LocationRequest locationRequest = new LocationRequest();
                                        locationRequest.setNumUpdates(1);
                                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                                .addLocationRequest(locationRequest);
                                        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient((Activity) context).checkLocationSettings(builder.build());
                                        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                                            @Override
                                            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                                                try {
                                                    LocationSettingsResponse response = task.getResult(ApiException.class);
                                                    //GPS가 켜져 있다면 여기로 온다.
                                                    sort_curIndex = 3;
                                                    sort_view.setText(R.string.distance_sort);
                                                    ((SearchActivity) context).getCurrentLocation();
                                                    SharedPreferences pref = context.getSharedPreferences("location", Context.MODE_PRIVATE);
                                                    double latitude = Double.longBitsToDouble(pref.getLong("latitude", 0));
                                                    double longitude = Double.longBitsToDouble(pref.getLong("longitude", 0));
                                                    ((SearchActivity) context).setDistance(items, latitude, longitude);
                                                    sortDistance(items);
                                                    notifyDataSetChanged();
                                                } catch (ApiException e) {
                                                    //GPS 가 켜져 있지 않다면 여기로 온다.
                                                    switch (e.getStatusCode()) {
                                                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                                                            ResolvableApiException resolvable = (ResolvableApiException) e;
                                                            try {
                                                                resolvable.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTINGS);
                                                            } catch (IntentSender.SendIntentException e1) {
                                                                e1.printStackTrace();
                                                            }
                                                    }
                                                }

                                            }
                                        });
                                        Log.v("find", "add 바깥쪽");


                                    }
                                    break;
                            }
                        }
                    });
                }
            });

            filter_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final BottomFilterDialog bottomFilterDialog = BottomFilterDialog.getInstance();
                    Bundle args = new Bundle();
                    args.putInt("curIndex", filter_curIndex);
                    args.putInt("sel_index", sel_filter_food);
                    bottomFilterDialog.setArguments(args);
                    bottomFilterDialog.show(((SearchActivity) context).getSupportFragmentManager(), "" + filter_curIndex);
                    bottomFilterDialog.setBottomFilterDialogListener(new BottomFilterDialogListener() {
                        @Override
                        public void selectFilterItem(int category, int sel_filter) {
                            filter_curIndex = category;
                            sel_filter_food = sel_filter;
                            //이전의 리스트 지움
                            for (int i = items.size() - 1; i > 1; i--) {
                                items.remove(i);
                            }
                            ArrayList<Shop> shops;
                            if (category == CATE_ALL) {
                                shops=db.getRegoinShopList("강서구");
                            } else {
                                shops=db.getFilteredItem("강서구",getFilterList(category, sel_filter));
                            }
                            for (Shop a : shops) {
                                items.add(new MultiInfo(2, a, db.getLookCount(a.getId()), db.getReplyCount(a.getId())));
                            }
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }

        @Override
        void bind(int position) {

        }
    }


    public class ShopViewHolder extends ViewHolder {
        ConstraintLayout shop_item;
        ImageView mainImage_view;
        TextView name_view;
        TextView region_view;
        TextView explanation_view;
        TextView count_view;
        TextView reply_count_view;
        TextView distance_view;
        TextView waiting_num_view;
        ProgressBar progressBar;

        public ShopViewHolder(View itemView) {
            super(itemView);
            shop_item = (ConstraintLayout) itemView.findViewById(R.id.shop_item);
            mainImage_view = (ImageView) itemView.findViewById(R.id.mainImage_View);
            name_view = (TextView) itemView.findViewById(R.id.name_View);
            region_view = (TextView) itemView.findViewById(R.id.region_View);
            explanation_view = (TextView) itemView.findViewById(R.id.explanation_View);
            count_view = (TextView) itemView.findViewById(R.id.count_View);
            reply_count_view = (TextView) itemView.findViewById(R.id.reply_View);
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
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.re_item_appear);
            shop_item.startAnimation(animation);
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
            items.get(position).check_count = db.getLookCount(items.get(position).data.getId());
            count_view.setText("" + items.get(position).check_count);
            reply_count_view.setText("" + items.get(position).reply_count);
            if (items.get(position).distance < 1000) {
                distance_view.setText("" + items.get(position).distance + "m");
            } else {
                distance_view.setText(String.format("%.2f", items.get(position).distance / 1000.0) + "km");
            }
            waiting_num_view.setText(" " + items.get(position).data.getWaiting_num());
        }
    }


    private Bitmap resize(Uri uri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        //로컬에 존재하는 파일을 그대로 읽어올때 쓰며 파일경로를 파라미터로 넘겨주면 FileInputStream을 만들어 decodeStream을 한다.
        Bitmap src = BitmapFactory.decodeFile(uri.toString(), options);
        //크기를 재조정 할수 있다.
        Bitmap resized = Bitmap.createScaledBitmap(src, 150, 130, true);

        return resized;
    }

    private void sortWaiting(ArrayList<MultiInfo> list) {
        Collections.sort(list, new Comparator<MultiInfo>() {
            int ret;

            @Override
            public int compare(MultiInfo multiInfo, MultiInfo t1) {
                if (multiInfo.type > t1.type) {
                    ret = 1;
                } else if (multiInfo.type < t1.type) {
                    ret = -1;
                } else {
                    if (multiInfo.data.getWaiting_num() < t1.data.getWaiting_num()) {
                        ret = 1;
                    } else if (multiInfo.data.getWaiting_num() > t1.data.getWaiting_num()) {
                        ret = -1;
                    } else {
                        ret = 0;
                    }
                }
                return ret;
            }
        });
    }

    private void sortCheck(ArrayList<MultiInfo> list) {
        Collections.sort(list, new Comparator<MultiInfo>() {
            int ret;

            @Override
            public int compare(MultiInfo multiInfo, MultiInfo t1) {
                if (multiInfo.type > t1.type) {
                    ret = 1;
                } else if (multiInfo.type < t1.type) {
                    ret = -1;
                } else {
                    if (multiInfo.check_count < t1.check_count) {
                        ret = 1;
                    } else if (multiInfo.check_count > t1.check_count) {
                        ret = -1;
                    } else {
                        ret = 0;
                    }
                }
                return ret;
            }
        });
    }

    private void sortReply(ArrayList<MultiInfo> list) {
        Collections.sort(list, new Comparator<MultiInfo>() {
            int ret;

            @Override
            public int compare(MultiInfo multiInfo, MultiInfo t1) {
                if (multiInfo.type > t1.type) {
                    ret = 1;
                } else if (multiInfo.type < t1.type) {
                    ret = -1;
                } else {
                    if (multiInfo.reply_count < t1.reply_count) {
                        ret = 1;
                    } else if (multiInfo.reply_count > t1.reply_count) {
                        ret = -1;
                    } else {
                        ret = 0;
                    }
                }
                return ret;
            }
        });
    }

    public void sortDistance(ArrayList<MultiInfo> list) {
        Collections.sort(list, new Comparator<MultiInfo>() {
            int ret;

            @Override
            public int compare(MultiInfo multiInfo, MultiInfo t1) {
                if (multiInfo.type > t1.type) {
                    ret = 1;
                } else if (multiInfo.type < t1.type) {
                    ret = -1;
                } else {
                    if (multiInfo.distance > t1.distance) {
                        ret = 1;
                    } else if (multiInfo.distance < t1.distance) {
                        ret = -1;
                    } else {
                        ret = 0;
                    }
                }
                return ret;
            }
        });
    }


    public boolean checkLocationPermission() {
        //권한 승인 아닐때
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //처음이 아닐때
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(context, "위치 정렬을 위해서는 위치 권한을 설정해야합니다.", Toast.LENGTH_LONG).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSON_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public void setSort_curIndex(int index) {
        sort_curIndex = index;
    }

    public void setSort_view(String sort) {
        mFilterViewHolder.sort_view.setText(sort);
    }

    private String[] getFilterList(int category, int sel_index) {
        String[] list = new String[10];
        for(int i=0;i<list.length;i++){
            list[i]="";
        }
        if (category == CATE_RESTAURANT) {
            list[0] = filter_list[0];
        }
        if (category == CATE_FOODTRUCK) {
            list[0] = filter_list[0];
        }
        //디저트가 아닐때 목록 체크
        if (category != CATE_DESSERT) {
            if (sel_index == 0) {
                sel_index = 255;
            }
            for (int i = 0; i < 8; i++) {
                if (checkIndex(sel_index, i)) {
                    list[i + 2] = filter_list[i + 4];
                }
            }
        } else {
            //디저트 목록 체크

        }

        return list;
    }

    //선택된 음식목록을 찾아내는 메소드
    public boolean checkIndex(int sel_index, int index) {
        int temp = sel_index;
        if (((temp >> index) & 1) == 1) {
            return true;
        } else {
            return false;
        }
    }


}



