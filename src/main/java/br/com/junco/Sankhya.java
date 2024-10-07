package br.com.junco;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class Sankhya {
    private String usuario;
    private String senha;
    private String token;
    private String appKey;
    private String bearerToken;

    public Sankhya(String usuario, String senha, String token, String appKey){
        this.usuario = usuario;
        this.senha = senha;
        this.token = token;
        this.appKey = appKey;
    }

    public boolean login() throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create("", mediaType);
        Request request = new Request.Builder()
                .url("https://api.sankhya.com.br/login")
                .method("POST", body)
                .addHeader("token", getToken())
                .addHeader("appkey", getAppKey())
                .addHeader("username", getUsuario())
                .addHeader("password", getSenha())
                .build();
        try{
            Response response = client.newCall(request).execute();
            JSONObject responseBody = new JSONObject(response.body().string());
            System.out.println(responseBody);
            System.out.println(responseBody.getString("error"));

            if (Objects.equals(responseBody.getString("error"), "null")){
                setBearerToken(responseBody.getString("bearerToken"));
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
    }

    public void logout() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url("https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=MobileLoginSP.logout&outputType=json")
                .addHeader("appkey", getAppKey())
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + getBearerToken())
                .build();
        Response response = client.newCall(request).execute();
    }

    private String getUsuario() {
        return usuario;
    }
    private void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    private String getSenha() {
        return senha;
    }
    private void setSenha(String senha) {
        this.senha = senha;
    }

    private String getToken() {
        return token;
    }
    private void setToken(String token) {
        this.token = token;
    }

    private String getAppKey() {
        return appKey;
    }
    private void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    private void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    private String getBearerToken() {
        return bearerToken;
    }
}
