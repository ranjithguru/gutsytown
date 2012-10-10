package controllers;

import java.util.List;

import models.Comment;
import models.LoveComment;
import models.LovePost;
import models.Post;
import models.User;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class LoveStory extends Controller {
	
	public static void loveStory(Long id) {
		Application.doInitialSetup();
    	LovePost frontPost;
    	if(id!=null){
    		frontPost =LovePost.findById(id);
    	}
    	else{
    		frontPost = LovePost.find("order by postedAt desc").first();
    	}
        List<LovePost> olderPosts = LovePost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        render(frontPost, olderPosts);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content) {
	    	
		 LovePost post = LovePost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	loveStory(postId);
	        	 
	        }
	        post.addComment(author, content);
	        loveStory(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	LovePost frontPost =LovePost.findById(frontPostId);
    	LovePost lastPost=LovePost.findById(lastPostId);
    	List<LovePost> olderPosts = LovePost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/LoveStory/loveStory.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	LovePost frontPost =LovePost.findById(frontPostId);
    	LovePost lastPost=LovePost.findById(lastPostId);
    	List<LovePost> olderPosts = LovePost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/LoveStory/loveStory.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new LovePost(author, title, content, imageforpost).save();
    	loveStory(null);
    }
    
    public static void getPostImage(long id) {
    	   final LovePost post = LovePost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final LovePost post = LovePost.findById(id);
    	post.delete();
    	loveStory(null);
    }
    public static void deleteComment(long id){
    	final LoveComment comment = LoveComment.findById(id);
    	comment.delete();
    	loveStory(null);
    }
    public static void likeThePost(long id){
    	final LovePost post = LovePost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
 public static List<LovePost> getLatestPost(){
	 List<LovePost> recentPosts =LovePost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
}
