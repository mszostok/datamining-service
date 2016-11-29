package com.mszostok.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

  @OneToMany(cascade = ALL, mappedBy = "user")
  @JsonManagedReference
  private List<UserRole> roles;

  @OneToMany(mappedBy = "user", cascade = ALL, fetch = LAZY)
  private List<Competition> competitions;

  @Column(name = "is_active", nullable = false)
  private boolean active;


}
