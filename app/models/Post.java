
	package models;
	 
	import java.awt.Image;
import java.util.*;

	import javax.persistence.*;
	 
import play.db.jpa.*;
	 
	@Entity
	public class Post extends Model {
	 
	    public String title;
	    public Date postedAt;
	    
	    @Lob
	    public String content;

	    public Blob postImage;
	    
	    @ManyToOne
	    public User author;
	    
	    public int likes=0;
	   
	    @OneToMany(mappedBy="post", cascade=CascadeType.ALL)
	    public List<Comment> comments;
	    
	    public Post(User author, String title, String content,Blob postImage) {
	    	this.comments = new ArrayList<Comment>();
	        this.author = author;
	        this.title = title;
	        this.content = content;
	        this.postImage=postImage;
	        this.postedAt = new Date();
	    }
	    public Post addComment(String author, String content) {
	        Comment newComment = new Comment(this, author, content).save();
	        this.comments.add(newComment);
	        this.save();
	        return this;
	    }
	    public Post previous() {
	        return Post.find("postedAt < ? order by postedAt desc", postedAt).first();
	    }
	     
	    public Post next() {
	        return Post.find("postedAt > ? order by postedAt asc", postedAt).first();
	    }
	    
	    public void addLike(){
	    	this.likes=this.likes+1;
	    	this.save();
	    }
	}

