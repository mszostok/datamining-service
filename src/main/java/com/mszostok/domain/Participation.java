package com.mszostok.domain;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
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

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    Participation that = (Participation) obj;

    if (idParticipation != that.idParticipation) return false;
    if (Double.compare(that.lastScore, lastScore) != 0) return false;
    if (Double.compare(that.bestScore, bestScore) != 0) return false;
    if (takeNumber != null ? !takeNumber.equals(that.takeNumber) : that.takeNumber != null) return false;
    if (lastTakeDate != null ? !lastTakeDate.equals(that.lastTakeDate) : that.lastTakeDate != null) return false;
    if (user != null ? !user.equals(that.user) : that.user != null) return false;
    return competition != null ? competition.equals(that.competition) : that.competition == null;

  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = idParticipation;
    temp = Double.doubleToLongBits(lastScore);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(bestScore);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (takeNumber != null ? takeNumber.hashCode() : 0);
    result = 31 * result + (lastTakeDate != null ? lastTakeDate.hashCode() : 0);
    result = 31 * result + (user != null ? user.hashCode() : 0);
    result = 31 * result + (competition != null ? competition.hashCode() : 0);
    return result;
  }
}
