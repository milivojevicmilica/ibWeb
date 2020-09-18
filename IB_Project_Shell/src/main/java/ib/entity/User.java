package ib.entity;

import java.sql.Timestamp;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;




@Entity
@Table(name="User_Tbl")
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "username", unique = true, nullable = false)
	private String username;
	
	//@JsonIgnore
	@Column(name = "password", unique = false, nullable = false)
	private String password;
	
	@Column(name = "enabled", unique = false, nullable = false)
	private boolean enabled;
	
	@Column(name = "email", unique = false, nullable = true)
	private String email;
	
	
	@JsonIgnore
	@Column(name = "last_password_reset_date")
	//@Temporal(TemporalType.TIMESTAMP)
    private Timestamp lastPasswordResetDate;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "authorities_users", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private List<Authority> authorities; 
	
	public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }


	public User() {
		
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
	        Timestamp now = new Timestamp(new Date().getTime());
	        this.setLastPasswordResetDate(now);
	        this.password = password;
	    }

	

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	

	public Timestamp getLastPasswordResetDate() {
		return lastPasswordResetDate;
	}



	public void setLastPasswordResetDate(Timestamp lastPasswordResetDate) {
		this.lastPasswordResetDate = lastPasswordResetDate;
	}


	
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + "]";
	}
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
	
	@JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @JsonIgnore
    public String getAuthoritiesAsString() {
    	StringBuilder sb = new StringBuilder();
    	
    	for (Authority authority : this.authorities) {
    		sb.append(authority.getName() + " ");
    	}
    	
    	return sb.toString();
}
}
