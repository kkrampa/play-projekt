package services;

import models.Contact;
import play.db.ebean.Model;

import javax.inject.Singleton;
import java.util.List;

/**
 * Created by kamil on 16.05.14.
 */
@Singleton
public class ContactDAO {
    public static Model.Finder<Long,Contact> find = new Model.Finder<Long, Contact>(
            Long.class, Contact.class
    );

    public Contact getContact(Long id) {
        return find.byId(id);
    }

    public List<Contact> getAllContacts() {
        return find.all();
    }
}
