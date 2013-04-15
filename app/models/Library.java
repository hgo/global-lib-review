package models;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.Model;

@Entity
public class Library extends Model {

    @Required
    @MinSize(5)
    @MaxSize(75)
    public String name;
    
    @Required
    @URL
    public String link;
    
    @MaxSize(2048)
    @Column(columnDefinition="TEXT")
    public String descMarkdown;
    
    @Required
    @MaxSize(255)
    @MinSize(1)
    public String language;
    
    @OneToMany(mappedBy="library")
    public List<Comment> comments;
    
    @ManyToMany
    public List<Category> categories;
    
    @OneToMany(mappedBy="library")
    public List<Feedback> feedbacks;
    
    public boolean active;

    public long last_updated;
    
    @PreUpdate
    void preUpdate(){
        last_updated = Calendar.getInstance().getTimeInMillis();
    }
    
    public static Library findByName(String name) {
        return Library.find("byName", name).first();
    }
    
    @Override
    public String toString() {
        return name;
    }
}
