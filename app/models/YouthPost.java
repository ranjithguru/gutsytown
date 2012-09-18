package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Blob;
import play.db.jpa.Model;
@Entity
public class YouthPost extends Model{
	   public String title;
	    public Date postedAt;
	    
	    @Lob
	    public String content;

	    public Blob postImage;
	    
	    @ManyToOne
	    public User author;
	    
	    public int likes=0;
	   
	    @OneToMany(mappedBy="post", cascade=CascadeType.ALL)
	    public List<YouthComment> comments;
	    
	    public YouthPost(User author, String title, String content,Blob postImage) {
	    	this.comments = new ArrayList<YouthComment>();
	        this.author = author;
	        this.title = title;
	        this.content = content;
	        this.postImage=postImage;
	        this.postedAt = new Date();
	    }
	    public YouthPost addComment(String author, String content) {
	    	YouthComment newComment = new YouthComment(this, author, content).save();
	        this.comments.add(newComment);
	        this.save();
	        return this;
	    }
	    public YouthPost previous() {
	        return YouthPost.find("postedAt < ? order by postedAt desc", postedAt).first();
	    }
	     
	    public YouthPost next() {
	        return YouthPost.find("postedAt > ? order by postedAt asc", postedAt).first();
	    }
	    
	    public void addLike(){
	    	this.likes=this.likes+1;
	    	this.save();
	    }
}
