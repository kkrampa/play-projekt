package services;

import models.User;
import play.db.ebean.Model;

/**
 * Created by kamil on 13.05.14.
 */

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class UserDAO {

    public final Model.Finder<Long, User> find = new Model.Finder<Long, User>(
            Long.class, User.class
    );

    public User findByUsername(String username) {
        return find.where().eq("username", username).findUnique();
    }

    public User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public User findByFbId(String id) {
        return find.where().eq("fbId", id).findUnique();
    }

    public User get(Long id) {
        return find.byId(id);
    }

    public User create(User user) {
        user.save();
        return findByUsername(user.getUsername());
    }

    public List<User> findAll() {
        return find.all();
    }

}
