package models;

import javax.persistence.Entity;

@Entity
public class FeedbackVote extends Vote {

    public Feedback feedback;
}
