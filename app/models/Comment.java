package models;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

;

@Entity
public class Comment extends Model {

    @Required
    @MinSize(15)
    @MaxSize(2048)
    @Column(columnDefinition = "TEXT", length = 2048)
    public String markdown;

    @ManyToOne
    @JoinColumn(name = "user_name", referencedColumnName = "name")
    public Suser user;

    public long created_at;

    public boolean flagged = false;

    @ManyToOne
    public Library library;

    @OneToMany(cascade = CascadeType.REMOVE)
    public List<CommentVote> votes;

    @PrePersist
    void prePersist() {
        this.created_at = Calendar.getInstance().getTimeInMillis();
    }

    public long getUpvote() {
        return CommentVote.count("comment = ? and up = ?", this, Boolean.TRUE);
    }

    public long getDownvote() {
        return CommentVote.count("comment = ? and up = ? ", this, Boolean.FALSE);
    }

}
