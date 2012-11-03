package models;


import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Query;

import org.hibernate.type.ImageType;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
public class User extends Model {
 
	public static int MALE=1;
	public static int FEMALE=2;
	
	@Email
    @Required
    public String email;
	@Required
    public String password;
	@Required
    public String fullname;
    public boolean isAdmin;
   
    @Required
    public String gender;
    
    public int points=0;
    
    public User(String fullname,String email, String password,String gender) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.gender=gender;
    }

	public static Boolean authenticateUser(String email, String password) {
		User user = User.find("email=? and password=?",email,password).first();
		if(user==null){
			return false;
		}
		return true;
	}
	public void addPoint(){
    	this.points=this.points+1;
    	this.save();
    }
 
	public static User gutsyGuyWinner(){
		 Query query = JPA.em().createQuery("select u from User u where u.gender=1 order by u.points desc ");
		 User user = (User) query.getResultList().get(0);
		 return user;
	}
	public static User gutsyGirlWinner(){
		 Query query = JPA.em().createQuery("select u from User u where u.gender=2 order by u.points desc ");
		 User user = (User) query.getResultList().get(0);
		return user;
	}
	
	public static List<User> getTopThreeGutsyGuys(){
		 Query query = JPA.em().createQuery("select u from User u where u.gender=1 and u.id!=1 order by u.points desc ");
		 List<User> topThreeGuys = query.getResultList().subList(0, 3);
		 return topThreeGuys;
    }
	public static List<User> getTopThreeGutsyGirls(){
		 Query query = JPA.em().createQuery("select u from User u where u.gender=2 order by u.points desc ");
		 List<User> topThreeGirls = query.getResultList().subList(0, 3);
		 return topThreeGirls;
   }
}
