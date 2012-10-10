package controllers;

import java.util.List;

import models.ContactComment;
import models.IndianComment;
import models.Post;
import play.data.validation.Required;
import play.mvc.Controller;

public class Contact extends Controller  {

	public static void contact(){
		Application.doInitialSetup();
		 List<ContactComment> contactCommentList = ContactComment.find("order by postedAt desc").fetch();
    	render(contactCommentList);
    }
	
   
    public static void postComment(@Required String author, @Required String content) {
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
