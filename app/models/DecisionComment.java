package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class DecisionComment extends Model {
 
    public String author;
    public Date postedAt;
     
    @Lob
    public String content;
    
    @ManyToOne
    public DecisionPost post;
    
    public int likes=0;
    
    public DecisionComment(DecisionPost post, String author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }
    public void addLike(){
    	this.likes=this.likes+1;
    	this.save();
    }
 
}