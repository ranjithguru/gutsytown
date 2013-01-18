package controllers;

import java.util.List;


import models.MoneyComment;
import models.MoneyPost;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class Money extends Controller {
	
	public static void money(Long id) {
		Application.doInitialSetup();
		MoneyPost frontPost;
    	if(id!=null){
    		frontPost =MoneyPost.findById(id);
    	}
    	else{
    		frontPost = MoneyPost.find("order by postedAt desc").first();
    	}
        List<MoneyPost> olderPosts = MoneyPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 MoneyPost post = MoneyPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	money(postId);
	        	 
	        }
	        post.addComment(author, content);
	        money(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	MoneyPost frontPost =MoneyPost.findById(frontPostId);
    	MoneyPost lastPost=MoneyPost.findById(lastPostId);
    	List<MoneyPost> olderPosts = MoneyPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	String randomID = Codec.UUID();
    	renderTemplate("app/views/Money/money.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	MoneyPost frontPost =MoneyPost.findById(frontPostId);
    	MoneyPost lastPost=MoneyPost.findById(lastPostId);
    	List<MoneyPost> olderPosts = MoneyPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	String randomID = Codec.UUID();
    	renderTemplate("app/views/Money/money.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new MoneyPost(author, title, content, imageforpost).save();
    	money(null);
    }
    
    public static void getPostImage(long id) {
    	   final MoneyPost post = MoneyPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final MoneyPost post = MoneyPost.findById(id);
    	post.delete();
    	money(null);
    }
    public static void deleteComment(long id){
    	final MoneyComment comment = MoneyComment.findById(id);
    	comment.delete();
    	money(null);
    }
    public static void likeThePost(long id){
    	final MoneyPost post = MoneyPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    
 public static List<MoneyPost> getLatestPost(){
	 List<MoneyPost> recentPosts =MoneyPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<MoneyPost> getRecentFivePost(){
	 List<MoneyPost> recentPosts =MoneyPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
