package controllers;

import play.*;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.Images;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {
	 
	
    public static void index() {
    	doInitialSetup();
    	Map<String,Model> glide=new HashMap<String, Model>();
    	List<PoliticsPost> politics=Politics.getRecentFivePost();
    	glide.put("po",politics.get(0));
    	List<DecisionPost> decision=Decision.getRecentFivePost();
    	glide.put("de",decision.get(0));
    	List<HealthPost> health=Health.getRecentFivePost();
    	glide.put("he",health.get(0));
    	List<FoodPost> food=Food.getRecentFivePost();
    	glide.put("fo",food.get(0));
    	List<BeautyPost> beauty=Beauty.getRecentFivePost();
    	glide.put("be",beauty.get(0));
    	List<MoneyPost> money=Money.getRecentFivePost();
    	glide.put("mo",money.get(0));
    	List<TechPost> tech=Tech.getRecentFivePost();
    	glide.put("te",tech.get(0));
    	List<SportPost> sport=Sport.getRecentFivePost();
    	glide.put("sp",sport.get(0));
    	List<JobPost> job=Job.getRecentFivePost();
    	glide.put("jo",job.get(0));
    	List<Post> yourStory=YourStory.getRecentFivePost();
    	glide.put("yo",yourStory.get(0));
    	List<MoviePost> movie=MovieReview.getRecentFivePost();
    	glide.put("mr",movie.get(0));
    	List<AutoPost> auto=Auto.getRecentFivePost();
    	glide.put("au",auto.get(0));
    	List<TravelPost> travel=Travel.getRecentFivePost();
    	glide.put("tr",travel.get(0));
    	List<BlogPost> blog=MyBlog.getRecentFivePost();
    	glide.put("bl",blog.get(0));
    	
    	render(politics,decision,health,food,beauty,money,tech,sport,job,yourStory,movie,auto,travel,blog,glide);
    }
    /**
     * this should not be public or else it will be a action
     */
    static void doInitialSetup() {
    	setGutsyWinnerGuy();
    	setGutsyWinnerGirl();
    	setTopThreeGutsyGuys();
    	setTopThreeGutsyGirls();
    	
    	setPoliticsStar();
    	setDecisionStar();
    	setHealthStar();
    	setFoodStar();
    	setBeautyStar();
    	setMoneyStar();
    	setTechStar();
    	setSportStar();
    	setJobStar();
    	setStoryStar();
    	setMovieStar();
    	setAutoStar();
    	setTravelStar();
		
	}

    
	private static void setJobStar() {
		session.put("jstar",Job.getStarName());
		session.put("jpost",Job.getStarTotalPosts());
		
	}


	private static void setStoryStar() {
		session.put("ysstar",YourStory.getStarName());
		session.put("yspost",YourStory.getStarTotalPosts());
		
	}


	private static void setMovieStar() {
		session.put("mrstar",MovieReview.getStarName());
		session.put("mrpost",MovieReview.getStarTotalPosts());
		
	}


	private static void setTravelStar() {
		session.put("ytstar",Travel.getStarName());
		session.put("ytpost",Travel.getStarTotalPosts());
		
	}


	private static void setSportStar() {
		session.put("sstar",Sport.getStarName());
		session.put("spost",Sport.getStarTotalPosts());
		
	}


	private static void setTechStar() {
		session.put("tstar",Tech.getStarName());
		session.put("tpost",Tech.getStarTotalPosts());
		
	}


	private static void setMoneyStar() {
		session.put("mstar",Money.getStarName());
		session.put("mpost",Money.getStarTotalPosts());
		
	}


	private static void setBeautyStar() {
		session.put("bstar",Beauty.getStarName());
		session.put("bpost",Beauty.getStarTotalPosts());
		
	}


	private static void setFoodStar() {
		session.put("fstar",Food.getStarName());
		session.put("fpost",Food.getStarTotalPosts());
		
	}


	private static void setHealthStar() {
		session.put("hstar",Health.getStarName());
		session.put("hpost",Health.getStarTotalPosts());
		
	}


	private static void setDecisionStar() {
		session.put("dstar",Decision.getStarName());
		session.put("dpost",Decision.getStarTotalPosts());
		
	}


	private static void setAutoStar() {
		session.put("astar",Auto.getStarName());
		session.put("apost",Auto.getStarTotalPosts());
		
	}
	private static void setPoliticsStar() {
			session.put("pstar",Politics.getStarName());
			session.put("ppost",Politics.getStarTotalPosts());
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
	public static void captcha(String id) {
	    Images.Captcha captcha = Images.captcha();
	    String code = captcha.getText("#E4EAFD");
	    Cache.set(id, code, "10mn");
	    renderBinary(captcha);
	}
}