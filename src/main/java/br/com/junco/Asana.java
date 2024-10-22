package br.com.junco;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class Asana {
    private String project_ID;
    private String token;
    private String workspace;

    public Asana(String project_ID, String token) {
        this.project_ID = project_ID;
        this.token = token;
    }

    public void createTask(String titulo, String assignee, String dueDate,String descrition, String notes, String section){
        try{
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            String bodyString = "{" +
                    "\"data\": {" +
                    "\"workspace\": \"" + workspace + "\"," +
                    "\"name\": \"" + titulo + "\"," +
                    "\"assignee\":  \"" + assignee + "\"," +
                    "\"due_at\": \"" + dueDate + "\"," +
                    "\"html_notes\": \"" + descrition + "\"," +
                    "\"notes\": \"" + notes + "\"," +
                    "\"assignee_section\": \"" + section + "\"," +
                    "\"projects\": " +
                    "[\"1207282158936107\"" +
                    "]" +
                    "}" +
                    "}";
            System.out.println(bodyString);
            RequestBody body    = RequestBody.create(bodyString, mediaType);
            System.out.println(bodyString);
            Request request = new Request.Builder()
                    .url("https://app.asana.com/api/1.0/tasks")
                    .method("POST", body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    private void setToken(String token) {
        this.token = token;
    }
    private String getToken() {
        return token;
    }

    private void setWorkspace(String workspace) {
        this.workspace = workspace;
    }
    private String getWorkspace() {
        return workspace;
    }
}
