package controllers;

import play.mvc.Controller;

public class TalkFight  extends Controller{
	
	public static void talkFight() {
		Application.doInitialSetup();
		render();
	}
	}


