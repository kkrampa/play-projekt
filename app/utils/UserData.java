package utils;

import models.User;
import play.mvc.Action;
import play.mvc.Http;
import services.UserDAO;

import javax.inject.Inject;

/**
 * Created by kamil on 16.05.14.
 */
public class UserData extends Action.Simple {

    @Inject
    private UserDAO userDAO;

    public play.libs.F.Promise<play.mvc.SimpleResult> call(Http.Context ctx) throws Throwable {
        ctx.args.put("user", userDAO.findByUsername(ctx.session().get("username")));
        return delegate.call(ctx);
    }

    public static User current() {
        return (User)Http.Context.current().args.get("user");
    }

}
