package com.example.juyoung.waiting2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;

public class MyDataBase extends SQLiteOpenHelper {
    private static final String DB_NAME = "place";
    private static final int DB_VERSION = 2;
    public static MyDataBase myDataBase = null;

    public static MyDataBase getInstance(Context context) { //싱글톤 패턴
        if (myDataBase == null) {
            myDataBase = new MyDataBase(context);
        }
        return myDataBase;
    }

    private MyDataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS place(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " name TEXT not null," +
                "first_category TEXT not null," +
                "second_category TEXT not null," +
                "business_hour TEXT not null," +
                "region_name TEXT not null," +
                "location TEXT not null," +
                "explanation TEXT," +
                "phone TEXT," +
                "longitude REAL not null," +
                "latitude REAL not null," +
                "waiting INTEGER," +
                "mainimage TEXT," +
                "subimage TEXT," +
                "boss TEXT not null," +
                "count INTEGER,"+
                "seat TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS reply(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "nickname TEXT not null,"+
                "content TEXT not null," +
                "like_count INTEGER,"+
                "date TEXT not null DEFAULT(datetime('now','localtime')),"+
                "shop_id INTEGER not null)");
    }

    public void insert(ShopInfo item, String userId) {
        //읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO place VALUES(null,'" + item.getName() + "','" + item.getFirst_category() + "','" + item.getSecond_category() + "','" + item.getBusiness_hour() + "','" + item.getRegion() + "','" + item.getLocation() + "','" + item.getExplanation() +
                "','" + item.getPhone() + "'," + item.getX() + "," + item.getY() + "," + item.getWaiting_num() + ",'" + item.getMainImage() + "','" + item.getSubImage() + "','" + userId + "',0,null);");
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void update(int id, ShopInfo place) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE place SET(name,first_category,second_category,business_hour,region_name,location,explanation,phone,longitude,latitude" +
                ",waiting,mainimage,subimage)=(?,?,?,?,?,?,?,?,?,?,?,?,?) WHERE id=?", new Object[]{place.getName(), place.getFirst_category(), place.getSecond_category(), place.getBusiness_hour(), place.getRegion(),
                place.getLocation(), place.getExplanation(), place.getPhone(), place.getX(), place.getY(), place.getWaiting_num(), place.getMainImage(), place.getSubImage(), id});
        db.close();
    }

    public void delete(ShopInfo place, String userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM place WHERE name='" + place.getName() + "'AND boss='" + userId + "';");
        db.close();
    }

    //    public ArrayList<ShopInfo> getResult(String userId) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM place", null);
//        ArrayList<ShopInfo> places = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            places.add(new ShopInfo(cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)
//                    , cursor.getString(8), cursor.getString(9), cursor.getDouble(10), cursor.getDouble(11), cursor.getInt(12)));
//        }
//        return places;
//    }
//
    public ArrayList<Shop> getShopList(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,name,region_name,explanation,longitude,latitude,waiting,mainimage from place where boss=?", new String[]{userId});
        ArrayList<Shop> shops = new ArrayList<>();
        while (cursor.moveToNext()) {
            shops.add(new Shop(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5), cursor.getInt(6), cursor.getString(7)));
        }
        return shops;
    }

    public ArrayList<Shop> getRegoinShopList(String region) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,name,region_name,explanation,longitude,latitude,waiting,mainimage from place where region_name=?", new String[]{region});
        ArrayList<Shop> shops = new ArrayList<>();
        while (cursor.moveToNext()) {
            shops.add(new Shop(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5), cursor.getInt(6), cursor.getString(7)));
        }
        return shops;
    }

    public ShopInfo getShopInfo(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM place WHERE id=?", new String[]{"" + id});
        ShopInfo info = null;
        while (cursor.moveToNext()) {
            info = new ShopInfo(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8)
                    , cursor.getDouble(9), cursor.getDouble(10), cursor.getString(12), cursor.getString(13), cursor.getInt(11));
        }
        return info;
    }

    public Shop getShop(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,name,region_name,explanation,longitude,latitude,waiting,mainimage from place WHERE id=?", new String[]{"" + id});
        Shop shop = null;
        while (cursor.moveToNext()) {
            shop = new Shop(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5), cursor.getInt(6), cursor.getString(7));
        }
        return shop;
    }

    public void lookCountIncrement(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE place SET count=count+1 where id=?", new String[]{"" + id});
        db.close();

    }
    public int getLookCount(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count FROM place where id=?", new String[]{"" + id});
        int count=0;
        while(cursor.moveToNext()){
            count=cursor.getInt(0);
        }
        return count;
    }
    public boolean saveSeat(int id,String sittingList){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE place SET seat=? WHERE id=?",new String[]{sittingList,""+id,});
        db.close();

        return true;
    }

    public String loadSeat(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT seat FROM place where id=?", new String[]{"" + id});
        String sittingList=null;
        while(cursor.moveToNext()){
            sittingList=cursor.getString(0);
        }
        return sittingList;
    }

    public boolean saveReply(int shop_id,Reply reply){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO reply VALUES(null,'"+reply.getNickname()+"','"+reply.getContent()+"',"+reply.getLike()+",'"+reply.getDate()+"',"+shop_id+")");
        db.close();
        return true;
    }

    public  ArrayList<Reply> loadReply(int shop_id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM reply WHERE shop_id=? order by id desc limit 4",new String[]{""+shop_id});
        ArrayList<Reply> list=new ArrayList<>();
        while (cursor.moveToNext()){
            list.add(new Reply(cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4)));
        }
        return list;
    }
    public int getReplyCount(int shop_id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM reply where shop_id=?", new String[]{"" + shop_id});
        int count=0;
        while(cursor.moveToNext()){
            count=cursor.getInt(0);
        }
        return count;
    }

    public  ArrayList<Shop> getFilteredItem(String region,String[] list){
        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor=db.rawQuery("SELECT id,name,region_name,explanation,longitude,latitude,waiting,mainimage FROM place where region_name='"+region+"'AND first_category in(?,?) AND second_category in(?,?,?,?,?,?,?,?)",list);
        ArrayList<Shop> shops = new ArrayList<>();
        while (cursor.moveToNext()) {
            shops.add(new Shop(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5), cursor.getInt(6), cursor.getString(7)));
        }
        return shops;
    }
}
