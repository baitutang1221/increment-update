package com.itfitness.incrementupdatedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.itfitness.incrementupdatedemo.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    private TextView tv_version;
    private Button bt_update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_version = findViewById(R.id.tv_version);
        bt_update = findViewById(R.id.bt_update);
        tv_version.setText("2.0");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
            }
        }

//通过此方法也可获取apk地址，不过合成patch之后解析失败，所以废弃此方法
//        String oldPath = extract(this);
//        Log.d("neo","oldPath ==== " + oldPath);


        bt_update.setOnClickListener(v->{
            try {
                new Thread(() -> {
                    // /storage/emulated/0/Android/data/com.itfitness.incrementupdatedemo/files/Download/old.apk
                    File oldApkFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "old.apk");
                    File newApkFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "new.apk");
                    File patchFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.patch");
                    PatchUtil.patchAPK(oldApkFile.getAbsolutePath(),newApkFile.getAbsolutePath(),patchFile.getAbsolutePath());
                    //安装APK
                    AppUtils.installApp(newApkFile);
                }).start();
            }catch (Exception e){

            }

//            Toast.makeText(this, "已经是最新版本了", Toast.LENGTH_SHORT).show();
        });


//        File oldApkFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "old.apk");
//        File newApkFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "new.apk");
//
//        String oldFileMD5 = getFileMD5(oldApkFile);
//        String newFileMD5 = getFileMD5(newApkFile);
//        Log.d("neo","oldFileMD5 ==== " + oldFileMD5);
//        Log.d("neo","newFileMD5 ==== " + newFileMD5);
    }

    /**
     * 提取本应用的apk路径
     */
    public static String extract(Context context) {
        context = context.getApplicationContext();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return applicationInfo.sourceDir;
    }

    /**
     * 获取单个文件的MD5值
     *
     * @param file 文件
     * @return 文件MD5值
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}