package controllers;

import java.util.List;

import models.ContactComment;
import models.PoliticsPost;
import models.Poll;
import play.libs.Codec;
import play.mvc.Controller;

public class GutsyPolls extends Controller{
	
	public static void gutsyPolls() {
		Application.doInitialSetup();
		 List<Poll> pollList = Poll.find("order by postedAt desc").fetch();
		 render(pollList);
	}
public static void getPoll(Long id){
    	Poll poll=Poll.findById(id);
	renderTemplate("app/views/GutsyPolls/pollDisplay.html",poll);
}
public static void vote(Long id,int option){
	Poll poll=Poll.findById(id);
	addVote(poll,option);
    renderTemplate("app/views/GutsyPolls/pollResult.html",poll);
}
private static void addVote(Poll poll,int option) {
	switch(option){
	case 1:poll.addOptionOneCount();
	break;
	case 2:poll.addOptionTwoCount();
	break;
	case 3:poll.addOptionThreeCount();
	break;
	case 4:poll.addOptionFourCount();
	break;
	case 5:poll.addOptionFiveCount();
	break;
	}
}
}
