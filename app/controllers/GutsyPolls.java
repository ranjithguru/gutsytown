package controllers;

import play.mvc.Controller;

public class GutsyPolls extends Controller{
	
	public static void gutsyPolls() {
		Application.doInitialSetup();
		render();
	}

}
