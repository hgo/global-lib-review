package models;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Feedback extends Model {

    @ManyToOne
    public Library library;

    @ManyToOne
    @JoinColumn(name = "creator_name", referencedColumnName = "name")
    public Suser creator;

    public boolean positive = Boolean.TRUE;

    @Required
    @MinSize(15)
    @MaxSize(256)
    public String feed;

    public long created_at;

    public boolean flagged;

    @OneToMany(cascade = CascadeType.REMOVE)
    public List<FeedbackVote> votes;
    
    @PrePersist
    void prePersist() {
        this.created_at = Calendar.getInstance().getTimeInMillis();
    }

    public long getUpvote() {
        return FeedbackVote.count("feedback = ? and up = ?", this, Boolean.TRUE);
    }

    public long getDownvote() {
        return FeedbackVote.count("feedback = ? and up = ? ", this, Boolean.FALSE);
    }

}
