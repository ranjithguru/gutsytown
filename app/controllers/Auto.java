package controllers;

import java.util.List;

import models.Comment;
import models.AutoComment;
import models.AutoPost;
import models.Post;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class Auto extends Controller {
	
	public static void auto(Long id) {
		Application.doInitialSetup();
    	AutoPost frontPost;
    	if(id!=null){
    		frontPost =AutoPost.findById(id);
    	}
    	else{
    		frontPost = AutoPost.find("order by postedAt desc").first();
    	}
        List<AutoPost> olderPosts = AutoPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 AutoPost post = AutoPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	auto(postId);
	        	 
	        }
	        post.addComment(author, content);
	        auto(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	AutoPost frontPost =AutoPost.findById(frontPostId);
    	AutoPost lastPost=AutoPost.findById(lastPostId);
    	List<AutoPost> olderPosts = AutoPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Auto/auto.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	AutoPost frontPost =AutoPost.findById(frontPostId);
    	AutoPost lastPost=AutoPost.findById(lastPostId);
    	List<AutoPost> olderPosts = AutoPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Auto/auto.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new AutoPost(author, title, content, imageforpost).save();
    	auto(null);
    }
    
    public static void getPostImage(long id) {
    	   final AutoPost post = AutoPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final AutoPost post = AutoPost.findById(id);
    	post.delete();
    	auto(null);
    }
    public static void deleteComment(long id){
    	final AutoComment comment = AutoComment.findById(id);
    	comment.delete();
    	auto(null);
    }
    public static void likeThePost(long id){
    	final AutoPost post = AutoPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    
 public static List<AutoPost> getLatestPost(){
	 List<AutoPost> recentPosts =AutoPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<AutoPost> getRecentFivePost(){
	 List<AutoPost> recentPosts =AutoPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
