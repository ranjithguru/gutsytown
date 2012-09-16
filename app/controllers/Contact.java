package controllers;

import java.util.List;

import models.ContactComment;
import models.Post;
import models.SimpleComment;
import play.mvc.Controller;

public class Contact extends Controller  {

	public static void contact(){
		 List<ContactComment> contactCommentList = ContactComment.find("order by postedAt desc").fetch();
    	render(contactCommentList);
    }
	
   
    public static void postComment(String author, String content) {
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
}
