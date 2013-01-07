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
public class TechPost  extends Model{
	public String title;
    public Date postedAt;
    
    @Lob
    public String content;

    public Blob postImage;
    
    @ManyToOne
    public User author;
    
    public int likes=0;
   
    @OneToMany(mappedBy="post", cascade=CascadeType.ALL)
    public List<TechComment> comments;
    
    public TechPost(User author, String title, String content,Blob postImage) {
    	this.comments = new ArrayList<TechComment>();
        this.author = author;
        this.title = title;
        this.content = content;
        this.postImage=postImage;
        this.postedAt = new Date();
    }
    public TechPost addComment(String author, String content) {
    	TechComment newComment = new TechComment(this, author, content).save();
        this.comments.add(newComment);
        this.save();
        return this;
    }
    public TechPost previous() {
        return TechPost.find("postedAt < ? order by postedAt desc", postedAt).first();
    }
     
    public TechPost next() {
        return TechPost.find("postedAt > ? order by postedAt asc", postedAt).first();
    }
    
    public void addLike(){
    	this.likes=this.likes+1;
    	this.save();
    }
}
