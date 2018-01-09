package jp.techacademy.kaneda.kenichi.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Cursor cursor;
    Timer mTimer;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prev click
        final Button prev = (Button) findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("UI_PARTS", "prevボタンをタップしました");
                if(cursor.isFirst()) {
                    if (cursor.moveToLast()) {
                        moveImage();
                    }
                } else {
                    if (cursor.moveToPrevious()) {
                        moveImage();
                    }
                }
            }
        });

        //next click
        final Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("UI_PARTS", "nextボタンをタップしました");
                if(cursor.isLast()) {
                    if (cursor.moveToFirst()) {
                        moveImage();
                    }
                } else {
                    if (cursor.moveToNext()) {
                        moveImage();
                    }
                }
            }
        });

        //start/stop click
        final Button startorstop = (Button) findViewById(R.id.startorstop);
        startorstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("UI_PARTS", "start/stopボタンをタップしました");
                //null = start
                if (mTimer == null) {
                    //button無効
                    prev.setEnabled(false);
                    next.setEnabled(false);

                    //名称変更
                    startorstop.setText("停止");

                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(cursor.isLast()) {
                                cursor.moveToFirst();
                            } else {
                                cursor.moveToNext();
                            }

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    moveImage();
                                }
                            });
                        }
                    }, 2000, 2000);
                } else {
                    //stop
                    //button有効
                    prev.setEnabled(true);
                    next.setEnabled(true);

                    //名称変更
                    startorstop.setText("再生");

                    //timer破棄
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                }
            }
        });


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        //Cursor cursor = resolver.query(
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            moveImage();
        }
        //cursor.close();
    }

    private void moveImage() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);
    }


}
