package controllers;

import java.util.List;

import models.IndianComment;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.Controller;

public class IndianConstitution extends Controller {
	
	public static void indianConstitution(){
		Application.doInitialSetup();
	    List<IndianComment> simpleCommentList = IndianComment.find("order by postedAt desc").fetch();
	    String randomID = Codec.UUID();
        render(simpleCommentList,randomID);
}
	
	public static void postConstitutionComment(@Required String author, @Required String content,@Required String code, 
	        String randomID) {
		
		validation.equals(
		        code, Cache.get(randomID));
		if (validation.hasErrors()) {
        	params.flash();
        	validation.keep();
        	indianConstitution();
        	 
        }
    	new IndianComment(author, content).save();
    	indianConstitution();
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
    	indianConstitution();
    }
}
