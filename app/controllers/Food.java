package controllers;

import java.util.List;


import models.FoodComment;
import models.FoodPost;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class Food extends Controller  {
	
	public static void food(Long id) {
		Application.doInitialSetup();
		FoodPost frontPost;
    	if(id!=null){
    		frontPost =FoodPost.findById(id);
    	}
    	else{
    		frontPost = FoodPost.find("order by postedAt desc").first();
    	}
        List<FoodPost> olderPosts = FoodPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 FoodPost post = FoodPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	food(postId);
	        	 
	        }
	        post.addComment(author, content);
	        food(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	FoodPost frontPost =FoodPost.findById(frontPostId);
    	FoodPost lastPost=FoodPost.findById(lastPostId);
    	List<FoodPost> olderPosts = FoodPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	  String randomID = Codec.UUID();
    	renderTemplate("app/views/Food/food.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	FoodPost frontPost =FoodPost.findById(frontPostId);
    	FoodPost lastPost=FoodPost.findById(lastPostId);
    	List<FoodPost> olderPosts = FoodPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	  String randomID = Codec.UUID();
    	renderTemplate("app/views/Food/food.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new FoodPost(author, title, content, imageforpost).save();
    	food(null);
    }
    
    public static void getPostImage(long id) {
    	   final FoodPost post = FoodPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final FoodPost post =FoodPost.findById(id);
    	post.delete();
    	food(null);
    }
    public static void deleteComment(long id){
    	final FoodComment comment = FoodComment.findById(id);
    	comment.delete();
    	food(null);
    }
    public static void likeThePost(long id){
    	final FoodPost post = FoodPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    
 public static List<FoodPost> getLatestPost(){
	 List<FoodPost> recentPosts =FoodPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<FoodPost> getRecentFivePost(){
	 List<FoodPost> recentPosts =FoodPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
