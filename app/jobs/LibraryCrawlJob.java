package jobs;

import java.io.IOException;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;

import models.Library;
import play.jobs.Job;
import play.libs.IO;
import play.libs.WS;
import play.libs.WS.WSRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LibraryCrawlJob extends Job {

    Long libId;
    
    public LibraryCrawlJob(Long libId) {
        this.libId = libId;
    }


    @Override
    public void doJob() throws Exception {
        setStackoverflowRating();
        setGithubRating();
    }

    private void setGithubRating() {
        //TODO:
        //Library library = Library.findById(libId);
    }

    JsonParser parser = new JsonParser();
    private void setStackoverflowRating() throws IOException, InterruptedException {
        int page = 0;
        int total = Integer.MAX_VALUE;
        Library library = Library.findById(libId);
        while(page < 1 && 100 * page <= total){
            Thread.sleep(1000);
            String url = String.format("http://api.stackoverflow.com/1.1/search?intitle=%s&page=%s&pagesize=100&sort=votes", library.name,page+1+"");
            WSRequest request = WS.url(url);
            request.headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
            GZIPInputStream gzipInputStream = new GZIPInputStream(request.get().getStream());
            JsonObject json = parser.parse(IO.readContentAsString(gzipInputStream)).getAsJsonObject();
            gzipInputStream.close();
            total = json.get("total").getAsInt();
            JsonArray questions = json.get("questions").getAsJsonArray();
            for(int i = 0 ; i < questions.size(); i++){
                JsonObject q = questions.get(i).getAsJsonObject();
                int fav_count = q.get("favorite_count").getAsInt();
                int score = q.get("score").getAsInt();
                int view_count = q.get("view_count").getAsInt();
                JsonArray jsonTags = q.get("tags").getAsJsonArray();
                for (int j = 0; j < jsonTags.size(); j++) {
                    int so_question_score = calculateStackoverflowQuestionScore(fav_count,view_count,score);
                    String tag = String.valueOf(jsonTags.get(j).getAsString());
                    Integer currentTagValue = library.so_tagMap.get(tag);
                    library.so_tagMap.put(tag, currentTagValue == null ? so_question_score : currentTagValue + so_question_score);
                }
            }
            library.so_last_updated = Calendar.getInstance().getTimeInMillis();
            library.so_rating = total;//TODO:
            library.save();
            page++;
        }
    }


    private int calculateStackoverflowQuestionScore(int fav_count, int view_count, int score) {
        return fav_count + score + (int)Math.floor(Math.pow(view_count, 0.3d));
    }
}
