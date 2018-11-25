package com.example.cjh.aboutball.activity;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjh.aboutball.R;
import com.example.cjh.aboutball.db.User;
import com.example.cjh.aboutball.util.ImageLoaderApplication;
import com.githang.statusbar.StatusBarCompat;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyInformationActivity extends AppCompatActivity {

    private TextView titleText;
    private CircleImageView infoHeadIcon;
    private EditText infoUserName;
    private EditText infoIntro;
    private Button saveInfo;
    private Spinner selectSex;
    private Spinner selectAge;
    private TextView userHobby;
    private TextView userCredit;
    private ImageView userCreditTip;

    private Uri imageUri;       //存储拍摄照片的Uri
    private File outputImage;       //存储拍摄照片
    private String path = "";           //存储照片路径以上传至服务器
    private String sex;
    private int age;
    private String nowUserId;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    public String getNowUserId() {
        return nowUserId;
    }

    public void setNowUserId(String nowUserId) {
        this.nowUserId = nowUserId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.orange));
        titleText = (TextView) findViewById(R.id.title_text);
        infoHeadIcon = (CircleImageView) findViewById(R.id.info_head_icon);
        saveInfo = (Button) findViewById(R.id.title_save);
        infoUserName = (EditText) findViewById(R.id.info_user_name);
        infoIntro = (EditText) findViewById(R.id.info_intro);
        selectSex = (Spinner) findViewById(R.id.select_sex);
        selectAge = (Spinner) findViewById(R.id.select_age);
        userHobby = (TextView) findViewById(R.id.select_hobby);
        userCredit = (TextView) findViewById(R.id.user_credit);
        userCreditTip = (ImageView) findViewById(R.id.user_credit_tip);
        titleText.setText("我的信息");
        saveInfo.setVisibility(View.VISIBLE);

        userHobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = {"篮球", "乒乓球", "羽毛球","网球","足球"};
                final boolean checkedItems[] = {false, false, false, false, false};
                AlertDialog dialog = new AlertDialog.Builder(MyInformationActivity.this)
                        .setTitle("选择你爱好的运动类型")
                        .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                checkedItems[which] = isChecked;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final StringBuilder result = new StringBuilder();
                                boolean flag = false;
                                for (int i = 0; i < checkedItems.length; i++) {
                                    if (checkedItems[i]) {
                                        if(flag){
                                            result.append("  ").append(items[i]);
                                        }else{
                                            result.append(items[i]);
                                            flag = true;
                                        }
                                    }
                                }
                                Log.d("爱好", result.toString());
                                User user = new User();
                                user.setUserHobby(result.toString());
                                user.update(getNowUserId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e == null){
                                            userHobby.setText(result.toString());   //更新界面UI
                                        }else{
                                            Toast.makeText(MyInformationActivity.this, "查询1失败！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(18);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(18);
            }
        });
        //点击tip按钮显示对话框，介绍信用值
        userCreditTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(MyInformationActivity.this)
                        .setIcon(R.drawable.ic_tip_icon)
                        .setTitle("关于信用值")//设置对话框的标题
                        .setMessage("信用值是用于防止用户随意退出约单的一项约束机制，为了不影响他人，约单完成" +
                                "前随意退出约单将扣取一定信用值，信用值低于0的用户短期内将不能再参与约单")//设置对话框的内容
                        //设置对话框的按钮
                        .create();
                dialog.show();
            }
        });
        Intent intent = getIntent();
        setNowUserId(intent.getStringExtra("user_id"));
        QueryUser();

        //获取下拉框选择项
        selectSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sex = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                age = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //保存按钮点击事件
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View focusView = null;
                if(TextUtils.isEmpty(infoUserName.getText())){
                    infoUserName.setError("用户名不能为空!");
                    focusView = infoUserName;
                    focusView.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(infoIntro.getText())){
                    infoIntro.setText("无");
                }
                //修改个人信息
                User user = new User();
                user.setUserName(infoUserName.getText().toString());
                user.setUserSex(sex);
                user.setUserAge(age);
                user.setUserIntro(infoIntro.getText().toString());
                user.update(getNowUserId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            Toast.makeText(MyInformationActivity.this, "修改个人信息成功!", Toast.LENGTH_SHORT).show();
                            MainActivity.setFlag("user");
                            MyInformationActivity.this.finish();
                        }else{
                            Toast.makeText(MyInformationActivity.this, "查询2失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        //点击头像进行更换，包括拍照和相册选取
        infoHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MyInformationActivity.this);
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

    //缓存当前所拍图片
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
            imageUri = FileProvider.getUriForFile(MyInformationActivity.this,
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
        if (ContextCompat.checkSelfPermission(MyInformationActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyInformationActivity.this, new String[]{ android.Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        } else {
            openAlbum();
        }
    }

    //打开相册
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
            infoHeadIcon.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (outputImage != null) {
            path = outputImage.getPath();
        }
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
            infoHeadIcon.setImageBitmap(bitmap);
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
                    User user = new User();
                    user.setHeadIcon(icon);
                    user.update(getNowUserId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Toast.makeText(MyInformationActivity.this, "头像上传成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(MyInformationActivity.this, "上传头像失败，请重试！" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
            }
        });
    }

    //加载用户基本信息，注意头像
    private void QueryUser(){
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(getNowUserId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e == null){
                    //加载用户名、信用值、性别、年龄、爱好
                    infoUserName.setText(user.getUserName());
                    userCredit.setText(String.valueOf(user.getUserCredit()));
                    setSpinnerItemSelectedByValue(selectSex, user.getUserSex());
                    setSpinnerItemSelectedByValue(selectAge, String.valueOf(user.getUserAge()));
                    if(TextUtils.isEmpty(user.getUserIntro())){
                        infoIntro.setText("");
                    }else{
                        infoIntro.setText(user.getUserIntro());
                    }
                    if(TextUtils.isEmpty(user.getUserHobby())){
                        userHobby.setText("未设置");
                    }else{
                        userHobby.setText(user.getUserHobby());
                    }
                    //加载头像
                    if(user.getHeadIcon() != null){
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(user.getHeadIcon().getFileUrl(), infoHeadIcon, ImageLoaderApplication.options);
                    }
                }else{
                    Toast.makeText(MyInformationActivity.this, "查询3失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter adapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= adapter.getCount();
        for(int i=0;i<k;i++){
            if(TextUtils.isEmpty(value)) {
                spinner.setSelection(0, true);
            }else{
                if(value.equals(adapter.getItem(i).toString())){
                    spinner.setSelection(i,true);// 默认选中项
                    break;
                }
            }
        }
    }
}
