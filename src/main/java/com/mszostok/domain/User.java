package com.mszostok.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.base.MoreObjects;
import lombok.EqualsAndHashCode;
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
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

/**
 * User entity model.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode
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

  @Column(name = "is_active", nullable = false)
  private boolean active;

  @Column(name = "is_deleted", nullable = false)
  private boolean deleted;

  @OneToMany(cascade = ALL, mappedBy = "user")
  @JsonManagedReference
  private List<UserRole> roles;

  @OneToMany(mappedBy = "user", cascade = ALL, fetch = LAZY)
  private List<Competition> competitions;

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("idUser", idUser)
      .add("username", username)
      .add("firstName", firstName)
      .add("lastName", lastName)
      .add("email", email)
      .add("password", password)
      .add("city", city)
      .add("country", country)
      .add("postalCode", postalCode)
      .add("roles", roles)
      .add("isActive", active)
      .add("isDeleted", deleted)
      .toString();
  }
}
