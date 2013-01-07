package controllers;

import java.util.List;


import models.TechComment;
import models.TechPost;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class Tech extends Controller {
	
	public static void tech(Long id) {
		Application.doInitialSetup();
		TechPost frontPost;
    	if(id!=null){
    		frontPost =TechPost.findById(id);
    	}
    	else{
    		frontPost = TechPost.find("order by postedAt desc").first();
    	}
        List<TechPost> olderPosts = TechPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 TechPost post = TechPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	tech(postId);
	        	 
	        }
	        post.addComment(author, content);
	        tech(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	TechPost frontPost =TechPost.findById(frontPostId);
    	TechPost lastPost=TechPost.findById(lastPostId);
    	List<TechPost> olderPosts = TechPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Tech/tech.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	TechPost frontPost =TechPost.findById(frontPostId);
    	TechPost lastPost=TechPost.findById(lastPostId);
    	List<TechPost> olderPosts = TechPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Tech/tech.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new TechPost(author, title, content, imageforpost).save();
    	tech(null);
    }
    
    public static void getPostImage(long id) {
    	   final TechPost post = TechPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final TechPost post = TechPost.findById(id);
    	post.delete();
    	tech(null);
    }
    public static void deleteComment(long id){
    	final TechComment comment = TechComment.findById(id);
    	comment.delete();
    	tech(null);
    }
    public static void likeThePost(long id){
    	final TechPost post = TechPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    
 public static List<TechPost> getLatestPost(){
	 List<TechPost> recentPosts =TechPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<TechPost> getRecentFivePost(){
	 List<TechPost> recentPosts =TechPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
