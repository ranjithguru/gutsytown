package controllers;

import java.util.List;

import models.IndianComment;
import play.data.validation.Required;
import play.mvc.Controller;

public class IndianConstitution extends Controller {
	
	public static void indianConstitution(){
		Application.doInitialSetup();
	    List<IndianComment> simpleCommentList = IndianComment.find("order by postedAt desc").fetch();
        render(simpleCommentList);
}
	
	public static void postConstitutionComment(@Required String author, @Required String content) {
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
