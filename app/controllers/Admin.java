package controllers;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import models.Library;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.libs.IO;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.Scope.Params;
import play.mvc.Scope.RenderArgs;
import services.AdminService;
import services.GithubService;
import services.ServiceException;

public class Admin extends Controller {

    final static String adminCacheKey = "admin_logged:";
    @Inject
    private static AdminService adminService;
    @Inject
    private static GithubService githubService;
    
    
    @Before(unless={"index","authenticate"})
    static void before(){
        if(Cache.get(adminCacheKey + session.getId()) == null){
            if(request.isAjax()){
                forbidden("timeout");
            }
            index();
        }
        
    }
    
    public static void index(){
        if(Cache.get(adminCacheKey+session.getId())!= null){
            home();
        }
        render();
    }
    
    public static void authenticate(String name, String password){
        if("guven".equals(name) && "12345".equals(password)){
            Cache.set(adminCacheKey+session.getId(), "yes","24h");
            home();
        }else{
            flash.error("Invalid attempt");
            params.flash();
            flash.keep();
            index();
        }
    }
    
    public static void home(){
        renderArgs.put("tab","home");
        render();
    }
    public static void libraries(){
        renderArgs.put("tab","libraries");
        renderArgs.put("libraries", Library.all().fetch());
        //List<Library> libraries = Library.all().fetch();
        //renderArgs.put("libraries",libraries);
        render();
    }
    public static void actions(){
        renderArgs.put("tab","actions");
        render();
    }
    
    public static void github(){
        if(githubService.isCodeResponse()){
            String accessToken = githubService.getAccessToken();
            Cache.set("github:access_token",accessToken,"24h");
            renderHtml("<html><body>Access token successfully retrieved</body></html>");
        }else{
            githubService.retrieveVerificationCode();
        }
    }
    
    public static void exportYMLGithub(String keyword) {
        String accessToken = (String) Cache.get("github:access_token");
        if (accessToken == null) {
            renderHtml("<html><body><a href=\"/admin/github\">click to get access to github</a></body></html>");
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= 100; i++) {
                JsonElement responseJSON = githubService.searchRepositories(keyword, accessToken, i);
                if (responseJSON.getAsJsonObject().get("message") == null) {
                    githubService.appendYmlRepresentationOfJson(responseJSON, builder);
                } else {
                    Logger.info(responseJSON.toString());
                    break;
                }
            }
            File f = new File(Play.tmpDir.getPath() + File.separator + "github-" + keyword + "-" + Calendar.getInstance().getTimeInMillis() + ".yml");
            IO.writeContent(builder.toString(), f);

            renderBinary(f);
        }
    }
    
    public static void importLibraries(@Required File file){
        checkAuthenticity();
        if(validation.hasErrors()){
            validation.keep();
            libraries();
        }
        Logger.info(Params.current().allSimple().toString());
        adminService.importFromFile(file);
        Logger.info(Params.current().allSimple().toString());
        flash.success("%s successfully imported",file.getName());
        flash.keep();
        libraries();
    }
    
    @Catch(value=ServiceException.class)
    static void catchExc(ServiceException e){
        Logger.error(ExceptionUtils.getStackTrace(e.e));
        error(e.e);
    }
    
}
