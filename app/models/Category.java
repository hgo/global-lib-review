package models;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class Category extends Model {

    public String name;
}
