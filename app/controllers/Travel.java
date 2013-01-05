package controllers;

import java.util.List;

import models.Comment;
import models.TravelComment;
import models.TravelPost;
import models.Post;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class Travel extends Controller {
	
	public static void travel(Long id) {
		Application.doInitialSetup();
    	TravelPost frontPost;
    	if(id!=null){
    		frontPost =TravelPost.findById(id);
    	}
    	else{
    		frontPost = TravelPost.find("order by postedAt desc").first();
    	}
        List<TravelPost> olderPosts = TravelPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 TravelPost post = TravelPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	travel(postId);
	        	 
	        }
	        post.addComment(author, content);
	        travel(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	TravelPost frontPost =TravelPost.findById(frontPostId);
    	TravelPost lastPost=TravelPost.findById(lastPostId);
    	List<TravelPost> olderPosts = TravelPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Travel/travel.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	TravelPost frontPost =TravelPost.findById(frontPostId);
    	TravelPost lastPost=TravelPost.findById(lastPostId);
    	List<TravelPost> olderPosts = TravelPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Travel/travel.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new TravelPost(author, title, content, imageforpost).save();
    	travel(null);
    }
    
    public static void getPostImage(long id) {
    	   final TravelPost post = TravelPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final TravelPost post = TravelPost.findById(id);
    	post.delete();
    	travel(null);
    }
    public static void deleteComment(long id){
    	final TravelComment comment = TravelComment.findById(id);
    	comment.delete();
    	travel(null);
    }
    public static void likeThePost(long id){
    	final TravelPost post = TravelPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
 public static List<TravelPost> getLatestPost(){
	 List<TravelPost> recentPosts =TravelPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<TravelPost> getRecentFivePost(){
	 List<TravelPost> recentPosts =TravelPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
