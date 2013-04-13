package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Library extends Model {

    public String name;
    public String link;
    @Column(columnDefinition="TEXT")
    public String description;
    public String language;
//    @OneToMany(mappedBy="library",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
//    public List<Version> versions;
    @OneToMany(mappedBy="library")
    public List<Comment> comments;
    @OneToMany
    public List<Category> categories;
    public boolean active;
    @OneToMany(mappedBy="library")
    public List<Suggestion> suggestions;
}
