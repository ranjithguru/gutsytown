package controllers;

import java.util.List;

import models.JobComment;
import models.JobPost;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class Job extends Controller{
	
	
	public static void job(Long id) {
		Application.doInitialSetup();
		JobPost frontPost;
    	if(id!=null){
    		frontPost =JobPost.findById(id);
    	}
    	else{
    		frontPost = JobPost.find("order by postedAt desc").first();
    	}
        List<JobPost> olderPosts = JobPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 JobPost post = JobPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	job(postId);
	        	 
	        }
	        post.addComment(author, content);
	        job(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	JobPost frontPost =JobPost.findById(frontPostId);
    	JobPost lastPost=JobPost.findById(lastPostId);
    	List<JobPost> olderPosts = JobPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Job/job.html",frontPost, olderPosts);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	JobPost frontPost =JobPost.findById(frontPostId);
    	JobPost lastPost=JobPost.findById(lastPostId);
    	List<JobPost> olderPosts = JobPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	renderTemplate("app/views/Job/job.html",frontPost, olderPosts);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new JobPost(author, title, content, imageforpost).save();
    	job(null);
    }
    
    public static void getPostImage(long id) {
    	   final JobPost post = JobPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final JobPost post = JobPost.findById(id);
    	post.delete();
    	job(null);
    }
    public static void deleteComment(long id){
    	final JobComment comment = JobComment.findById(id);
    	comment.delete();
    	job(null);
    }
    public static void likeThePost(long id){
    	final JobPost post = JobPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    
 public static List<JobPost> getLatestPost(){
	 List<JobPost> recentPosts =JobPost.find("order by postedAt desc").from(0).fetch(2);
	 return recentPosts;
 }
 public static List<JobPost> getRecentFivePost(){
	 List<JobPost> recentPosts =JobPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
}
