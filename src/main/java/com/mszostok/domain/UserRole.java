package com.mszostok.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.base.MoreObjects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * User role (authorities which have) entity model.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "user_roles", schema = "public")
public class UserRole {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "user_roles_id_user_role_seq")
  @SequenceGenerator(name = "user_roles_id_user_role_seq", sequenceName = "user_roles_id_user_role_seq", allocationSize = 1)
  @Column(name = "id_user_role", nullable = false)
  private Integer idUserRoles;

  @Column(name = "role", nullable = false)
  private String role;

  @ManyToOne
  @JoinColumn(name = "users_id_user")
  @JsonBackReference
  private User user;

  public UserRole() {
  }

  public UserRole(final String role, final User user) {
    this.role = role;
    this.user = user;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("idUserRoles", idUserRoles)
      .add("role", role)
      .add("user", user)
      .toString();
  }
}
