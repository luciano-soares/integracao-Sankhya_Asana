package br.com.junco;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static Properties getProp() throws IOException {
        Properties props = new Properties();
        BufferedReader file = new BufferedReader(new FileReader("data.properties"));
        props.load(file);
        return props;
    }

    public static void main(String[] args) throws IOException {
        Properties props = getProp();
        Sankhya s = new Sankhya(props.getProperty("sankhya.id.username"),
                props.getProperty("sankhya.id.password"),
                props.getProperty("sankhya.token"),
                props.getProperty("sankhya.appkey")
        );
        try {
            if (s.login()){
                System.out.println("Oi");
                s.logout();
            }
        }
        catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }
}