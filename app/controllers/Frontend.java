package controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import models.Role;
import models.User;

import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.Play;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Result;
import play.mvc.With;
import services.RoleDAO;
import services.UserDAO;
import utils.AccountType;
import utils.IsNotLogged;
import play.mvc.Controller;
import views.html.login;
import views.html.register;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Frontend extends Controller {

    @Inject
    private UserDAO userDAO;

    @Inject
    private RoleDAO roleDAO;

    public static class Login {
        @Constraints.Required
        String username;

        @Constraints.Required
        String password;

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }

        public String validate() {
            User userByName = User.find.where()
                    .eq("username", getUsername()).eq("type", AccountType.NORMAL)
                    .findUnique();
            if(userByName == null) {
                return "Użytkownik o podanym loginie nie istnieje!";
            } else {
                if(!BCrypt.checkpw(getPassword(), userByName.getPassword())) {
                    return "Błędne hasło!";

                }
            }
            return null;
        }
    }

    //TODO DAO?
    public static class RegisterForm extends User {

        public RegisterForm(){}

        public RegisterForm(User user) {
            this.setId(user.getId());
            this.setUsername(user.getUsername());
            this.setEmail(user.getEmail());
            this.setRole(user.getRole());
        }

        @Constraints.Required
        public String repeatPassword;

        public String getRepeatPassword() {
            return repeatPassword;
        }

        public void setRepeatPassword(String repeatPassword) {
            this.repeatPassword = repeatPassword;
        }

        public String validate() {
            User userByName = User.find.where()
                    .eq("username", getUsername())
                    .findUnique();

            User userByEmail = User.find.where()
                    .eq("email", getEmail())
                    .findUnique();

            if(!getPassword().equals(repeatPassword)) {
                return "Podano różne hasła!";
            } else if(userByName != null) {
                return "Użytkownik o podanym loginie istnieje już w bazie!";
            } else if(userByEmail != null) {
                return "Użytkownik o podanym emailu istnieje już w bazie!";
            }
            return null;
        }
    }

    @With(IsNotLogged.class)
    public Result createRegisterForm() {
        return ok(register.render(Form.form(RegisterForm.class), false));
    }

    @With(IsNotLogged.class)
    public Result registerUser() {
        Form<RegisterForm> registerForm = Form.form(RegisterForm.class).bindFromRequest();
        if(registerForm.hasErrors()) {
            return badRequest(register.render(registerForm, false));
        }
        User userFromForm = registerForm.get();
        User user = new User();
        user.setPassword(BCrypt.hashpw(userFromForm.getPassword(), BCrypt.gensalt()));
        user.setUsername(userFromForm.getUsername());
        user.setEmail(userFromForm.getEmail());
        Role role = roleDAO.findByName("user");
        user.setRole(role);
        user.save();
        flash("success", "Rejestracja powiodła się. Teraz możesz się zalogować");
        return redirect(
                routes.Frontend.login()
        );

    }
    @With(IsNotLogged.class)
    public Result login() {
		return ok(login.render((Form.form(Login.class))));
	}

    @With(IsNotLogged.class)
    public Result auth() {

        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        }

        session().clear();
        session("username", loginForm.get().getUsername());
        flash("success", "Logowanie udane");
        return redirect(
            routes.Contacts.index()
        );

    }


    public Result logout() {
        session().clear();
        flash("success", "Zostałeś wylogowany!");
        return redirect(
                routes.Frontend.login()
        );
    }


    public Result registerFbUser(String userId, String AuthToken) {
        FacebookClient facebookClient = new DefaultFacebookClient();

        String apiKey = Play.application().configuration().getString("facebook.apikey");
        String apiSecret = Play.application().configuration().getString("facebook.apisecret");
        FacebookClient.AccessToken accessToken = facebookClient.obtainAppAccessToken(apiKey,
                apiSecret);
        facebookClient = new DefaultFacebookClient(accessToken.getAccessToken(), apiSecret);
        FacebookClient.DebugTokenInfo debug = facebookClient.debugToken(AuthToken);

        if(!debug.isValid()) {
            Logger.info("Błędny token");
            return badRequest();
        }

        if(!debug.getUserId().equals(userId)) {
            Logger.info("Token nienależy do podanego użytkownika");
            return badRequest();
        }
        facebookClient = new DefaultFacebookClient(AuthToken);
        com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);

        String username;
        if(userDAO.findByFbId(fbUser.getId()) == null) {
            String email = fbUser.getEmail();
            User user = new User();
            user.setRole(roleDAO.findByName("user"));
            user.setEmail(email);
            username = (fbUser.getName() == null) ? fbUser.getId() : fbUser.getName();
            user.setUsername(fbUser.getName());
            user.setFbId(fbUser.getId());
            user.setType(AccountType.FACEBOOK);
            userDAO.create(user);
        } else {
            User user2 = userDAO.findByFbId(fbUser.getId());
            username = user2.getUsername();
        }
        session().clear();
        session("username", username);
        flash("success", "Logowanie udane");
        return ok(routes.Contacts.index().toString());
    }
	

}
