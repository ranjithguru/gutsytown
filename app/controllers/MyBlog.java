package controllers;

import java.util.List;


import models.BlogComment;
import models.BlogPost;
import models.User;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class MyBlog extends Controller {
	
	public static void myBlog(Long id) {
		Application.doInitialSetup();
    	BlogPost frontPost;
    	if(id!=null){
    		frontPost =BlogPost.findById(id);
    	}
    	else{
    		frontPost = BlogPost.find("order by postedAt desc").first();
    	}
        List<BlogPost> olderPosts = BlogPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        render(frontPost, olderPosts);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content) {
	    	
		 BlogPost post = BlogPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	myBlog(postId);
	        	 
	        }
	        post.addComment(author, content);
	        myBlog(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	BlogPost frontPost =BlogPost.findById(frontPostId);
    	BlogPost lastPost=BlogPost.findById(lastPostId);
    	List<BlogPost> olderPosts = BlogPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/MyBlog/myBlog.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	BlogPost frontPost =BlogPost.findById(frontPostId);
    	BlogPost lastPost=BlogPost.findById(lastPostId);
    	List<BlogPost> olderPosts = BlogPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/MyBlog/myBlog.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new BlogPost(author, title, content, imageforpost).save();
    	myBlog(null);
    }
    
    public static void getPostImage(long id) {
    	   final BlogPost post = BlogPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final BlogPost post = BlogPost.findById(id);
    	post.delete();
    	myBlog(null);
    }
    public static void deleteComment(long id){
    	final BlogComment comment = BlogComment.findById(id);
    	comment.delete();
    	myBlog(null);
    }
    public static void likeThePost(long id){
    	final BlogPost post = BlogPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
 public static List<BlogPost> getLatestPost(){
	 List<BlogPost> recentPosts =BlogPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<BlogPost> getRecentFivePost(){
	 List<BlogPost> recentPosts =BlogPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
