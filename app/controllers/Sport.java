package controllers;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;

import models.SportComment;
import models.SportPost;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class Sport extends Controller{
	
	public static void sport(Long id) {
		Application.doInitialSetup();
		SportPost frontPost;
    	if(id!=null){
    		frontPost =SportPost.findById(id);
    	}
    	else{
    		frontPost = SportPost.find("order by postedAt desc").first();
    	}
        List<SportPost> olderPosts = SportPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 SportPost post = SportPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	sport(postId);
	        	 
	        }
	        post.addComment(author, content);
	        sport(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	SportPost frontPost =SportPost.findById(frontPostId);
    	SportPost lastPost=SportPost.findById(lastPostId);
    	List<SportPost> olderPosts = SportPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	 String randomID = Codec.UUID();
    	renderTemplate("app/views/Sport/sport.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	SportPost frontPost =SportPost.findById(frontPostId);
    	SportPost lastPost=SportPost.findById(lastPostId);
    	List<SportPost> olderPosts = SportPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	 String randomID = Codec.UUID();
    	renderTemplate("app/views/Sport/sport.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new SportPost(author, title, content, imageforpost).save();
    	sport(null);
    }
    
    public static void getPostImage(long id) {
    	   final SportPost post = SportPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final SportPost post = SportPost.findById(id);
    	post.delete();
    	sport(null);
    }
    public static void deleteComment(long id){
    	final SportComment comment =SportComment.findById(id);
    	comment.delete();
    	sport(null);
    }
    public static void likeThePost(long id){
    	final SportPost post = SportPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    
 
 public static List<SportPost> getRecentFivePost(){
	 List<SportPost> recentPosts =SportPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
 public static String getStarName(){
		String sql= "SELECT id FROM User WHERE id IN ("+
				 "SELECT author_id FROM sportpost GROUP BY author_id"+
				 " HAVING COUNT(*) = (SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM sportpost GROUP BY author_id HAVING author_id!=1) AS ma)"+
				 "AND author_id != 1) ORDER BY points desc LIMIT 1 ";
		
		 Query query = JPA.em().createNativeQuery(sql);
		List<BigInteger> userIdList =query.getResultList();
		BigInteger bigIntegerUserId=userIdList.get(0);
		Long longuserId=(long) bigIntegerUserId.intValue();
		User star= User.findById(longuserId);
		return star.fullname;
	 }
	 public static String getStarTotalPosts(){
		 String sql="SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM sportpost GROUP BY author_id HAVING author_id!=1) AS ma";
		 Query query = JPA.em().createNativeQuery(sql);
		 List<BigInteger> result =query.getResultList();
		 BigInteger bigIntegerUserId=result.get(0);
		 String totalStarPosts=bigIntegerUserId.toString();
		 return totalStarPosts;

	 }
}
