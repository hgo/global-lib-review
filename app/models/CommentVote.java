package models;

import javax.persistence.Entity;

@Entity
public class CommentVote extends Vote {

    public Comment comment;
}
