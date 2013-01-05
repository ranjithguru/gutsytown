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
public class AutoPost extends Model {
 
    public String title;
    public Date postedAt;
    
    @Lob
    public String content;

    public Blob postImage;
    
    @ManyToOne
    public User author;
    
    public int likes=0;
   
    @OneToMany(mappedBy="post", cascade=CascadeType.ALL)
    public List<AutoComment> comments;
    
    public AutoPost(User author, String title, String content,Blob postImage) {
    	this.comments = new ArrayList<AutoComment>();
        this.author = author;
        this.title = title;
        this.content = content;
        this.postImage=postImage;
        this.postedAt = new Date();
    }
    public AutoPost addComment(String author, String content) {
        AutoComment newComment = new AutoComment(this, author, content).save();
        this.comments.add(newComment);
        this.save();
        return this;
    }
    public AutoPost previous() {
        return AutoPost.find("postedAt < ? order by postedAt desc", postedAt).first();
    }
     
    public AutoPost next() {
        return AutoPost.find("postedAt > ? order by postedAt asc", postedAt).first();
    }
    
    public void addLike(){
    	this.likes=this.likes+1;
    	this.save();
    }
}

