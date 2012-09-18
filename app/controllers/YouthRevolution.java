package controllers;

import java.util.List;

import models.Comment;
import models.Post;
import models.User;
import models.YouthComment;
import models.YouthPost;

import com.google.gson.JsonObject;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.mvc.Controller;

public class YouthRevolution extends Controller{

	public static void youthRevolution(Long id) {
    	YouthPost frontPost;
    	if(id!=null){
    		frontPost =YouthPost.findById(id);
    	}
    	else{
    		frontPost = YouthPost.find("order by postedAt desc").first();
    	}
        List<YouthPost> olderPosts = YouthPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        render(frontPost, olderPosts);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content) {
	    	
		 YouthPost post = YouthPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	youthRevolution(postId);
	        	 
	        }
	        post.addComment(author, content);
	        youthRevolution(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	YouthPost frontPost =YouthPost.findById(frontPostId);
    	YouthPost lastPost=YouthPost.findById(lastPostId);
    	List<YouthPost> olderPosts = YouthPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/YouthRevolution/youthRevolution.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	YouthPost frontPost =YouthPost.findById(frontPostId);
    	YouthPost lastPost=YouthPost.findById(lastPostId);
    	List<YouthPost> olderPosts = YouthPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/YouthRevolution/youthRevolution.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new YouthPost(author, title, content, imageforpost).save();
    	youthRevolution(null);
    }
    
    public static void getPostImage(long id) {
    	   final YouthPost post = YouthPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final YouthPost post = YouthPost.findById(id);
    	post.delete();
    	youthRevolution(null);
    }
    public static void deleteComment(long id){
    	final YouthComment comment = YouthComment.findById(id);
    	comment.delete();
    	youthRevolution(null);
    }
    public static void likeThePost(long id){
    	final YouthPost post = YouthPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
 public static List<YouthPost> getLatestPost(){
	 List<YouthPost> recentPosts =YouthPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
}
