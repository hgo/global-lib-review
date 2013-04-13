package services;

import com.google.gson.JsonElement;

import play.libs.OAuth.ServiceInfo;
import play.libs.OAuth2;

public interface GithubService {
    public boolean isCodeResponse();
    public void retrieveVerificationCode();
    public String getAccessToken();
    public JsonElement searchRepositories(String keyword, String accessToken,int page);
    public void appendYmlRepresentationOfJson(JsonElement responseJSON,StringBuilder builder);
}
