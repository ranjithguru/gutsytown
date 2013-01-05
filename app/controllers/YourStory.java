package controllers;

import java.awt.Image;
import java.util.List;

import com.google.gson.JsonObject;

import models.Comment;
import models.Post;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.mvc.Controller;

public class YourStory extends Controller {
	
	public static void yourStory(Long id) {
		Application.doInitialSetup();
    	Post frontPost;
    	if(id!=null){
    		frontPost =Post.findById(id);
    	}
    	else{
    		frontPost = Post.find("order by postedAt desc").first();
    	}
        List<Post> olderPosts = Post.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
	        Post post = Post.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	yourStory(postId);
	        	 
	        }
	        post.addComment(author, content);
	        yourStory(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	Post frontPost =Post.findById(frontPostId);
    	Post lastPost=Post.findById(lastPostId);
    	List<Post> olderPosts = Post.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/YourStory/yourStory.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	Post frontPost =Post.findById(frontPostId);
    	Post lastPost=Post.findById(lastPostId);
    	List<Post> olderPosts = Post.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/YourStory/yourStory.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new Post(author, title, content, imageforpost).save();
    	yourStory(null);
    }
    
    public static void getPostImage(long id) {
    	   final Post post = Post.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final Post post = Post.findById(id);
    	post.delete();
    	yourStory(null);
    }
    public static void deleteComment(long id){
    	final Comment comment = Comment.findById(id);
    	comment.delete();
    	yourStory(null);
    }
    public static void likeThePost(long id){
    	final Post post = Post.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
 public static List<Post> getLatestPost(){
	 List<Post> recentPosts =Post.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<Post> getRecentFivePost(){
	 List<Post> recentPosts =Post.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
