package com.mszostok.domain;

import javax.persistence.*;

/**
 * @author mszostok
 */
@Entity
@Table(name = "user_roles", schema = "public")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="user_roles_id_user_role_seq")
    @SequenceGenerator(name="user_roles_id_user_role_seq", sequenceName="user_roles_id_user_role_seq", allocationSize=1)
    @Column(name = "id_user_role", nullable = false)
    private Integer idUserRoles;

    @Column(name = "role", nullable = false)
    private String role;

    @ManyToOne
    @JoinColumn(name = "users_id_user")
    private User user;

    public UserRole() {
    }

    public UserRole(User client, String role) {
        this.user = client;
        this.role = role;
    }

    public Integer getIdUserRoles() {
        return idUserRoles;
    }

    public void setIdUserRoles(Integer idUserRoles) {
        this.idUserRoles = idUserRoles;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRole userRole = (UserRole) o;

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
        return "UserRole{" +
                "user=" + user +
                ", idUserRoles=" + idUserRoles +
                ", role='" + role + '\'' +
                '}';
    }
}
