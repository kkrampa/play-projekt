package utils;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

/**
 * Created by kamil on 13.05.14.
 */
public class IsNotLogged extends Action.Simple {


    @Override
    public F.Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
        if(ctx.session().containsKey("username")) {
            return play.libs.F.Promise.pure(redirect(controllers.routes.Contacts.index()));
        }
        return delegate.call(ctx);
    }
}
