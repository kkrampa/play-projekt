package services;

import models.Role;
import play.db.ebean.Model;

import javax.inject.Singleton;
import java.util.List;

/**
 * Created by kamil on 16.05.14.
 */
@Singleton
public class RoleDAO {
    public static Model.Finder<Long, Role> find = new Model.Finder<Long, Role>(
            Long.class, Role.class
    );

    public Role getRole(Long id) {
        return find.byId(id);
    }

    public List<Role> getAll() {
        return find.all();
    }

    public Role findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }
}
