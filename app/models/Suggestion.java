package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Suggestion extends Model {

    @ManyToOne
    public Library library;
    @ManyToOne
    public Suser user;
    public boolean positive;
    public String reference;
}
