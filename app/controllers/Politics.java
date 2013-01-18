package controllers;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import models.Comment;
import models.Post;
import models.User;
import models.PoliticsComment;
import models.PoliticsPost;

import com.google.gson.JsonObject;

import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.libs.Codec;
import play.mvc.Controller;

public class Politics extends Controller{

	public static void politics(Long id) {
		Application.doInitialSetup();
    	PoliticsPost frontPost;
    	if(id!=null){
    		frontPost =PoliticsPost.findById(id);
    	}
    	else{
    		frontPost = PoliticsPost.find("order by postedAt desc").first();
    	}
        List<PoliticsPost> olderPosts = PoliticsPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
	    	
		 PoliticsPost post = PoliticsPost.findById(postId);
		 validation.equals(
			        code, Cache.get(randomID));
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	politics(postId);
	        	 
	        }
	        post.addComment(author, content);
	        politics(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	PoliticsPost frontPost =PoliticsPost.findById(frontPostId);
    	PoliticsPost lastPost=PoliticsPost.findById(lastPostId);
    	List<PoliticsPost> olderPosts = PoliticsPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	String randomID = Codec.UUID();
    	renderTemplate("app/views/Politics/politics.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	PoliticsPost frontPost =PoliticsPost.findById(frontPostId);
    	PoliticsPost lastPost=PoliticsPost.findById(lastPostId);
    	List<PoliticsPost> olderPosts = PoliticsPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	String randomID = Codec.UUID();
    	renderTemplate("app/views/Politics/politics.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new PoliticsPost(author, title, content, imageforpost).save();
    	politics(null);
    }
    
    public static void getPostImage(long id) {
    	   final PoliticsPost post = PoliticsPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final PoliticsPost post = PoliticsPost.findById(id);
    	post.delete();
    	politics(null);
    }
    public static void deleteComment(long id){
    	final PoliticsComment comment = PoliticsComment.findById(id);
    	comment.delete();
    	politics(null);
    }
    public static void likeThePost(long id){
    	final PoliticsPost post = PoliticsPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
 public static List<PoliticsPost> getLatestPost(){
	 List<PoliticsPost> recentPosts =PoliticsPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 
 public static List<PoliticsPost> getRecentFivePost(){
	 List<PoliticsPost> recentPosts =PoliticsPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
  
}
