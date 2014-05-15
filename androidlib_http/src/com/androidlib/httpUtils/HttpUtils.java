package com.androidlib.httpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.androidlib.httpUtils.CustomMultiPartEntity.ProgressListener;



public class HttpUtils{
    
    private static final int connection_timeout = 10*1000;
    private static final int read_timeout = 10*1000;
    
    private ExecutorService cachedThreadPool = Executors.newFixedThreadPool(5);
    
    private static HttpUtils instance = new HttpUtils();
    
    private HttpUtils(){
    }
    
    public static HttpUtils getInstance(){
        return instance;
    }
    
    public void excuteGet(final String urlStr){
        excuteGet(urlStr,null);
    }
    
    public void excuteGet(final String urlStr,final HttpResponseCallBack callBack){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final long start = System.currentTimeMillis();
                    L.i("excuteGet THREAD ID IS %s", String.valueOf(Thread.currentThread().getId()));
                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("connection", "Keep-Alive");
                    connection.setRequestProperty("Charsert", "UTF-8");
                    connection.setConnectTimeout(connection_timeout);
                    connection.setReadTimeout(read_timeout);
                    connection.setRequestMethod("GET");
                    if (connection.getResponseCode() == HttpStatus.SC_OK) {
                        InputStream inStream = connection.getInputStream();
                        if (inStream != null) {
                           String response = InputStreamUtils.InputStreamTOString(inStream, HTTP.UTF_8);
                           L.e("excuteGet spent time is : %s", String.valueOf(System.currentTimeMillis()-start));
                           if(callBack != null) callBack.onComplete(null, response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(callBack != null) callBack.onComplete(e, null);
                }
            }
        });
    }
    
    
    
    public void executePost(final String url, final Map<String, String> params){
        executePost(url, params, null);
    }
    
    public void executePost(final String url, final Map<String, String> params,final HttpResponseCallBack callBack){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final long start = System.currentTimeMillis();
                    L.i("executePost THREAD ID IS %s", String.valueOf(Thread.currentThread().getId()));
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    if (params != null && params.size() > 0) {
                        Iterator<String> iterator = params.keySet().iterator();
                        List<BasicNameValuePair> requestParam = new ArrayList<BasicNameValuePair>();
                        while (iterator.hasNext()) {
                            String param = iterator.next();
                            String value = params.get(param);
                            requestParam.add(new BasicNameValuePair(param, value));
                        }
                        post.setEntity(new UrlEncodedFormEntity(requestParam, "UTF-8"));
                    }
                    HttpResponse response = httpClient.execute(post);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            L.e("executePost spent time is : %s", String.valueOf(System.currentTimeMillis()-start));
                            String res = EntityUtils.toString(entity, "UTF-8");
                            if(callBack != null) callBack.onComplete(null, res);
                        }else{
                            throw new IllegalArgumentException("post response is null");
                        }
                    }else{
                        throw new IllegalArgumentException("post failed !!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(callBack != null) callBack.onComplete(e, null);
                }
            }
        });
    }

    
    public void exePostUploadFile( String url, String fileParam, String filePath,
             HttpControl control, HttpProgressCallBack callBack){
        
       Map<String,Object> params=new HashMap<String, Object>(); 
       params.put(fileParam, new File(filePath));
       exePostUploadFile(url, params,  control, callBack);
    }
    
    public void exePostUploadFile(final String url,final Map<String, Object> params, final HttpControl control,
            final HttpProgressCallBack callBack)
    {
        cachedThreadPool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                String serverResponse = null;
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext httpContext = new BasicHttpContext();
                final HttpPost httpPost = new HttpPost(url);
                try
                {
                    CustomMultiPartEntity multipartContent = new CustomMultiPartEntity();
                    for(Map.Entry<String,Object> entry:params.entrySet())
                    {
                        if(entry.getValue() instanceof File)
                        {
                            multipartContent.addPart(entry.getKey(), new FileBody((File)entry.getValue()));
                        }
                        else if(entry.getValue() instanceof FileBodyModel)
                        {
                            FileBodyModel fileModel = (FileBodyModel) entry.getValue();
                            multipartContent.addPart(entry.getKey(), new FileBody(fileModel.getFile(), //
                                    fileModel.getRemoteFileName(), fileModel.getMimeType(), fileModel.getCharset()));
                        }
                        else if(entry.getValue() instanceof String)
                        {
                            multipartContent.addPart(entry.getKey(),new StringBody((String) entry.getValue()));
                        }
                        else  if(entry.getValue() instanceof Double)
                        {
                            multipartContent.addPart(entry.getKey(),new StringBody(String.valueOf(entry.getValue()))); 
                        }
                        else  if(entry.getValue() instanceof Integer)
                        {
                            multipartContent.addPart(entry.getKey(),new StringBody(String.valueOf(entry.getValue()))); 
                        }  
                        else  if(entry.getValue() instanceof Float)
                        {
                            multipartContent.addPart(entry.getKey(),new StringBody(String.valueOf(entry.getValue()))); 
                        } 
                        else  if(entry.getValue() instanceof Long)
                        {
                            multipartContent.addPart(entry.getKey(),new StringBody(String.valueOf(entry.getValue()))); 
                        } else  if(entry.getValue() instanceof Boolean)
                        {
                            multipartContent.addPart(entry.getKey(),new StringBody(String.valueOf(entry.getValue()))); 
                        } 
                        else  if(entry.getValue() instanceof Byte)
                        {
                            multipartContent.addPart(entry.getKey(),new StringBody(entry.getValue().toString())); 
                        } 
                        else
                        {
                            throw new IllegalStateException("Can not  resolve the param:"+entry.getValue().toString());
                        }
                    }
                    
                    final long totalSize = multipartContent.getContentLength();
                    httpPost.setEntity(multipartContent);

                    multipartContent.setListener(new ProgressListener()
                    {
                        @Override
                        public void transferred(long num)
                        {
                            if (!control.isCancel())
                            {
                                int progress = (int) ((num / (float) totalSize) * 100);
                                progress = progress > 100 ? 100 : progress;
                                if (callBack != null)
                                    callBack.onLoading(progress);
                            } else
                            {
                                httpPost.abort();
                                if (callBack != null)
                                    callBack.onComplete(new IllegalArgumentException(HttpControl.task_cancel), null);
                            }
                        }
                    });
                    HttpResponse response = httpClient.execute(httpPost, httpContext);

                    serverResponse = EntityUtils.toString(response.getEntity());
                    if (callBack != null)
                        callBack.onComplete(null, serverResponse);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    if (callBack != null)
                        callBack.onComplete(e, null);
                }
            }
        });

    }
    
    
    public void exeDownloadFile(final String downloadUrl, final File saveFilePath, 
            final HttpControl control,final HttpProgressCallBack callBack) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                int fileSize = -1;
                int downFileSize = 0;
                int progress = 0;
                try {
                    URL url = new URL(downloadUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(connection_timeout);
                    conn.setReadTimeout(read_timeout);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        fileSize = conn.getContentLength();
                        InputStream is = conn.getInputStream();
                        FileOutputStream fos = new FileOutputStream(saveFilePath);
                        byte[] buffer = new byte[1024];
                        int i = 0;
                        int tempProgress = -1;
                        while ((i = is.read(buffer)) != -1) {
                            if(control != null && control.isCancel()){
                                conn.disconnect();
                                if(callBack != null) callBack.onComplete(new IllegalArgumentException(HttpControl.task_cancel), null);
                                break;
                            }else{
                                downFileSize = downFileSize + i;
                                progress = (int) (downFileSize * 100.0 / fileSize);
                                fos.write(buffer, 0, i);
    
                                synchronized (this) {
                                    if (downFileSize == fileSize) {
                                        if(callBack != null) callBack.onComplete(null, "");
                                    } else if (tempProgress != progress) {
                                        progress = progress>100?100:progress;
                                        if(callBack != null) callBack.onLoading(progress);
                                        tempProgress = progress;
                                    }
                                }
                            }
                        }
                        fos.flush();
                        fos.close();
                        is.close();
                    } else {
                        if(callBack != null) callBack.onComplete(new IllegalArgumentException("Connection Faild"), null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(callBack != null) callBack.onComplete(e, null);
                }
            }
        });
    }
    
    public boolean syncExeDownloadFile(final String downloadUrl, final File saveFilePath, final HttpControl control,
            final HttpProgressCallBack callBack)
    {
        int fileSize = -1;
        int downFileSize = 0;
        int progress = 0;
        try
        {
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connection_timeout);
            conn.setReadTimeout(read_timeout);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                fileSize = conn.getContentLength();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(saveFilePath);
                byte[] buffer = new byte[1024];
                int i = 0;
                int tempProgress = -1;
                while ((i = is.read(buffer)) != -1)
                {
                    if (control != null && control.isCancel())
                    {
                        conn.disconnect();
                        if (callBack != null)
                            callBack.onComplete(new IllegalArgumentException(HttpControl.task_cancel), null);
                        break;
                    } else
                    {
                        downFileSize = downFileSize + i;
                        progress = (int) (downFileSize * 100.0 / fileSize);
                        fos.write(buffer, 0, i);

                        if (downFileSize == fileSize)
                        {
                            if (callBack != null)
                                callBack.onComplete(null, "");
                        } else if (tempProgress != progress)
                        {
                            progress = progress > 100 ? 100 : progress;
                            if (callBack != null)
                                callBack.onLoading(progress);
                            tempProgress = progress;
                        }
                    }
                }
                fos.flush();
                fos.close();
                is.close();
            } else
            {
                if (callBack != null)
                    callBack.onComplete(new IllegalArgumentException("Connection Faild"), null);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            if (callBack != null)
                callBack.onComplete(e, null);

            return false;
        }

        return true;
    }
}
