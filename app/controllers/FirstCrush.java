package controllers;

import java.util.List;

import models.Comment;
import models.CrushComment;
import models.CrushPost;
import models.Post;
import models.User;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class FirstCrush extends Controller {
	
	public static void firstCrush(Long id) {
		Application.doInitialSetup();
    	CrushPost frontPost;
    	if(id!=null){
    		frontPost =CrushPost.findById(id);
    	}
    	else{
    		frontPost = CrushPost.find("order by postedAt desc").first();
    	}
        List<CrushPost> olderPosts = CrushPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        render(frontPost, olderPosts);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content) {
	    	
		 CrushPost post = CrushPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	firstCrush(postId);
	        	 
	        }
	        post.addComment(author, content);
	        firstCrush(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	CrushPost frontPost =CrushPost.findById(frontPostId);
    	CrushPost lastPost=CrushPost.findById(lastPostId);
    	List<CrushPost> olderPosts = CrushPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/FirstCrush/firstCrush.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	CrushPost frontPost =CrushPost.findById(frontPostId);
    	CrushPost lastPost=CrushPost.findById(lastPostId);
    	List<CrushPost> olderPosts = CrushPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/FirstCrush/firstCrush.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new CrushPost(author, title, content, imageforpost).save();
    	firstCrush(null);
    }
    
    public static void getPostImage(long id) {
    	   final CrushPost post = CrushPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final CrushPost post = CrushPost.findById(id);
    	post.delete();
    	firstCrush(null);
    }
    public static void deleteComment(long id){
    	final CrushComment comment = CrushComment.findById(id);
    	comment.delete();
    	firstCrush(null);
    }
    public static void likeThePost(long id){
    	final CrushPost post = CrushPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    
 public static List<CrushPost> getLatestPost(){
	 List<CrushPost> recentPosts =CrushPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<CrushPost> getRecentFivePost(){
	 List<CrushPost> recentPosts =CrushPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
