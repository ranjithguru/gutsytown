package controllers;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;

import models.Comment;
import models.TravelComment;
import models.TravelPost;
import models.Post;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
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
    	String randomID = Codec.UUID();
    	renderTemplate("app/views/Travel/travel.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	TravelPost frontPost =TravelPost.findById(frontPostId);
    	TravelPost lastPost=TravelPost.findById(lastPostId);
    	List<TravelPost> olderPosts = TravelPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	String randomID = Codec.UUID();
    	renderTemplate("app/views/Travel/travel.html",frontPost, olderPosts,randomID);
    	
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

 public static List<TravelPost> getRecentFivePost(){
	 List<TravelPost> recentPosts =TravelPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
 public static String getStarName(){
		String sql= "SELECT id FROM User WHERE id IN ("+
				 "SELECT author_id FROM TravelPost GROUP BY author_id"+
				 " HAVING COUNT(*) = (SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM TravelPost GROUP BY author_id HAVING author_id!=1) AS ma)"+
				 "AND author_id != 1) ORDER BY points desc LIMIT 1 ";
		
		 Query query = JPA.em().createNativeQuery(sql);
		List<BigInteger> userIdList =query.getResultList();
		BigInteger bigIntegerUserId=userIdList.get(0);
		Long longuserId=(long) bigIntegerUserId.intValue();
		User star= User.findById(longuserId);
		return star.fullname;
	 }
	 public static String getStarTotalPosts(){
		 String sql="SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM TravelPost GROUP BY author_id HAVING author_id!=1) AS ma";
		 Query query = JPA.em().createNativeQuery(sql);
		 List<BigInteger> result =query.getResultList();
		 BigInteger bigIntegerUserId=result.get(0);
		 String totalStarPosts=bigIntegerUserId.toString();
		 return totalStarPosts;

	 }
}
