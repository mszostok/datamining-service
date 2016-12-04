package com.mszostok.domain;

import com.google.common.base.MoreObjects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "competitions", schema = "public")
public class Competition {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "competitions_id_competition_seq")
  @SequenceGenerator(name = "competitions_id_competition_seq", sequenceName = "competitions_id_competition_seq", allocationSize = 1)
  @Column(name = "id_competition", nullable = false, unique = true)
  private int idCompetition;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "score_fn_id", nullable = false, length = 50)
  private Integer scoreFnId;

  @Column(name = "short_desc", nullable = false, length = 255)
  private String shortDesc;

  @Column(name = "start_date", nullable = false)
  private DateTime startDate;

  @Column(name = "end_date", nullable = false)
  private DateTime endDate;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "users_id_user")
  private User user;

  @OneToMany(mappedBy = "competition", cascade = ALL, fetch = LAZY)
  private List<Upload> uploads;

  @OneToOne(mappedBy = "competition")
  private Description description;

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("idCompetition", idCompetition)
      .add("name", name)
      .add("scoreFnId", scoreFnId)
      .add("shortDesc", shortDesc)
      .add("startDate", startDate)
      .add("endDate", endDate)
      .toString();
  }
}
