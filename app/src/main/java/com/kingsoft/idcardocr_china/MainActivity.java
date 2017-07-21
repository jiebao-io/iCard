package com.kingsoft.idcardocr_china;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.kingsoft.idcardocr_china.idcardocr.CameraActivity;
import com.kingsoft.idcardocr_china.idcardocr.CardIdChinaEntity;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String DEFAULT_LANGUAGE = "chi_sim";
    private static final int GETPERMISSION_SUCCESS_CARDID = 1;//获取权限成功跳身份证识别
    private static final int GETPERMISSION_SUCCESS_BANKCARD = 2;//获取权限成功跳银行卡识别
    private static final int GETPERMISSION_FAILER = 3;//获取权限失败
    private TextView tv_id, tv_name, tv_sex, tv_nation, tv_date, tv_address, tv_unit, tv_expirydate;
    private int MY_SCAN_REQUEST_CODE = 100;
    private int MY_SCAN_REQUEST_CODE1 = 101;
    private Context mContext;
    private MyHandler myHandler = new MyHandler();
    private boolean front = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_nation = (TextView) findViewById(R.id.tv_nation);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_unit = (TextView) findViewById(R.id.tv_unit);
        tv_expirydate = (TextView) findViewById(R.id.tv_expirydate);
        findViewById(R.id.btn_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getPermissions();
                front = true;
                requestAllPermission();
            }
        });
        findViewById(R.id.btn_go1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                front = false;
//                bankCardPermission();
                requestAllPermission();
            }
        });
        String path = Environment.getExternalStorageDirectory().getPath() + "/tessdata";
        // 判断字库是否存在
        if (!IsFileExists(path + "/" + DEFAULT_LANGUAGE + ".traineddata")) {
            //推送字库到SD卡
            CopyAssets(this, R.raw.chi_sim, Environment.getExternalStorageDirectory().getPath() + "/tessdata", DEFAULT_LANGUAGE + ".traineddata");
        }
    }

    private void requestAllPermission() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(MainActivity.this,
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        myHandler.sendEmptyMessage(GETPERMISSION_SUCCESS_CARDID);
                    }

                    @Override
                    public void onDenied(String permission) {
                        myHandler.sendEmptyMessage(GETPERMISSION_FAILER);
                    }
                });
    }

    private void bankCardPermission() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(MainActivity.this,
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        myHandler.sendEmptyMessage(GETPERMISSION_SUCCESS_BANKCARD);
                    }

                    @Override
                    public void onDenied(String permission) {
                        myHandler.sendEmptyMessage(GETPERMISSION_FAILER);
                    }
                });
    }

    //因为权限管理类无法监听系统，所以需要重写onRequestPermissionResult方法，更新权限管理类，并回调结果。这个是必须要有的。
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_SCAN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CardIdChinaEntity entity = (CardIdChinaEntity) data.getParcelableExtra("entity");
            boolean isFront = data.getBooleanExtra("isFront", true);
            if (entity == null) {
                return;
            }
            if (isFront) {
                tv_id.setText("公民身份号码：" + entity.getIdCardNo());
                tv_name.setText("姓名：" + entity.getName());
                tv_sex.setText("性别：" + entity.getSex());
                tv_nation.setText("民族：" + entity.getNation());
                tv_date.setText("出生：" + entity.getYear());// + "年" + entity.getMonth() + "月" + entity.getDay() + "日"
                tv_address.setText("住址：" + entity.getAddress());
            } else {
                tv_unit.setText("性别：" + entity.getIssuingAuthority());
                tv_expirydate.setText("民族：" + entity.getExpiryDate());
            }
        } else if (requestCode == MY_SCAN_REQUEST_CODE1) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getFormattedCardNumber() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            } else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            tv_expirydate.setText(resultDisplayStr);
        }
    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETPERMISSION_SUCCESS_CARDID:
                    Intent scanIntent = new Intent(mContext, CameraActivity.class);
                    scanIntent.putExtra("front", front);
                    startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
                    break;
                case GETPERMISSION_SUCCESS_BANKCARD:
                    Intent bankCardIntent = new Intent(mContext, CardIOActivity.class);
                    startActivityForResult(bankCardIntent, MY_SCAN_REQUEST_CODE1);
                    break;
                case GETPERMISSION_FAILER:
                    Toast.makeText(mContext, "此功能须获摄像头权限", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //load OpenCV engine and init OpenCV library
        OpenCVLoader.initDebug();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, getApplicationContext(), mLoaderCallback);
        Log.i("", "onResume sucess load OpenCV...");
    }

    /**
     * 将资源转换成文件
     *
     * @param context    上下文
     * @param resourceId 资源Id
     * @param filePath   目标文件
     * @param fileName   目标文件地址
     * @return 是否成功
     */
    public static void CopyAssets(Context context, int resourceId, String filePath, String fileName) {
        InputStream is = null;
        FileOutputStream fs = null;
        try {
            File destDir = new File(filePath);
            destDir.mkdir();
            is = context.getResources().openRawResource(resourceId);
            File file = new File(filePath + "/" + fileName);
            file.createNewFile();
            fs = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length = is.read(buffer);
            while (length > 0) {
                fs.write(buffer, 0, length);
                length = is.read(buffer);
            }
            fs.flush();// 刷新缓冲区
            is.close();
            fs.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            try {
                if (fs != null)
                    fs.close();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName 文件名，必须为文件完整路径
     * @return 文件存在返回true，文件不存在返回false
     */
    public static boolean IsFileExists(String fileName) {
//        try {
        File file = new File(fileName);
        return file.exists();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
    }
}
