package com.omd.beta02;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //코드의 숫자에 따라 기능이 실행 되도록 하기 위한 코드 설정
    private static final int CAMERA_CODE = 10;
    private static final int GALLERY_CODE = 0;

    //전역 으로 선언 해주어야 하는 변수 또는 상수 들이다.
    //전역 으로 선언 해줄 시 데이터 값을 불러와 사용하기 편해지기 때문에 전역으로 선언해 주면 좋다
    //모든 변수를 전역으로 선언하게 되는 것은 좋지 않다
    private String mCurrentPhotoPath;
    EditText editText_tags1, editText_tags2, editText_tags3;
    Button button_save2;
    int i = 0;
    public static SQLiteHelper sqLiteHelper;
    private String TAG;
    private Uri contentUri , photoUri, galleryUri, newUri, uri2;
    private String getmCurrentPhotoPath;

    //자바로 따지면 main class의 기능을 하는 onCreate 메소드 이다.
    //자바의 main class와 차이점 이라면 onCreate 메소드는 클래스당 필요 하다는 점이다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //가상머신에서 구동 했을시 문제 발생에 대한 문제점 파악을 위한 로그 추적 기능이다.
        Log.d(TAG,"Problem");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //사진 촬영에 대한 허가권을 얻는 기능
        //onCreate 밖에 선언해준 메소드를 이용 하여 기능 구현을 도와준다.
        requirePermission();

        init();

        //sqLiteHelper class로 부터 DB 생성에 관련한 메소드들과 DB를 호출 하고
        //Table을 생성해주는 코드를 입력 하여 준다.
        sqLiteHelper = new SQLiteHelper(this, "Example01.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS EXAMPLEn (id INTEGER PRIMARY KEY AUTOINCREMENT, tags1 VARCHAR, tags2 VARCHAR,tags3 VARCHAR, uri VARCHAR)");

        //앞으로 사진출력용으로 사용 될 xml에 대한 선언을 해준다.
        final ImageView imageView = (ImageView) findViewById(R.id.picture_view);

        /*//갤러리 불러오기 기능으로 사용하고자 하였던 버튼 기능
        Button gallery = (Button)findViewById(R.id.button_gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageResource(0);
                //pickUpPicture();
            }
        });*/

        //button_save2는 현재 view에 표시되고있는 사진과 추가된 태그를 데이터 베이스로 넘겨주는 기능을 한다.
        Button button_save2 = (Button) findViewById(R.id.button_save2);

        button_save2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                try {
                    //i로 설정해준 변수 값이 1일 경우는 갤러리에서 불러온 파일에 대한 uri를 받아온 경우이다
                    if(i==1) {
                        newUri = galleryUri;
                        sqLiteHelper.insertData(
                                editText_tags1.getText().toString().trim(),
                                editText_tags2.getText().toString().trim(),
                                editText_tags3.getText().toString().trim(),
                                newUri.toString().trim()
                        );

                        editText_tags1.setText("");
                        editText_tags2.setText("");
                        editText_tags3.setText("");


                        galleryUri = null;
                    }
                    //i로 설정해준 변수 값이 2 일 경우는 카메라 촬영을 통해 얻은 이미지 파일의 uri이다
                    if(i==2){
                        newUri = photoUri;
                        sqLiteHelper.insertData(
                                editText_tags1.getText().toString().trim(),
                                editText_tags2.getText().toString().trim(),
                                editText_tags3.getText().toString().trim(),
                                newUri.toString().trim()
                        );

                        editText_tags1.setText("");
                        editText_tags2.setText("");
                        editText_tags3.setText("");

                        photoUri = null;
                    }

                    //uri를 받아온 후 i 변수값 초기화 과정
                    i=0;

                    Toast.makeText(getApplicationContext(), "기록 저장 성공",Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });


        try {
            //button_save 버튼은 촬영 된 사진에 대해 넘어온 정보 값들을 받아 와 실제 기기 내의 저장 공간에 저장 해주는 형식이다
            Button button_save = (Button) findViewById(R.id.button_save);

            //Button button_save = (Button) findViewById(R.id.button_save);
            button_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //사진 저장을 위해 galleryAddPic이라는 메소드를 구현 하였고
                    //메소드를 호출 하게 되면 카메라 기능으로 부터 정보를 받아와 galleryAddPic으로 정보를 넘겨 주게 된다.
                    galleryAddPic();
                }
            });
        }catch(NullPointerException e){
            Toast.makeText(this, "이미 저장되거나 없는 이미지입니다", Toast.LENGTH_SHORT).show();
        }


        //camera_button을 이용한 카메라 기능과의 연동
        Button button = (Button) findViewById(R.id.camera_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean camera = ContextCompat.checkSelfPermission
                        (view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

                boolean write = ContextCompat.checkSelfPermission
                        (view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if (camera && write) {
                    //사진찍은 인텐트 코드 넣기
                    takePicture();
                } else {
                    Toast.makeText(MainActivity.this, "카메라 권한 및 쓰기 권한을 주지 않았습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        /*//아직 구현 되지 않은 기능
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    //////////////// oncreate 끝지점\\\\\\\\\\\\\\\\\\\\
    //현재 지점 부터는 외부에 선언되는 메소드 또는 변수들이다.


    //글과 사진 저장을 위해 선언한 버튼 및 edittext들의 view에 대한 initial 선언 메소드
    private void init(){
        editText_tags1 = (EditText) findViewById(R.id.editText_tags1);
        editText_tags2 = (EditText) findViewById(R.id.editText_tags2);
        editText_tags3 = (EditText) findViewById(R.id.editText_tags3);
        button_save2 = (Button) findViewById(R.id.button_save2);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //FragmentManager fragmentManager = getFragmentManager();
        if (id == R.id.nav_gallery) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_CODE);

            /*setTitle("camera");
            FirstFragment fragment = new FirstFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment, "FirstFragment");
            fragmentTransaction.commit();*/
        } else if (id == R.id.nav_tags) {


            Intent intent = new Intent(MainActivity.this, contentlist.class);
            startActivity(intent);


            /*setTitle("gallery");
            SecondFragment fragment = new SecondFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment, "SecondFragment");
            fragmentTransaction.commit();*/
        } else if (id == R.id.nav_slideshow) {



        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    void takePicture(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(this, "com.omd.beta02.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
            startActivityForResult(intent, CAMERA_CODE);
            i=2;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        //super.onActivityResult(resultCode, resultCode, data);

        if(requestCode==CAMERA_CODE){
            Button button_save1 = (Button) findViewById(R.id.button_save);
            ImageView imageView = (ImageView) findViewById(R.id.picture_view);
            imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            button_save1.setVisibility(View.VISIBLE);
        }
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            //절대경로로 받아오기
            //바로 아래의 주석 처리 된 코드가 그냥 uri를 받아와 그 uri 정보를 통해 갤러리로부터 이미지 불러오기 기능을
            //구현하고자 하였던 코드이다.
            //uri = data.getData();
            //따라서 아래의 두줄과 같이 uri를 똑같이 선언 해준후 그 uri를 절대 경로를 얻는 메소드(미리 선언해 놓은)를 이용해
            //절대 경로를 얻어 언제 어디서나 같은 경로로 이미지 파일을 얻어올 수 있게 해 준 것이다.
            uri2 = data.getData();
            galleryUri = Uri.parse(getRealPathFromURI(uri2));

            Button button_save2 = (Button) findViewById(R.id.button_save);
            ImageView imageView = (ImageView) findViewById(R.id.picture_view);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageURI(galleryUri);
            button_save2.setVisibility(View.GONE);
            i=1;

        }
    }

    private void galleryAddPic() {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            Toast.makeText(this, "사진 저장 완료!", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //uri를 통한 절대 경로를 알아오는 메소드
    //카메라를 통한 이미지 촬영은 촬영 되는 순간 저장 되는 경로를 알 수 있으며 기본적인 이미지 내에 같이 저장 되어 지기 때문에 카메라를 통한 이미지 촬영에서는 오류가 나지 않았다.
    //갤러리로 부터의 이미지 불러오기는 uri를 이용해 불러오기 기능을 사용 하기 때문에 실제 기기나 가상 머신 환경에서마다의 경로 설정 차이로 인해
    //두번째 불러오기 부터 오류가 났었기 때문에 uri를 통해 절대 경로를 얻어와 어디서나 오류 없이 절대적 경로를 이용한 갤러리로부터의 이미지 불러오기 기능을 구현 하고자 하였다.
    private String getRealPathFromURI(Uri galleryUri) {
        int column_index=0;
        String[] result={MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(galleryUri, result, null, null, null);

        if (cursor.moveToFirst()) { // Source is local file path
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }
}
