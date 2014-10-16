package models;

import javax.persistence.*;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model.Finder;

@Entity
public class Contact {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Required
	@Column(nullable = false)
	private String firstName;
	
	@Required
	@Column(nullable = false)
	private String lastName;

    @Column
	private String companyName;
	
	@Required
	@Column(nullable = false)
	private String email;
	
	@Required
	@Column(nullable = false)
	private String phoneNumber;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public static Finder<Long,Contact> find = new Finder<Long,Contact>(
		    Long.class, Contact.class
	); 
	
}
