package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;;

@Entity
public class Comment extends Model {
    
    public String markup;
    public long userId;
    public int upvote; 
    public int downvote; 
    public long created_at;
//    @ManyToOne(optional=true)
//    public Version version;
    @ManyToOne(optional=true)
    public Library library;
}
