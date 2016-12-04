package com.mszostok.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.mszostok.domain.User;
import com.mszostok.domain.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
@Getter
@Setter
public class UserDto {
  public static final int PASSWORD_MIN_LENGTH = 4;
  public static final int PASSWORD_MAX_LENGTH = 100;

  @NotEmpty
  @Size(max = 50)
  private String username;

  @Size(max = 50)
  private String firstName;

  @Size(max = 50)
  private String lastName;

  @Email
  @NotEmpty
  @Size(min = 5, max = 100)
  private String email;

  @Size(max = 50)
  private String city;

  @Size(max = 50)
  private String country;

  @Size(max = 10)
  private String postalCode;

  @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private boolean activated;

  private Set<String> authorities;

  public UserDto() {
  }

  public UserDto(final User user) {
    this(user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getCity(),
      user.getCountry(), user.getPostalCode(), user.isActive(),
      user.getRoles().stream().map(UserRole::getRole)
        .collect(Collectors.toSet()));
  }

  public UserDto(final String username, final String firstName, final String lastName, final String email,
                 final String city, final String country, final String postalCode, final boolean activated, final Set<String> authorities) {

    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.activated = activated;
    this.authorities = authorities;
    this.city = city;
    this.country = country;
    this.postalCode = postalCode;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("username", username)
      .add("firstName", firstName)
      .add("lastName", lastName)
      .add("email", email)
      .add("city", city)
      .add("country", country)
      .add("postalCode", postalCode)
      .add("activated", activated)
      .add("authorities", authorities)
      .toString();
  }
}
