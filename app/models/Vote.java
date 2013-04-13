package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Vote extends Model {

    public long commentId;
    public long userId;
    public boolean up;
}
