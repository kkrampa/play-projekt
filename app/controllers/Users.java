package controllers;

import models.Role;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import play.mvc.With;
import services.RoleDAO;
import services.UserDAO;
import utils.AccountType;
import utils.CheckRoleAnnotation;
import utils.UserData;
import views.html.listing;
import views.html.register;
import views.html.userrows;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

/**
 * Created by kamil on 13.05.14.
 */
@With(UserData.class)
@Security.Authenticated(Secured.class)
@Singleton
public class Users extends Controller {

    private final UserDAO userDAO;

    @Inject
    private RoleDAO roleDAO;

    @Inject
    public Users(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    //TODO Za dużo powtarzania się.
    public static class EditUserForm {
        @Constraints.Required
        private String username;

        @Constraints.Email
        @Constraints.Required
        private String email;


        private String password;
        private String repeatPassword;

        public UserDAO getUserDAO() {
            return userDAO;
        }

        public void setUserDAO(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        private UserDAO userDAO;


        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRepeatPassword() {
            return repeatPassword;
        }

        public void setRepeatPassword(String repeatPassword) {
            this.repeatPassword = repeatPassword;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String validate() {
            if(!getPassword().equals(repeatPassword)) {
                return "Podano różne hasła!";
            }
            return null;
        }

        public EditUserForm() {}

        public EditUserForm(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        public EditUserForm(User user) {
            setEmail(user.getEmail());
            setUsername(user.getUsername());
        }
    }

    public Result index() {
        User user = userDAO.findByUsername(request().username());
        EditUserForm editUserForm = new EditUserForm(user);
        editUserForm.setUserDAO(userDAO);
        Form<EditUserForm> registerForm = Form.form(EditUserForm.class).fill((editUserForm));
        return ok(register.render(registerForm, true));
    }

    public Result editProfile() {
        Form<EditUserForm> formFromRequest = Form.form(EditUserForm.class).bindFromRequest();
        User user = userDAO.findByUsername(request().username());
        if(user.getType() == AccountType.NORMAL) {
            if (formFromRequest.hasErrors()) {
                return badRequest(register.render(formFromRequest, true));
            }

            EditUserForm editUserForm = formFromRequest.get();
            user.setEmail(editUserForm.getEmail());
            if (!editUserForm.getPassword().equals("")) {
                user.setPassword(BCrypt.hashpw(editUserForm.getPassword(), BCrypt.gensalt()));
            }
            user.update();
        }
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart image = body.getFile("photo");

        if (image != null) {
            try {
                File file = image.getFile();
                Image img = ImageIO.read(file);
                if (img == null) {
                    flash("error", "Podany plik nie jest obrazkem");
                    return Results.redirect(controllers.routes.Users.index());
                }
                if (img.getHeight(null) > 400 || img.getWidth(null) > 400) {
                    flash("error", "Maksymalna wielkość obrazka to: 400 x 400");
                    return Results.redirect(controllers.routes.Users.index());
                }
                if (user.getImage() == null) {
                    models.Image im = new models.Image();
                    im.setContent(com.google.common.io.Files.toByteArray(file));
                    im.setContentType(image.getContentType());
                    im.save();
                    user.setImage(im);
                    user.update();
                } else {
                    models.Image im = user.getImage();
                    im.setContent(com.google.common.io.Files.toByteArray(file));
                    im.setContentType(image.getContentType());
                    im.update();
                }

            } catch (IOException ioe) {
                Logger.error(ioe.getMessage());
            }
        }
        return Results.redirect(controllers.routes.Users.index());
    }

    @CheckRoleAnnotation("admin")
    public Result changeRole(Long userId, Long roleId) {
        User user = userDAO.get(userId);
        if(user.getUsername().equals(request().username())) {
            return badRequest("Nie możesz zmienić roli samemu sobie!");
        }
        Role role = roleDAO.getRole(roleId);
        user.setRole(role);
        user.update();
        return ok("Rola została zmieniona");
    }

    @CheckRoleAnnotation("admin")
    public Result listAllUsers() {
        return ok(listing.render(userrows.render(userDAO.findAll(), roleDAO.getAll()), "Lista użytkowników"));
    }

    public Result renderImage(Long id) {
        models.Image image = models.Image.FINDER.byId(id);
        if(image == null)
            return badRequest();
        return ok(image.getContent()).as(image.getContentType());
    }
}
