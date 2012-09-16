package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;

import play.db.jpa.Model;

@Entity
public class ContactComment extends Model  {
	
	public String author;
    public Date postedAt;
     
    @Lob
    public String content;
    
    public ContactComment(String author, String content) {
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }
    
}