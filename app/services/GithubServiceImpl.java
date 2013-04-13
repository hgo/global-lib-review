package services;

import java.util.HashMap;
import java.util.Map;

import play.libs.OAuth2;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Http.Request;
import play.mvc.Scope.Params;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

class GithubServiceImpl implements GithubService {
    static final OAuth2 AUTH =  new OAuth2(
            "https://github.com/login/oauth/authorize", 
            "https://github.com/login/oauth/access_token", 
            "61f969fde9390b74842b", 
            "326b6dddf8244c1c003c0cc5a8d13b59f6d3a890");
    static final String REPO_YML_FORMAT= 
            "- name : %s\n" +
            "  description : \"%s\"\n" +
            "  link : %s\n" +
            "  active : %s\n" +
            "  language : %s\n";
    
    public boolean isCodeResponse() {
        return AUTH.isCodeResponse();
    }

    @Override
    public void retrieveVerificationCode() {
        AUTH.retrieveVerificationCode();
    }
    public String getAccessToken(){
        String accessCode = Params.current().get("code");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("client_id", AUTH.clientid);
        params.put("client_secret", AUTH.secret);
        params.put("redirect_uri", Request.current().getBase() + Request.current().url);
        params.put("code", accessCode);
        WSRequest request = WS.url(AUTH.accessTokenURL).params(params);
        request.headers.put("Accept", "application/json");
        HttpResponse response = request.get();
        return response.getJson().getAsJsonObject().get("access_token").getAsString();
    }

    @Override
    public JsonElement searchRepositories(String keyword, String accessToken,int page) {
        WSRequest request = WS.url("https://api.github.com/legacy/repos/search/%s?access_token=%s&sort=%s&order=%s&start_page=%s",
                keyword,accessToken,"stars","desc",page+"");
        return request.get().getJson();
    }

    @Override
    public void appendYmlRepresentationOfJson(JsonElement responseJSON, StringBuilder builder) {
        JsonArray repos = responseJSON.getAsJsonObject().get("repositories").getAsJsonArray();
        for (int i = 0; i < repos.size(); i++) {
            String name = repos.get(i).getAsJsonObject().get("name").getAsString();
            String description = repos.get(i).getAsJsonObject().get("description").getAsString().replaceAll("\"", "\\\\\"");
            String link = repos.get(i).getAsJsonObject().get("url").getAsString();
            String active = "1";
            String language = repos.get(i).getAsJsonObject().get("language").getAsString().toLowerCase();
            builder.append(String.format(REPO_YML_FORMAT, name,description,link,active,language));
        }
    }
}
