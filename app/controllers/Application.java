package controllers;

import play.*;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {
	 
	
    public static void index() {
    	doInitialSetup();
    	 List<IndianComment> simpleCommentList = IndianComment.find("order by postedAt desc").fetch();
        render(simpleCommentList);
    }
    
  
    /**
     * this should not be public or else it will be a action
     */
    static void doInitialSetup() {
    	setGutsyWinnerGuy();
    	setGutsyWinnerGirl();
    	setTopThreeGutsyGuys();
    	setTopThreeGutsyGirls();
    	setRecentSadPosts();
    	setRecentYouthPosts();
    	setRecentCrushPosts();
    	setRecentLovePosts();
    	setRecentMovieReviews();
    	setRecentMyBlogPosts();
		
	}



	private static void setRecentMyBlogPosts() {
		List<BlogPost> recentPosts=MyBlog.getLatestPost();

		BlogPost post1=recentPosts.get(0);
				if(post1!=null){
					session.put("my1",post1.title);
					session.put("my1id",post1.id);
				}
				BlogPost post2=recentPosts.get(1);
				if(post2!=null){
				session.put("my2",post2.title);
				session.put("my2id",post2.id);
				}
				
		
	}



	private static void setRecentMovieReviews() {
		
		List<MoviePost> recentPosts=MovieReview.getLatestPost();

		MoviePost post1=recentPosts.get(0);
				if(post1!=null){
					session.put("mov1",post1.title);
					session.put("mov1id",post1.id);
				}
				MoviePost post2=recentPosts.get(1);
				if(post2!=null){
				session.put("mov2",post2.title);
				session.put("mov2id",post2.id);
				}
				
		
	}



	private static void setRecentLovePosts() {
		List<LovePost> recentPosts=LoveStory.getLatestPost();

		LovePost post1=recentPosts.get(0);
				if(post1!=null){
					session.put("love1",post1.title);
					session.put("love1id",post1.id);
				}
				LovePost post2=recentPosts.get(1);
				if(post2!=null){
				session.put("love2",post2.title);
				session.put("love2id",post2.id);
				}
				
		
	}



	private static void setRecentCrushPosts() {
		List<CrushPost> recentPosts=FirstCrush.getLatestPost();

		CrushPost post1=recentPosts.get(0);
				if(post1!=null){
					session.put("crush1",post1.title);
					session.put("crush1id",post1.id);
				}
				CrushPost post2=recentPosts.get(1);
				if(post2!=null){
				session.put("crush2",post2.title);
				session.put("crush2id",post2.id);
				}
				
	}



	private static void setRecentYouthPosts() {
List<YouthPost> recentPosts=YouthRevolution.getLatestPost();

  YouthPost post1=recentPosts.get(0);
		if(post1!=null){
			session.put("youth1",post1.title);
			session.put("youth1id",post1.id);
		}
		YouthPost post2=recentPosts.get(1);
		if(post2!=null){
		session.put("youth2",post2.title);
		session.put("youth2id",post2.id);
		}
		
	}



	private static void setRecentSadPosts() {
		List<Post> recentPosts=SadStory.getLatestPost();
		
		Post post1=recentPosts.get(0);
		if(post1!=null){
			session.put("sad1",post1.title);
			session.put("sad1id",post1.id);
		}
		Post post2=recentPosts.get(1);
		if(post2!=null){
		session.put("sad2",post2.title);
		session.put("sad2id",post2.id);
		}
		
	}



	private static void setTopThreeGutsyGirls() {
		List<User> topThreeGirls=User.getTopThreeGutsyGirls();
		session.put("girl1", topThreeGirls.get(0).fullname);
		session.put("girl2", topThreeGirls.get(1).fullname);
		session.put("girl3", topThreeGirls.get(2).fullname);
		
		session.put("girl1p", topThreeGirls.get(0).points);
		session.put("girl2p", topThreeGirls.get(1).points);
		session.put("girl3p", topThreeGirls.get(2).points);
	}



	private static void setTopThreeGutsyGuys() {
		List<User> topThreeGuys=User.getTopThreeGutsyGuys();
		session.put("guy1", topThreeGuys.get(0).fullname);
		session.put("guy2", topThreeGuys.get(1).fullname);
		session.put("guy3", topThreeGuys.get(2).fullname);
		
		session.put("guy1p", topThreeGuys.get(0).points);
		session.put("guy2p", topThreeGuys.get(1).points);
		session.put("guy3p", topThreeGuys.get(2).points);
		
	}



	private static void setGutsyWinnerGirl() {
        User gutsyGirl=User.gutsyGirlWinner();
    	session.put("gutsygirl", gutsyGirl.fullname);
    	session.put("gutsygirlp", gutsyGirl.points);
		
	}



	private static void setGutsyWinnerGuy() {
    	User gutsyGuy=User.gutsyGuyWinner();
    	session.put("gutsyguy", gutsyGuy.fullname);
    	session.put("gutsyguyp", gutsyGuy.points);
	}



	public static void postConstitutionComment(@Required String author, @Required String content) {
		if (validation.hasErrors()) {
        	params.flash();
        	validation.keep();
        	index();
        	 
        }
    	new IndianComment(author, content).save();
    	index();
    }
	
    public static long getCount(){
    	return IndianComment.count();
    }
    public static List<IndianComment> getSimpleCommentList(){ 
    List<IndianComment> simpleCommentList = IndianComment.find("order by postedAt desc").fetch();
    return simpleCommentList;
    }
    
    public static void deleteComment(long id){
    	final IndianComment comment = IndianComment.findById(id);
    	comment.delete();
    	index();
    }
   
}