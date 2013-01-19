package controllers;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;

import models.Comment;
import models.DecisionComment;
import models.DecisionPost;
import models.Post;
import models.User;

import com.google.gson.JsonObject;

import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.libs.Codec;
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
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
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
    	 String randomID = Codec.UUID();
    	renderTemplate("app/views/Decision/decision.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	DecisionPost frontPost =DecisionPost.findById(frontPostId);
    	DecisionPost lastPost=DecisionPost.findById(lastPostId);
    	List<DecisionPost> olderPosts = DecisionPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	 String randomID = Codec.UUID();
    	renderTemplate("app/views/Decision/decision.html",frontPost, olderPosts,randomID);
    	
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
 
 public static List<DecisionPost> getRecentFivePost(){
	 List<DecisionPost> recentPosts =DecisionPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
 public static String getStarName(){
		String sql= "SELECT id FROM User WHERE id IN ("+
				 "SELECT author_id FROM decisionpost GROUP BY author_id"+
				 " HAVING COUNT(*) = (SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM decisionpost GROUP BY author_id HAVING author_id!=1) AS ma)"+
				 "AND author_id != 1) ORDER BY points desc LIMIT 1 ";
		
		 Query query = JPA.em().createNativeQuery(sql);
		List<BigInteger> userIdList =query.getResultList();
		BigInteger bigIntegerUserId=userIdList.get(0);
		Long longuserId=(long) bigIntegerUserId.intValue();
		User star= User.findById(longuserId);
		return star.fullname;
	 }
	 public static String getStarTotalPosts(){
		 String sql="SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM decisionpost GROUP BY author_id HAVING author_id!=1) AS ma";
		 Query query = JPA.em().createNativeQuery(sql);
		 List<BigInteger> result =query.getResultList();
		 BigInteger bigIntegerUserId=result.get(0);
		 String totalStarPosts=bigIntegerUserId.toString();
		 return totalStarPosts;

	 }
}



