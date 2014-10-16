package utils;

import models.User;
import play.mvc.SimpleResult;
import play.mvc.Action;
import play.mvc.Http;

public class CheckRole extends Action<CheckRoleAnnotation>{

    public play.libs.F.Promise<play.mvc.SimpleResult> call(Http.Context ctx) throws Throwable {
        //TODO Zastanowić się, czy jest sens sprawdzania tego.
        if(!ctx.session().containsKey("username")) {
            return play.libs.F.Promise.pure(redirect(controllers.routes.Frontend.login()));
        }
        User user = User.find.where().eq("username", ctx.session().get("username")).findUnique();
        if(user == null)
            return play.libs.F.Promise.pure(redirect(controllers.routes.Frontend.login()));

        //TODO przydałaby się lepsza strona forbidden
        if(user.getRole() == null)
            return play.libs.F.Promise.pure((SimpleResult)forbidden("Brak dostępu!"));
        if(!configuration.value().equals(user.getRole().getName()))
            return play.libs.F.Promise.pure((SimpleResult)forbidden("Brak dostępu!"));
        return delegate.call(ctx);
    }
}