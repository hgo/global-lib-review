package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import org.apache.commons.lang.time.DateUtils;

import jobs.LibraryCrawlJob;

import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }
    public static void lib(Long id) throws Exception {
        Library l = Library.findById(id);
        long now = Calendar.getInstance().getTimeInMillis();
        if(l.so_last_updated + (DateUtils.MILLIS_PER_DAY * 7) < now){
            new LibraryCrawlJob(l.id).doJob();
            l = l.refresh();
        }
        render(l);
    }

}