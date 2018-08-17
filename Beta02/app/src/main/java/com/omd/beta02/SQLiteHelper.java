package com.omd.beta02;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


//DB에 대한 HELPER class를 선언 해줌으로서 Main 클래스 내에서 간편하게 호출 하여 생성 및 디비 관리를 할 수 있다.
public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }


    //데이터 삽입 관련 메소드
    //전달 받은 인자값을 통해 데이터를 입력 받는다
    public void insertData(String tags1 , String tags2,String tags3, String uri){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO EXAMPLEn VALUES (NULL, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, tags1);
        statement.bindString(2, tags2);
        statement.bindString(3, tags3);
        statement.bindString(4, uri);

        statement.executeInsert();


    }

    //업데이트 기능을 구현해 주기 위한 메소드
    public void updateData(String tags1, String tags2, String tags3, String uri, int id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE EXAMPLEn SET tags1 = ?, tags2 = ?, tags3 = ?, uri = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, tags1);
        statement.bindString(2, tags2);
        statement.bindString(3, tags3);
        statement.bindString(4, uri);
        statement.bindDouble(5, (double)id);

        statement.execute();
        database.close();
    }

    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM EXAMPLEn WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();
    }

    //데이터를 읽기 위한 cursor 메소드를 정의 한 것이다.
    //cursor메소드를 통해 주소값을 불러와 원하는 위치의 값을 읽어 올 수 있게 된다.
    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
