package models;

import javax.persistence.*;

import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import utils.AccountType;

import java.util.List;

@Entity
public class User extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5782263324591897377L;

	@Id
	@GeneratedValue
	private Long id;

    @Column

    private String fbId;
	
	@Required
	@Column(nullable = false)
	private String username;

    @Constraints.MinLength(6)
	@Required
	private String password;
	
	@Required
    @Constraints.Email
	@Column
	private String email;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="role_id")
	private Role role;

    @Column(nullable = false)
    private AccountType type = AccountType.NORMAL;

    @OneToMany(mappedBy="user")
    private List<Contact> contacts;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="image_id")
    private Image image;

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static Finder<Long, User> find = new Finder<Long, User>(
		    Long.class, User.class
	);

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }
	
}
