package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Blob;
import play.db.jpa.Model;
@Entity
public class Poll extends Model{
	
	 public String title;
	 public Date postedAt;
	 public String optOne;
	 public String optTwo;
	 public String optThree;
	 public String optFour;
	 public String optFive;
	 public String district;
	 
	 public int optOneCount=0;
	 public int optTwoCount=0;
	 public int optThreeCount=0;
	 public int optFourCount=0;
	 public int optFiveCount=0;
	 
	 public Poll(String title,String op1,String op2,String op3,String op4,String op5,String district){
		 this.title=title;
		 this.optOne=op1;
		 this.optTwo=op2;
		 this.optThree=op3;
		 this.optFour=op4;
		 this.optFive=op5; 
		 this.district=district;
	 }
	 public void addOptionOneCount(){
		 this.optOneCount=this.optOneCount+1;
	    	this.save();
		 
	 }
	 public void addOptionTwoCount(){
		 this.optTwoCount=this.optTwoCount+1;
	    	this.save();
		 
	 }
	 public void addOptionThreeCount(){
		 this.optThreeCount=this.optThreeCount+1;
	    	this.save();
		 
	 }
	 public void addOptionFourCount(){
		 this.optFourCount=this.optFourCount+1;
	    	this.save();
		 
	 }
	 public void addOptionFiveCount(){
		 this.optFiveCount=this.optFiveCount+1;
	    this.save();
	 }
	 
}


