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
