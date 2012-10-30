package controllers;

import play.mvc.Controller;

public class Decision  extends Controller{
	
	public static void decision() {
		Application.doInitialSetup();
		render();
	}
	}


