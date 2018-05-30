package indi.ruiyangding.smmstool;

import javafx.concurrent.Task;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import java.lang.reflect.Field;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import org.apache.http.util.EntityUtils;


public class UploadSmmsAPI implements SmmsAPI {

    public static String url = "https://sm.ms/api/upload";
    private String path;
    private ImageInfo responseInfo;

    UploadSmmsAPI(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public boolean sendRequest() {

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            //HttpPost req = new HttpPost(url);
            FileBody bin = new FileBody(new File(path));
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("smfile", bin)
                    .build();
            HttpPost request = new HttpPost(url);
            //request.addHeader("content-type", "application/json");
            request.setEntity(reqEntity);
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            com.google.gson.Gson gson = new com.google.gson.Gson();
            responseInfo = gson.fromJson(json, ImageInfo.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public ImageInfo getResponseInfo() {
        return responseInfo;
    }
}


