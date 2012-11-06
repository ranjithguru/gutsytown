package controllers;

import java.util.List;

import models.Comment;
import models.DecisionComment;
import models.DecisionPost;
import models.Post;
import models.User;

import com.google.gson.JsonObject;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.mvc.Controller;

public class Decision extends Controller {
	
	public static void decision(Long id) {
		Application.doInitialSetup();
		DecisionPost frontPost;
    	if(id!=null){
    		frontPost =DecisionPost.findById(id);
    	}
    	else{
    		frontPost = DecisionPost.find("order by postedAt desc").first();
    	}
        List<DecisionPost> olderPosts = DecisionPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        render(frontPost, olderPosts);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content) {
	    	
		 DecisionPost post = DecisionPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	decision(postId);
	        	 
	        }
	        post.addComment(author, content);
	        decision(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	DecisionPost frontPost =DecisionPost.findById(frontPostId);
    	DecisionPost lastPost=DecisionPost.findById(lastPostId);
    	List<DecisionPost> olderPosts = DecisionPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Decision/decision.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	DecisionPost frontPost =DecisionPost.findById(frontPostId);
    	DecisionPost lastPost=DecisionPost.findById(lastPostId);
    	List<DecisionPost> olderPosts = DecisionPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Decision/decision.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new DecisionPost(author, title, content, imageforpost).save();
    	decision(null);
    }
    
    public static void getPostImage(long id) {
    	   final DecisionPost post = DecisionPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final DecisionPost post = DecisionPost.findById(id);
    	post.delete();
    	decision(null);
    }
    public static void deleteComment(long id){
    	final DecisionComment comment = DecisionComment.findById(id);
    	comment.delete();
    	decision(null);
    }
    public static void likeThePost(long id){
    	final DecisionPost post = DecisionPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    public static void likeTheComment(long id){
    	final DecisionComment comment = DecisionComment.findById(id);
    	comment.addLike();
    	//User user=post.author;
    	//user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",comment.likes);
    	 renderJSON(jsObject.toString());
    }
 public static List<DecisionPost> getLatestPost(){
	 List<DecisionPost> recentPosts =DecisionPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<DecisionPost> getRecentFivePost(){
	 List<DecisionPost> recentPosts =DecisionPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}



