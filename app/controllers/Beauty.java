package controllers;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;

import models.BeautyComment;
import models.BeautyPost;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class Beauty extends Controller{
	
	public static void beauty(Long id) {
		Application.doInitialSetup();
		BeautyPost frontPost;
    	if(id!=null){
    		frontPost =BeautyPost.findById(id);
    	}
    	else{
    		frontPost = BeautyPost.find("order by postedAt desc").first();
    	}
        List<BeautyPost> olderPosts = BeautyPost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 BeautyPost post = BeautyPost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	beauty(postId);
	        	 
	        }
	        post.addComment(author, content);
	        beauty(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	BeautyPost frontPost =BeautyPost.findById(frontPostId);
    	BeautyPost lastPost=BeautyPost.findById(lastPostId);
    	List<BeautyPost> olderPosts = BeautyPost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	String randomID = Codec.UUID();
    	renderTemplate("app/views/Beauty/beauty.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	BeautyPost frontPost =BeautyPost.findById(frontPostId);
    	BeautyPost lastPost=BeautyPost.findById(lastPostId);
    	List<BeautyPost> olderPosts = BeautyPost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	String randomID = Codec.UUID();
    	renderTemplate("app/views/Beauty/beauty.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new BeautyPost(author, title, content, imageforpost).save();
    	beauty(null);
    }
    
    public static void getPostImage(long id) {
    	   final BeautyPost post = BeautyPost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final BeautyPost post = BeautyPost.findById(id);
    	post.delete();
    	beauty(null);
    }
    public static void deleteComment(long id){
    	final BeautyComment comment = BeautyComment.findById(id);
    	comment.delete();
    	beauty(null);
    }
    public static void likeThePost(long id){
    	final BeautyPost post = BeautyPost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }
    
 public static List<BeautyPost> getRecentFivePost(){
	 List<BeautyPost> recentPosts =BeautyPost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
 public static String getStarName(){
		String sql= "SELECT id FROM User WHERE id IN ("+
				 "SELECT author_id FROM BeautyPost GROUP BY author_id"+
				 " HAVING COUNT(*) = (SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM BeautyPost GROUP BY author_id HAVING author_id!=1) AS ma)"+
				 "AND author_id != 1) ORDER BY points desc LIMIT 1 ";
		
		 Query query = JPA.em().createNativeQuery(sql);
		List<BigInteger> userIdList =query.getResultList();
		BigInteger bigIntegerUserId=userIdList.get(0);
		Long longuserId=(long) bigIntegerUserId.intValue();
		User star= User.findById(longuserId);
		return star.fullname;
	 }
	 public static String getStarTotalPosts(){
		 String sql="SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM BeautyPost GROUP BY author_id HAVING author_id!=1) AS ma";
		 Query query = JPA.em().createNativeQuery(sql);
		 List<BigInteger> result =query.getResultList();
		 BigInteger bigIntegerUserId=result.get(0);
		 String totalStarPosts=bigIntegerUserId.toString();
		 return totalStarPosts;

	 }
}
