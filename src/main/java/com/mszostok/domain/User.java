package com.mszostok.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.SEQUENCE;

/**
 * User entity model.
 */
@Entity
@Getter
@Setter
@Table(name = "users", schema = "public")
public final class User {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "users_id_user_seq")
  @SequenceGenerator(name = "users_id_user_seq", sequenceName = "users_id_user_seq", allocationSize = 1)
  @Column(name = "id_user", nullable = false, unique = true)
  private Integer idUser;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "country", nullable = false)
  private String country;


  @Column(name = "postal_code", nullable = false)
  private String postalCode;

  @OneToMany(fetch = EAGER, cascade = ALL, mappedBy = "user")
  @JsonManagedReference
  private List<UserRole> roles;

  @Column(name = "is_active", nullable = false)
  private boolean active;

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    User user = (User) obj;

    if (active != user.active) return false;
    if (idUser != null ? !idUser.equals(user.idUser) : user.idUser != null) return false;
    if (username != null ? !username.equals(user.username) : user.username != null) return false;
    if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
    if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
    if (email != null ? !email.equals(user.email) : user.email != null) return false;
    if (password != null ? !password.equals(user.password) : user.password != null) return false;
    return roles != null ? roles.equals(user.roles) : user.roles == null;

  }

  @Override
  public int hashCode() {
    int result = idUser != null ? idUser.hashCode() : 0;
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
    result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
    result = 31 * result + (email != null ? email.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    result = 31 * result + (roles != null ? roles.hashCode() : 0);
    result = 31 * result + (active ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("idUser", idUser)
      .add("username", username)
      .add("firstName", firstName)
      .add("lastName", lastName)
      .add("email", email)
      .add("password", password)
      .add("roles", roles)
      .add("active", active)
      .toString();
  }
}
