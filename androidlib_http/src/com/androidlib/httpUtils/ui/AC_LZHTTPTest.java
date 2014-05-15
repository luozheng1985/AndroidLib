package com.androidlib.httpUtils.ui;

import java.io.File;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.androidlib.httpUtils.HttpControl;
import com.androidlib.httpUtils.HttpProgressCallBack;
import com.androidlib.httpUtils.HttpResponseCallBack;
import com.androidlib.httpUtils.HttpUtils;
import com.androidlib.httpUtils.L;
import com.androidlib.httpUtils.R;

public class AC_LZHTTPTest extends Activity{
    
    private TextView tv_httpResponse;
    private Button btnHttp;
    private Handler handler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_lz_http_test);
        
        tv_httpResponse = (TextView) findViewById(R.id.tv_httpResponse);
        tv_httpResponse.setText("显示HTTP结果");
        
        btnHttp = (Button) findViewById(R.id.btn_get);
        btnHttp.setOnClickListener(new MOnClick(4));
    }

    private class MOnClick implements OnClickListener{

        private int reqCode;
        private String URL_STR = "http://54.248.127.251:18080/IdentityServer/LoginUserService?name=fz";
        
        public MOnClick(int reqCode) {
            super();
            this.reqCode = reqCode;
        }

        @Override
        public void onClick(View v) {
            switch (reqCode) {
            // http://54.248.127.251:18080/IdentityServer/LoginUserService?name=fz
            // http://222.92.116.173:8081/ShuTong/bytalkLoginAction?loginname=18626029399
            // http://222.92.116.172:8888/post.php
            case 3:// GET/POST
                HttpUtils.getInstance().executePost(URL_STR,null, new HttpResponseCallBack() {
                    @Override
                    public void onComplete(Exception e, final String strResponse) {
                        if(e != null){
                            e.printStackTrace();
                            return ;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_httpResponse.setText(strResponse);
                            }
                        });
                    }
                });
                break;
            case 4:// 上传
                final HttpControl control = new HttpControl(false);
                URL_STR = "http://222.92.116.172:8888/post.php";// http://222.92.116.172:8888/ftp/
                final File file = new File(Environment.getExternalStorageDirectory(),"A88_99_00.png");
                HttpUtils.getInstance().exePostUploadFile(URL_STR, "mediaFile", file.getAbsolutePath(),control, new HttpProgressCallBack() {
                    @Override
                    public void onLoading(final int progress) {
                        // TODO Auto-generated method stub
                        L.i("onLoading file: %s <%d>", file.getAbsolutePath(),progress);
                        
//                        if(progress>60){
//                            control.setCancel(true);
//                        }
                        
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_httpResponse.setText(progress + "%");
                            }
                        });
                    }
                    
                    @Override
                    public void onComplete(Exception e, final String strResponse) {
                        // TODO Auto-generated method stub
                        String con = strResponse;
                        if(e != null){
                            con = e.getMessage();
                            L.e(con);
                            return ;
                        }
                        
                        L.e("onComplete file: %s result <%s>", file.getAbsolutePath(),strResponse);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_httpResponse.setText(strResponse);
                                tv_httpResponse.setTextColor(Color.RED);
                            }
                        });
                    }
                });
                break;
            case 5:// 下载
                final HttpControl controlDownload = new HttpControl(false);
                URL_STR = "http://222.92.116.172:8888/ftp/20140213/2253326423094907/20140213152848023_2253327910150211_2253326423094907.png";
                final File saveFile = new File(Environment.getExternalStorageDirectory(),"httpDownload.png");
                HttpUtils.getInstance().exeDownloadFile(URL_STR, saveFile,controlDownload, new HttpProgressCallBack() {
                    
                    @Override
                    public void onLoading(final int progress) {
                        // TODO Auto-generated method stub
                        L.i("onLoading file: %s <%d>", saveFile.getAbsolutePath(),progress);
                        
                        if(progress > 61){
                            controlDownload.setCancel(true);
                        }
                        
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_httpResponse.setText(progress + "%");
                            }
                        });
                    }
                    
                    @Override
                    public void onComplete(Exception e, final String strResponse) {
                        // TODO Auto-generated method stub
                        L.e("onComplete file: %s", saveFile.getAbsolutePath());
                        
                        String con = strResponse;
                        if(e != null){
                            con = e.getMessage();
                            L.e(con);
                        }
                        final String conn = con;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_httpResponse.setText(conn);
                                tv_httpResponse.setTextColor(Color.RED);
                            }
                        });
                    }
                });
                break;
            default:
                break;
            }
            
        }
        
    }

}
