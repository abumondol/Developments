package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	public static Result index() {        
		if(session("user_id")!=null)
		{	
			return ok(views.html.home.render(session("user_code"), session("user_id")));
		}
		else
			return ok(views.html.login.render());
    }
}
