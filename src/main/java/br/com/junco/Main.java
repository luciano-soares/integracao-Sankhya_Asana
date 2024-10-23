package br.com.junco;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Main {
    public static Properties getProp() throws IOException {
        Properties props = new Properties();
        BufferedReader file = new BufferedReader(new FileReader("data.properties"));
        props.load(file);
        return props;
    }

    private static String firstAndSecondName(String name){
        //#################################################################
        //
        //
        // firstAndSecondName -- Return only the first and second name from a complete name
        //  -Example: Samuel L Jackson, returns Samuel L
        //
        //
        // Parameters
        //
        // name -- The name of the user
        //
        //
        //##################################################################
        StringBuilder newName = new StringBuilder();
        int spaces = 0;
        for(int i = 0; i < name.length(); i++){
            if(name.charAt(i) == ' '){
                spaces++;
            }

            if(spaces == 2){
                return newName.toString();
            }
            else{
                newName.append(name.charAt(i));
            }
        }
        return newName.toString();
    }

    private static String formatSankhyaDateToAsana(String date){
        //#################################################################
        //
        //
        // formatSankhyaDateToNormal -- Convert data format from ddmmyyyy hh:mm to yyyy-mm-ddThh:mm:ss.mmmZ
        //
        //
        // Parameters
        //
        // Date -- Date and time extract from sankhya
        //
        //
        // Obs: Time in Asana follows the Zulu pattern
        //##################################################################
        return date.substring(4, 8) + '-' + date.substring(2, 4) + '-' +  date.substring(0, 2) + 'T' + date.substring(9, 17) + ".000Z";
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
                s.getInstanciasMkt();
                String[][] instanciasSankhya = s.getInstanciasMkt();
                s.logout();
                Asana asana = new Asana(props.getProperty("asana.projectID")
                                        , props.getProperty("asana.token")
                                        , props.getProperty("asana.workspace"));
                JSONArray tarefasAsana = asana.activeTaskInProject();

                //Verifica se as instâncias do flow estão no Asana. Se não estiver, adiciona no asana.
                for(int i = 0; i < s.getQtdInstancias(); i++){
                    System.out.println(instanciasSankhya[i][0]);
                    System.out.println(instanciasSankhya[i][4] + '\n');
                    int instanceIsOnAsana = 0;
                    for(int j = 0; j < tarefasAsana.length(); j++){
                        //Caso a instância esteja no asana, para o loop e passa para a próxima instância
                        if (Objects.equals(tarefasAsana.getJSONObject(j).getString("name"), instanciasSankhya[i][0])){
                            instanceIsOnAsana = 1;
                            break;
                        }
                    }

                    //Caso a instância não esteja no asana, cria uma tarefa no asana.
                    if(instanceIsOnAsana == 0){
                        String section = "";

                        if(Objects.equals(instanciasSankhya[i][4], "Solicitação de desenvolvimento")){
                            section = "1207282158936135";
                        }
                        else if(Objects.equals(instanciasSankhya[i][4], "Confecção de CTP")){
                            section = "1207282158936137";
                        }
                        else if(Objects.equals(instanciasSankhya[i][4], "Solicitação / Correção de embalagem")){
                            section = "1207282158936136";
                        }
                        else if(Objects.equals(instanciasSankhya[i][4], "Solicitação de impressão")){
                            section = "1207282158936108";
                        }
                        else if(Objects.equals(instanciasSankhya[i][4], "Solicitação de material de comunicação")){
                            section = "1207282158936111";
                        }
                        else if(Objects.equals(instanciasSankhya[i][4], "Solicitação de PDV")){
                            section = "1207282158936134";
                        }
                        else if(Objects.equals(instanciasSankhya[i][4], "Solicitação de vídeo")){
                            section = "1207282158936110";
                        }
                        else if(Objects.equals(instanciasSankhya[i][4], "Solicitações de parceiros - MKT")){
                            section = "1207282158936138";
                        }
                        else if(Objects.equals(instanciasSankhya[i][4], "Solicitação de Faca")){
                            section = "1207282158936140";
                        }

                        asana.createTask(instanciasSankhya[i][0],
                                "me",
                                formatSankhyaDateToAsana(instanciasSankhya[i][6]),
                                "<body><h1>" + instanciasSankhya[i][0] + "</h1> " +
                                        "<ul> " +
                                        "<li><b>Solicitante:</b> " + firstAndSecondName(instanciasSankhya[i][3]) + "</li>" +
                                        "<li><b>Dono:</b> "        + firstAndSecondName(instanciasSankhya[i][2]) + "</li>" +
                                        "<li><b>Tarefa:</b> "      + instanciasSankhya[i][5] + "</li>" +
                                        "<li><b>Solicitação:</b> " + instanciasSankhya[i][1] + "</li>" +
                                        "</ul>" +
                                        "</body>",
                                "Integrado com o sankhya",
                                section
                        );
                    }
                }
            }
        }
        catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }
}