package controllers;


import com.avaje.ebean.Ebean;

import models.Contact;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import services.ContactDAO;
import utils.UserData;
import views.html.addContact;
import views.html.contactrows;
import views.html.listing;

import javax.inject.Inject;
import javax.inject.Singleton;

@With(UserData.class)
@Security.Authenticated(Secured.class)
@Singleton
public class Contacts extends Controller {

    @Inject
    private ContactDAO contactDAO;

    public Result index() {
        User user = User.find.where().eq("username", request().username()).findUnique();
		return ok(listing.render(contactrows.render(user.getContacts()), "Kontakty"));
	}
	
	public Result add() {
			return ok(addContact.render((Form.form(Contact.class)), "Dodaj kontakt"));
	}
	
	public Result edit(Long id) {
		Contact contact = contactDAO.getContact(id);
        if(contact == null) {
            flash("error", "Kontakt o podanym id nie istnieje!");
            return redirect(
                    routes.Contacts.index()
            );
        }
        String username = contact.getUser().getUsername();

        if(!username.equals(request().username())) {
            return forbidden("Brak dostępu");
        }
		return ok(addContact.render((Form.form(Contact.class).fill(contact)), "Edytuj kontakt"));
	}
	
	public Result save() {
		Form<Contact> loginForm = Form.form(Contact.class).bindFromRequest();

        if (loginForm.hasErrors()) {
                return badRequest(addContact.render(loginForm, "Dodaj/Edytuj kontakt"));
	    } else {
            Contact contact = loginForm.get();
            contact.setUser(User.find.where().eq("username", request().username()).findUnique());
            if(contact.getId() != null) {
	    		Ebean.update(contact);
	    	} else {
                Ebean.save(contact);
	    	}
            flash("success", "Kontakt dodany pomyślnie!");
	        return redirect(
	            routes.Contacts.index()
	        );
	    }
	}
	
	public Result delete(Long id) {
        Contact contact = Contact.find.byId(id);
        if(contact != null) {
            Ebean.delete(contact);
            flash("success", "Kontakt usunięty pomyślnie!");
        } else {
            flash("error", "Kontakt o podanym id nie istnieje!");
        }

        return redirect(
	            routes.Contacts.index()
	        );
	}
	
}
