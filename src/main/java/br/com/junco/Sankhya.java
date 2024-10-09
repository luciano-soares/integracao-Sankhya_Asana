package br.com.junco;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Sankhya {
    private String usuario;
    private String senha;
    private String token;
    private String appKey;
    private String bearerToken;
    private int qtdInstancias;

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

    public String[][] getInstanciasMkt() throws IOException, JSONException {
        String sql = "SELECT tar.idinstprn || ' - ' || lower(apelido) AS titulo  " +
                "    , LOWER(apelido) as apelido " +
                "    , NVL(INITCAP(NVL(parDono.nomeparc, usuDono.nomeusu)), 'Fila') AS executor  " +
                "    , INITCAP(NVL(parSolic.nomeparc, usuSolic.nomeusu)) AS solicitante   " +
                "    , 'Solicitação de desenvolvimento' AS nome_processo  " +
                "    , ele.nome AS tarefa  " +
                "    , FUN_CALC_END_DATE_FLOW(tar.dhcriacao, pro.sla) + 0.125 AS prazo_de_entrega   " +
                "FROM twfitar tar   " +
                "INNER JOIN ad_pdesenv dev      ON dev.idinstprn    = tar.idinstprn   " +
                "LEFT  JOIN tsiusu     usuDono  ON usuDono.codusu   = tar.codusudono   " +
                "LEFT  JOIN tgfpar     parDono  ON parDono.codparc  = usuDono.codparc  " +
                "INNER JOIN tsiusu     usuSolic ON usuSolic.codusu  = tar.codususolicitante   " +
                "LEFT  JOIN tgfpar     parSolic ON parSolic.codparc = usuSolic.codparc   " +
                "INNER JOIN twfele     ele      ON ele.idelemento   = tar.idelemento   " +
                "INNER JOIN ad_slaproc pro      ON PRO.NOME         = ele.idelemento   " +
                "WHERE TAR.DHCONCLUSAO IS NULL   " +
                "AND pro.codcencus = 21040100   " +
                "AND ele.versao    = (SELECT MAX(versao) FROM twfele WHERE idelemento = ele.idelemento)  " +
                " " +
                "UNION " +
                " " +
                "SELECT tar.idinstprn || ' - ' || lower(gra.faca) AS titulo  " +
                "    , LOWER(gra.faca) as apelido " +
                "    , NVL(INITCAP(NVL(parDono.nomeparc, usuDono.nomeusu)), 'Fila') AS executor  " +
                "    , INITCAP(NVL(parSolic.nomeparc, usuSolic.nomeusu)) AS solicitante   " +
                "    , 'Confecção de CTP' AS nome_processo  " +
                "    , ele.nome AS tarefa  " +
                "    , FUN_CALC_END_DATE_FLOW(tar.dhcriacao, pro.sla) + 0.125 AS prazo_de_entrega   " +
                "FROM twfitar tar   " +
                "INNER JOIN ad_ppgrafica gra      ON gra.idinstprn    = tar.idinstprn   " +
                "LEFT  JOIN tsiusu       usuDono  ON usuDono.codusu   = tar.codusudono   " +
                "LEFT  JOIN tgfpar       parDono  ON parDono.codparc  = usuDono.codparc  " +
                "INNER JOIN tsiusu       usuSolic ON usuSolic.codusu  = tar.codususolicitante   " +
                "LEFT  JOIN tgfpar       parSolic ON parSolic.codparc = usuSolic.codparc   " +
                "INNER JOIN twfele       ele      ON ele.idelemento   = tar.idelemento   " +
                "INNER JOIN ad_slaproc   pro      ON PRO.NOME         = ele.idelemento   " +
                "WHERE TAR.DHCONCLUSAO IS NULL   " +
                "AND pro.codcencus = 21040100   " +
                "AND ele.versao    = (SELECT MAX(versao) FROM twfele WHERE idelemento = ele.idelemento)  ";
        System.out.println(sql);

        String JSONbody = "{" +
                "    \"serviceName\":\"DbExplorerSP.executeQuery\"," +
                "    \"requestBody\": {" +
                "        \"sql\":\"" + sql + "\"" +
                "    }" +
                "}";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(10, TimeUnit.MINUTES)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSONbody, mediaType);
        Request request = new Request.Builder()
                //HTTPS não funciona na api sankhya, ou no serviço interno da junco
                .url("https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=DbExplorerSP.executeQuery&outputType=json&mgeSession=" + getToken())
                .method("POST", body)
                .addHeader("appkey", getAppKey())
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + getBearerToken())
                .build();
        Response response = client.newCall(request).execute();
        JSONObject responseBody = new JSONObject(response.body().string());
        String[][] instancias = new String[500][7];
        responseBody.getJSONObject("responseBody").getJSONArray("rows");
        setQtdInstancias(responseBody.getJSONObject("responseBody").getJSONArray("rows").length());

        System.out.println(responseBody.getJSONObject("responseBody").getJSONArray("rows"));
        for(int i = 0; i < responseBody.getJSONObject("responseBody").getJSONArray("rows").length(); i++){
            instancias[i][0] = responseBody.getJSONObject("responseBody").getJSONArray("rows").getJSONArray(i).getString(0);
            instancias[i][1] = responseBody.getJSONObject("responseBody").getJSONArray("rows").getJSONArray(i).getString(1);
            instancias[i][2] = responseBody.getJSONObject("responseBody").getJSONArray("rows").getJSONArray(i).getString(2);
            instancias[i][3] = responseBody.getJSONObject("responseBody").getJSONArray("rows").getJSONArray(i).getString(3);
            instancias[i][4] = responseBody.getJSONObject("responseBody").getJSONArray("rows").getJSONArray(i).getString(4);
            instancias[i][5] = responseBody.getJSONObject("responseBody").getJSONArray("rows").getJSONArray(i).getString(5);
            instancias[i][6] = responseBody.getJSONObject("responseBody").getJSONArray("rows").getJSONArray(i).getString(6);
        }
        return instancias;
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

    private void setQtdInstancias(int qtdInstancias) {
        this.qtdInstancias = qtdInstancias;
    }

    public int getQtdInstancias() {
        return qtdInstancias;
    }
}
