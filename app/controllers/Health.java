package controllers;

import java.util.List;


import models.HealthComment;
import models.HealthPost;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class Health extends Controller{
	
	public static void health(Long id) {
		Application.doInitialSetup();
		HealthPost frontPost;
    	if(id!=null){
    		frontPost =HealthPost.findById(id);
    	}
    	else{
    		frontPost = HealthPost.find("order by postedAt desc").first();
    	}
        List<HealthPost> olderPosts = HealthPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 HealthPost post = HealthPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	health(postId);
	        	 
	        }
	        post.addComment(author, content);
	        health(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	HealthPost frontPost =HealthPost.findById(frontPostId);
    	HealthPost lastPost=HealthPost.findById(lastPostId);
    	List<HealthPost> olderPosts = HealthPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Health/health.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	HealthPost frontPost =HealthPost.findById(frontPostId);
    	HealthPost lastPost=HealthPost.findById(lastPostId);
    	List<HealthPost> olderPosts = HealthPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Health/health.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new HealthPost(author, title, content, imageforpost).save();
    	health(null);
    }
    
    public static void getPostImage(long id) {
    	   final HealthPost post = HealthPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final HealthPost post = HealthPost.findById(id);
    	post.delete();
    	health(null);
    }
    public static void deleteComment(long id){
    	final HealthComment comment = HealthComment.findById(id);
    	comment.delete();
    	health(null);
    }
    public static void likeThePost(long id){
    	final HealthPost post = HealthPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    
 public static List<HealthPost> getLatestPost(){
	 List<HealthPost> recentPosts =HealthPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<HealthPost> getRecentFivePost(){
	 List<HealthPost> recentPosts =HealthPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
