import com.google.inject.Guice;
import com.google.inject.Injector;
import models.Role;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import services.PlayModule;
import utils.AccountType;

/**
 * Created by kamil on 13.05.14.
 */
public class Global extends GlobalSettings {
    private Application application;
    private Injector injector;

    @Override
    public void onStart(Application application) {
        this.application = application;
        injector = Guice.createInjector(new PlayModule(application));

        if(Role.find.where().eq("name", "user").findUnique() == null) {
            Logger.info("Brak roli user. Tworzenie...");
            Role role = new Role();
            role.setName("user");
            role.setId(1L);
            role.save();
            Logger.info("Utworzono rolę user");
        }


        if(Role.find.where().eq("name", "admin").findUnique() == null) {
            Logger.info("Brak roli admin. Tworzenie...");
            Role role = new Role();
            role.setName("admin");
            role.setId(2L);
            role.save();
            Logger.info("Utworzono rolę admin");
        }


        if(User.find.where().eq("username", "admin").findUnique() == null) {
            Logger.info("Brak użytkownika admin. Tworzenie...");
            User user = new User();
            user.setUsername("admin");
            user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
            user.setEmail("admin@op.pl");
            user.setRole(Role.find.byId(2L));
            user.setType(AccountType.NORMAL);
            user.save();
            Logger.info("Utworzono użytkownika admin.");
        }


    }

    @Override
    public void onStop(Application application) {
        this.application = null;
        this.injector = null;
    }

    @Override
    public <A> A getControllerInstance(Class<A> aClass) throws Exception {
        return injector.getInstance(aClass);
    }
}
