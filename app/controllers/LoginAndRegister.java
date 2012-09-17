package controllers;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import com.google.gson.JsonObject;

import models.User;

import play.data.validation.Required;
import play.libs.Crypto;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.utils.Java;

public class LoginAndRegister extends Controller{

	
	
	public static void register(String fullname,String email,String password,String gender) {
		 JsonObject jsObject=new JsonObject();
		 boolean uniqueAdress=isUniqueEmailAddress(email);
		 if (uniqueAdress){
			 User newUser = new User(fullname,email,password,gender);
			    newUser.save();
			    jsObject.addProperty("registerSuccess", true);
			    jsObject.addProperty("message","Your Registration Is Sucessfully! You Can Now Login With Your Credentials");
		 }
		 else{
			 jsObject.addProperty("registerSuccess", false);
			 jsObject.addProperty("message","Account With The Given Email Address Already Exists! Please Try With Different Email Address");
		 }
		 
	    renderJSON(jsObject.toString());
	   
	}
	
	 /**
     * This method is called during the authentication process. This is where you check if
     * the user is allowed to log in into the system. This is the actual authentication process
     * against a third party system (most of the time a DB).
     *
     * @param email
     * @param password
     * @return true if the authentication process succeeded
     */
	 public static void authenticate(String email, String password) throws Throwable {
		 JsonObject jsObject=new JsonObject();
	        User user = User.find("email=? and password=?",email,password).first();
	           if(user==null){
	        	   jsObject.addProperty("loginSuccess", false);
	        	   jsObject.addProperty("message", "The Login Details Entered by You Doesnot Match or May be Invalid");
	        	   jsObject.addProperty("loggedInUser","Guest");
	           }
	           else{
	        	   loginto(user);
	        	   jsObject.addProperty("loginSuccess", true);
	        	   jsObject.addProperty("message", "You are Now Logged In as "+ user.fullname);
	        	   jsObject.addProperty("loggedInUser",user.fullname);
	        	   
	           }
	    
	           renderJSON(jsObject.toString());
	    }
	 
	 private static void loginto(User user) {
		 session.put("username", user.fullname);
		 session.put("userid", user.id);
		
	}
	 
	 private static boolean isUniqueEmailAddress(String email){
		 User user = User.find("byEmail",email).first();
		 if(user==null){
			 return true;
		 }
		 return false;
	 }
	/**
      * This method returns the current connected username
      * @return
      */
     static String connected() {
         return session.get("username");
     }
     
     /**
      * Indicate if a user is currently connected
      * @return  true if the user is connected
      */
     private static boolean isConnected() {
         return session.contains("username");
     }

     public static void logout() throws Throwable {
    	 JsonObject jsObject=new JsonObject();
    	        if(isConnected()){
    	        	jsObject.addProperty("logoutSuccess", true);
    	        	jsObject.addProperty("message","You are Logged Out");
    	        	//session.clear();
    	        	session.remove("username");
    	        	session.remove("userid");
    	        }
    	        else{
    	        	jsObject.addProperty("logoutSuccess", false);
    	        	jsObject.addProperty("message", "oooops! You Cannot Logout without Logging In");
    	        }
    	        renderJSON(jsObject.toString());
     }
     
 public static void checkUserLogin(){
	 JsonObject jsObject=new JsonObject();
     if(isConnected()){
     	jsObject.addProperty("login", true);
     	
     }
     else{
     	jsObject.addProperty("login", false);
     }
     renderJSON(jsObject.toString());
 }
	            
}
