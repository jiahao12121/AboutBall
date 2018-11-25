package com.example.cjh.aboutball.activity;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.Test;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.jar.Manifest;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class TestActivity extends AppCompatActivity {

    private CircleImageView headIcon;

    private TextView name;

    private Uri imageUri;

    private File outputImage;

    private String nowUri;

    private String username;

    private Bitmap nowBm;

    String path = "";

    private ImageLoader imageLoader;

    private DisplayImageOptions options; // 设置图片显示相关参数

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    public static final int LOAD_NEW_ICON = 1;

    public static final int LOAD_DEFAULT_ICON = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        headIcon = (CircleImageView) findViewById(R.id.touxiang);
        name = (TextView) findViewById(R.id.mingzi);
        ImageLoaderApplication.initImageLoader(this);
        imageLoader = ImageLoader.getInstance();
        Bmob.initialize(this, "a6ae281975bc11279c375a74884bcca9");
        initIcon();
        headIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(TestActivity.this);
                String title = "选择获取图片方式";
                String[] items = new String[]{"拍照", "相册", "取消"};
                dialog.setTitle(title);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                //拍照
                                pickImageFromCamera();
                                break;
                            case 1:
                                //选择相册
                                pickImageFromAlbum();
                                break;
                            case 2:
                                //取消
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
            }
        });
    }

    private Handler handler = new Handler(){
      public void handleMessage(Message msg){
          switch (msg.what){
              case LOAD_NEW_ICON:
                  imageLoader.displayImage(nowUri, headIcon, options);
                  name.setText(username);
                  break;
              case LOAD_DEFAULT_ICON:
                  headIcon.setImageResource(R.drawable.ic_pear);
                  break;
              default:
                  break;
          }
      }
    };
    private void initIcon(){
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_myinfo) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_myinfo) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_myinfo) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成
        //Bmob访问默认在子线程中进行，因此尽量配合Message使用（否则主线程可能先于子线程进行引起报错）

        BmobQuery<Test> query = new BmobQuery<Test>();
        query.getObject("JKSmAAAQ", new QueryListener<Test>() {
            @Override
            public void done(Test test, BmobException e) {
                if (e == null) {
                    BmobFile icon = test.getHeadIcon();
                    Log.d("TestActivity", test.getUserName());
                    if (icon != null) {
                        username = test.getUserName();
                        nowUri = icon.getFileUrl();
                        Message message = new Message();
                        message.what = LOAD_NEW_ICON;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = LOAD_DEFAULT_ICON;
                        handler.sendMessage(message);
                    }
                } else {
                    Toast.makeText(TestActivity.this, "查询用户失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("TestActivity", e.getMessage());
                }
            }
        });
    }


/*
    public void getBitmap(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(nowPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200){
                        InputStream inputStream = conn.getInputStream();
                        nowBm = BitmapFactory.decodeStream(inputStream);
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
*/

    private void pickImageFromCamera() {
        outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(TestActivity.this,
                    "com.example.cjh.AboutBall.fileprovider", outputImage);
        }else{
            imageUri = Uri.fromFile(outputImage);
        }

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
    /*动态申请对手机SD卡的处理权限*/
    private void pickImageFromAlbum() {
        if (ContextCompat.checkSelfPermission(TestActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TestActivity.this, new String[]{ android.Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    setPicToView();     //显示头像
                    break;
                case CHOOSE_PHOTO:
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                        break;

                    }
            }
        }
    }

    private void setPicToView() {
        try {
            // 将拍摄的照片显示出来
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            headIcon.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (outputImage != null) {
            path = outputImage.getPath();
        }
        Log.d("TestActivity", path);
        uploadIcon(path); //将更换头像上传至服务器
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            headIcon.setImageBitmap(bitmap);
            uploadIcon(imagePath);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }



    private void uploadIcon(String path){
        final BmobFile icon = new BmobFile(new File(path));
        icon.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Test test = new Test();
                    test.setHeadIcon(icon);
                    test.update("JKSmAAAQ", new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Toast.makeText(TestActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(TestActivity.this, "上传头像失败，请重试！" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
            }
        });
    }
}