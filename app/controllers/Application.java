package controllers;

import play.Routes;
import play.mvc.*;
public class Application extends Controller {

   /* public static Result index() {
    	List<Contact> contacts = Contact.find.all();
    	return ok(index.render(contacts));
    }*/

    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("myJsRoutes",
                      routes.javascript.Frontend.registerFbUser(),
                      routes.javascript.Users.changeRole()

                )
        );
    }



}
