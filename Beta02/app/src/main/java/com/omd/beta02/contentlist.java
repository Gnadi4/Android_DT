package com.omd.beta02;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;

import static android.text.Selection.setSelection;

public class contentlist extends AppCompatActivity{

    private static String tags11;
    private static String tags22;
    private static String tags33;
    private static String uri1;
    private static int id1;
    private static int id2;
    private String TAG;
    GridView gridView;
    ArrayList<content> list;
    contentListAdapter adapter = null;
    private static final int GALLERY_CODE = 0;
    Uri galleryUri, uri;
    ImageView imageView_update;
    Button button_update;


    @Override
    protected  void onCreate(@Nullable Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.content_list_activity);
        gridView = (GridView) findViewById(R.id.gridView);

        Log.d(TAG,"Problem2");
        requirePermission();

        list = new ArrayList<>();
        adapter = new contentListAdapter(this, R.layout.content_items, list);
        gridView.setAdapter(adapter);

        //get all data from sqlite

        //cursor 기능은 DB에서 가져온 데이터를 쉽게 처리 하기 위해서 제공해주는 인터페이스 이다.
        //cursor는 가져온 DB데이터를 한행 씩 참조하는 것 처럼 사용하여 개발 할 수 있도록 도와준다.
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM EXAMPLEn");

        list.clear();

        //cursor.moveToNext() 는 Cursor를 다음행으로 이동 시켜주는 메소드이다.
        //그 외에도 moveToFirst(), moveToPrevious(), moveToPosition(position), moveToLast()등의 메소드 들이 있다.
        while (cursor.moveToNext()){

            int id = cursor.getInt(0);
            String tags1 = cursor.getString(1);
            String tags2 = cursor.getString(2);
            String tags3 = cursor.getString(3);
            String uri = cursor.getString(4);

            //Arraylist로 선언되어 동적 할당 받는 배열에 값을 하나씩 추가 시켜 주는 코드이다.
            list.add(new content(tags1, tags2, tags3, uri, id));
        }

        //adapter에 연결된 listview를 갱신하는 코드이다.
        adapter.notifyDataSetChanged();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                //dialog에 띄워 줄 두가지 아이템 이름 배열의 인자로 선언
                CharSequence[] items = {"업데이트", "삭제"};

                //dialog라는 객체를 만들어 다이얼로그 기능을 띄워준다, 괄호안의 위치가 다이얼로그를 생성해줄 위치
                AlertDialog.Builder dialog = new AlertDialog.Builder(contentlist.this);


                dialog.setTitle("선택하세요");

                //dialog 클릭시 행동에 관한 기능 구현
                dialog.setItems(items, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int item){
                        //item은 배열 이기 때문에 0 이면 첫번째 인자를 선택한 것이다.
                        if(item == 0){
                            //update
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT * FROM EXAMPLEn");

                            c.moveToPosition(i);

                            id1 = c.getInt(0);
                            tags11 = c.getString(1);
                            tags22 = c.getString(2);
                            tags33 = c.getString(3);
                            uri1 = c.getString(4);

                            //showDialogUpdate(contentlist.this);
                            showDialogUpdate(contentlist.this);

                        }else{
                            //delete
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM EXAMPLEn");
                            c.moveToPosition(i);

                            id2 = c.getInt(0);
                            showDialogDelete(id2);

                        }
                    }
                });
                dialog.show();
            }
        });


    }

    ////////////////\\\\\\\\\\\\\\\\\\onCreate 끝지점//////////////////\\\\\\\\\\\\\\\\

    private void showDialogUpdate(Activity activity){

        final Dialog dialog = new Dialog(activity);

        //content_update xml 파일 을 dialog기능을 통해 불러온다.
        dialog.setContentView(R.layout.content_update);
        dialog.setTitle("Update");

        imageView_update = (ImageView) dialog.findViewById(R.id.imageView_update);
        final EditText editText_tags1 = (EditText) dialog.findViewById(R.id.editText_tags1);
        final EditText editText_tags2 = (EditText) dialog.findViewById(R.id.editText_tags2);
        final EditText editText_tags3 = (EditText) dialog.findViewById(R.id.editText_tags3);
        Button button_update = (Button) dialog.findViewById(R.id.button_update);

        //set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //set height for dialog
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.85);
        dialog.getWindow().setLayout(width, height);


        editText_tags1.setText(contentlist.tags11);
        editText_tags2.setText(contentlist.tags22);
        editText_tags3.setText(contentlist.tags33);
        imageView_update.setImageURI(Uri.parse(contentlist.uri1));
        imageView_update.setScaleType(ImageView.ScaleType.FIT_XY);

        //EditText에서 커서를 가장 끝에 위치시키는 방법
        editText_tags1.setSelection(editText_tags1.length());
        editText_tags2.setSelection(editText_tags2.length());
        editText_tags3.setSelection(editText_tags3.length());

        dialog.show();

        //업데이트 다이얼로그에서 이미지 부분을 클릭할 시 다른 이미지로 변경 할 수 있는 코드
        imageView_update.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //request photo library
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        //새로 작성되거나 지워진 내용을 데이터베이스로 업데이트 시키기 위한 버튼 코드
        button_update.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    MainActivity.sqLiteHelper.updateData(
                            editText_tags1.getText().toString().trim(),
                            editText_tags2.getText().toString().trim(),
                            editText_tags3.getText().toString().trim(),
                            uri1.toString().trim(),
                            id1
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"업데이트 성공!",Toast.LENGTH_SHORT).show();
                }
                catch(Exception error){
                    Log.e("Update error", error.getMessage());
                }
                updateContentlist();
            }
        });

    }

    private void showDialogDelete(final int i){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(contentlist.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to this delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelper.deleteData(i);
                    Toast.makeText(getApplicationContext(), "삭제 성공!", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateContentlist();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();

    }

    private void updateContentlist(){
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM EXAMPLEn");

        list.clear();

        while (cursor.moveToNext()){

            int id = cursor.getInt(0);
            String tags1 = cursor.getString(1);
            String tags2 = cursor.getString(2);
            String tags3 = cursor.getString(3);
            String uri = cursor.getString(4);

            list.add(new content(tags1, tags2, tags3, uri, id));
        }

        adapter.notifyDataSetChanged();
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_CODE) {

            uri1 = String.valueOf(Uri.parse(getRealPathFromURI(data.getData())));

            imageView_update.setScaleType(ImageView.ScaleType.FIT_XY);

            imageView_update.setImageURI(Uri.parse(uri1));
        }
    }

    private String getRealPathFromURI(Uri uri) {
        int column_index=0;
        String[] result={MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, result, null, null, null);

        if (cursor.moveToFirst()) { // Source is local file path
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    void requirePermission() {

        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

        for (String permission : permissions) {

            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                //권한이 허가가 안됬을 경우 요청할 권한을 모집하는 부분
                listPermissionsNeeded.add(permission);
            }

        }

        if (!listPermissionsNeeded.isEmpty()) {

            //권한 요청 하는 부분
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }


    }
}
