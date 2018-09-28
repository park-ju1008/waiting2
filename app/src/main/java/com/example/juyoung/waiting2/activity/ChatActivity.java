package com.example.juyoung.waiting2.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juyoung.waiting2.Chat;
import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.adapter.ChatAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private ArrayList<Chat> mChatArrayList;
    ConstraintLayout mConstraintLayout;
    RecyclerView mRecyclerView;
    ChatAdapter mChatAdapter;
    EditText mContent;
    ImageView mEnter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("chat");
        initView();
        initRecyclerView();
        initFireBase();
    }

    private void initView() {
        mConstraintLayout=(ConstraintLayout)findViewById(R.id.root);
        //키보드 올라 왔는지 안올라왔는 지 확인하여 리스트의 마지막으로 이동
        mConstraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int mRootViewHeight=mConstraintLayout.getRootView().getHeight();
                int mConstraintHeight=mConstraintLayout.getHeight();
                int mDiff=mRootViewHeight-mConstraintHeight;
                if(mDiff>((MyApplication)getApplication()).convertDpToPixel(200)){
                    mRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
                }
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_list);
        mContent = (EditText) findViewById(R.id.editText);
        mEnter = (ImageView) findViewById(R.id.enterview);

        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mEnter.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mEnter.setClickable(true);
                } else {
                    mEnter.setBackgroundColor(getResources().getColor(R.color.gap_background));
                    mEnter.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyApplication.user_nick!=null) {
                    Chat chat = new Chat(MyApplication.user_nick, mContent.getText().toString());
                    myRef.push().setValue(chat);
                    mContent.setText("");
                }
                else{
                    Toast.makeText(view.getContext(),"로그인 후 이용가능합니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initFireBase() {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //새로운 자식이 추가되면 여기가 실행
                Chat chatData = dataSnapshot.getValue(Chat.class);
                mChatArrayList.add(chatData);
                mRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView() {
        mChatArrayList = new ArrayList<>();
        mChatAdapter = new ChatAdapter(mChatArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }
}


