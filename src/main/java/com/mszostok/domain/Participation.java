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
@Table(name = "participation", schema = "public")
public class Participation {
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "participation_id_participation_seq")
  @SequenceGenerator(name = "participation_id_participation_seq", sequenceName = "participation_id_participation_seq", allocationSize = 1)
  @Column(name = "id_participation", nullable = false)
  private int idParticipation;
  @Column(name = "last_score", nullable = false, precision = 0)
  private double lastScore;
  @Column(name = "best_score", nullable = false, precision = 0)
  private double bestScore;
  @Column(name = "take_number", nullable = true)
  private Integer takeNumber;
  @Column(name = "last_take_date", nullable = false)
  private DateTime lastTakeDate;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "users_id_user")
  private User user;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "competitions_id_competition")
  private Competition competition;

  @OneToMany(mappedBy = "participation", cascade = ALL, fetch = LAZY)
  private List<Submission> participationHistories;

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("idParticipation", idParticipation)
      .add("lastScore", lastScore)
      .add("bestScore", bestScore)
      .add("takeNumber", takeNumber)
      .add("lastTakeDate", lastTakeDate)
      .toString();
  }
}
