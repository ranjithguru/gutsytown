package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
@Entity
public class SimpleComment extends Model  {
	
	public String author;
    public Date postedAt;
     
    @Lob
    public String content;
    public SimpleComment(String author, String content) {
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }
    
}

   