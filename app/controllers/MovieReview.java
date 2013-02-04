package controllers;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;

import models.Comment;
import models.MovieComment;
import models.MoviePost;
import models.Post;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.gson.JsonObject;

public class  MovieReview extends Controller {
	
	public static void movieReview(Long id) {
		Application.doInitialSetup();
    	MoviePost frontPost;
    	if(id!=null){
    		frontPost =MoviePost.findById(id);
    	}
    	else{
    		frontPost = MoviePost.find("order by postedAt desc").first();
    	}
        List<MoviePost> olderPosts = MoviePost.find(
            "order by postedAt desc"
        ).from(0).fetch(5);
        String randomID = Codec.UUID();
        render(frontPost, olderPosts,randomID);
    }
	 public static void postComment(Long postId, @Required String author, @Required String content,@Required String code, 
		        String randomID) {
		 validation.equals(
			        code, Cache.get(randomID));
		 MoviePost post = MoviePost.findById(postId);
	        if (validation.hasErrors()) {
	        	params.flash();
	        	validation.keep();
	        	movieReview(postId);
	        	 
	        }
	        post.addComment(author, content);
	        movieReview(postId);
	    }
	 
    public static void nextOldPosts(Long frontPostId,Long lastPostId){    	
    	MoviePost frontPost =MoviePost.findById(frontPostId);
    	MoviePost lastPost=MoviePost.findById(lastPostId);
    	List<MoviePost> olderPosts = MoviePost.find(
                "postedAt < ? order by postedAt desc",lastPost.postedAt
            ).fetch(5);
    	 String randomID = Codec.UUID();
    	renderTemplate("app/views/MovieReview/movieReview.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void previousOldPosts(Long frontPostId,Long lastPostId){    	
    	MoviePost frontPost =MoviePost.findById(frontPostId);
    	MoviePost lastPost=MoviePost.findById(lastPostId);
    	List<MoviePost> olderPosts = MoviePost.find(
                "postedAt > ? order by postedAt asc",lastPost.postedAt
            ).fetch(5);
    	 String randomID = Codec.UUID();
    	renderTemplate("app/views/MovieReview/movieReview.html",frontPost, olderPosts,randomID);
    	
    }
    
    public static void writeNew(String title,String content,Blob imageforpost){
    	Long userId=Long.parseLong(session.get("userid"));
    	User author= User.findById(userId);
    	new MoviePost(author, title, content, imageforpost).save();
    	movieReview(null);
    }
    
    public static void getPostImage(long id) {
    	   final MoviePost post = MoviePost.findById(id);
    	   notFoundIfNull(post);
    	   response.setContentTypeIfNotSet(post.postImage.type());
    	   renderBinary(post.postImage.get());
    	}
    public static void deletePost(long id){
    	final MoviePost post = MoviePost.findById(id);
    	post.delete();
    	movieReview(null);
    }
    public static void deleteComment(long id){
    	final MovieComment comment = MovieComment.findById(id);
    	comment.delete();
    	movieReview(null);
    }
    public static void likeThePost(long id){
    	final MoviePost post = MoviePost.findById(id);
    	post.addLike();
    	User user=post.author;
    	user.addPoint();
    	 JsonObject jsObject=new JsonObject();
    	 jsObject.addProperty("likeSuccess",true);
    	 jsObject.addProperty("postLikes",post.likes);
    	 renderJSON(jsObject.toString());
    }

 public static List<MoviePost> getRecentFivePost(){
	 List<MoviePost> recentPosts =MoviePost.find("order by postedAt desc").from(0).fetch(5);
	 return recentPosts;
 }
 public static String getStarName(){
		String sql= "SELECT id FROM User WHERE id IN ("+
				 "SELECT author_id FROM MoviePost GROUP BY author_id"+
				 " HAVING COUNT(*) = (SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM MoviePost GROUP BY author_id HAVING author_id!=1) AS ma)"+
				 "AND author_id != 1) ORDER BY points desc LIMIT 1 ";
		
		 Query query = JPA.em().createNativeQuery(sql);
		List<BigInteger> userIdList =query.getResultList();
		BigInteger bigIntegerUserId=userIdList.get(0);
		Long longuserId=(long) bigIntegerUserId.intValue();
		User star= User.findById(longuserId);
		return star.fullname;
	 }
	 public static String getStarTotalPosts(){
		 String sql="SELECT MAX(noofpost) from (SELECT COUNT(author_id) AS noofpost FROM MoviePost GROUP BY author_id HAVING author_id!=1) AS ma";
		 Query query = JPA.em().createNativeQuery(sql);
		 List<BigInteger> result =query.getResultList();
		 BigInteger bigIntegerUserId=result.get(0);
		 String totalStarPosts=bigIntegerUserId.toString();
		 return totalStarPosts;

	 }
}

