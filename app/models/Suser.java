package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Suser extends Model {

    public String name;
    public MetaData metaData;
}
