package controllers;

import java.util.List;

import models.ContactComment;
import models.IndianComment;
import models.Post;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.Controller;

public class Contact extends Controller  {

	public static void contact(){
		Application.doInitialSetup();
		 List<ContactComment> contactCommentList = ContactComment.find("order by postedAt desc").fetch();
		 String randomID = Codec.UUID();
    	render(contactCommentList,randomID);
    }
	
   
    public static void postComment(@Required String author, @Required String content,@Required String code, 
	        String randomID) {
    	validation.equals(
		        code, Cache.get(randomID));
    	if (validation.hasErrors()) {
        	params.flash();
        	validation.keep();
        	contact();
        	 
        }
    	new ContactComment(author, content).save();
    	contact();
    }
      
    public static long getCount(){
    	return ContactComment.count();
    }
    public static List<ContactComment> getContactCommentList(){ 
    List<ContactComment> contactCommentList = ContactComment.find("order by postedAt desc").fetch();
    return contactCommentList;
    }
    
    
    public static void deleteComment(long id){
    	final ContactComment comment = ContactComment.findById(id);
    	comment.delete();
    	contact();
    }
}
