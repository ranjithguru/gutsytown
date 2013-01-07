package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class FoodComment extends Model{
	public String author;
    public Date postedAt;
     
    @Lob
    public String content;
    
    @ManyToOne
    public FoodPost post;
    
    public FoodComment(FoodPost post, String author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }
}
