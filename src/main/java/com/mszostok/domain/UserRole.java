package com.mszostok.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.base.MoreObjects;
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
@Table(name = "user_roles", schema = "public")
@Getter
@Setter
public final class UserRole {

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

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    UserRole userRole = (UserRole) obj;

    if (idUserRoles != null ? !idUserRoles.equals(userRole.idUserRoles) : userRole.idUserRoles != null)
      return false;
    if (role != null ? !role.equals(userRole.role) : userRole.role != null) return false;
    return user != null ? user.equals(userRole.user) : userRole.user == null;

  }

  @Override
  public int hashCode() {
    int result = idUserRoles != null ? idUserRoles.hashCode() : 0;
    result = 31 * result + (role != null ? role.hashCode() : 0);
    result = 31 * result + (user != null ? user.hashCode() : 0);
    return result;
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
