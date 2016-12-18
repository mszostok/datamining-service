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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "participation_history", schema = "public")
public class Submission {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "participation_history_id_participation_history_seq")
  @SequenceGenerator(name = "participation_history_id_participation_history_seq", sequenceName = "participation_history_id_participation_history_seq", allocationSize = 1)
  @Column(name = "id_participation_history", nullable = false, unique = true)
  private int idParticipationHistory;

  @Column(name = "original_file_name", nullable = false, length = 255)
  private String originalFileName;

  @Column(name = "file_ref_link", nullable = false, length = 255)
  private String fileRefLink;

  @Column(name = "score", nullable = false)
  private Double score;

  @Column(name = "take_date", nullable = false)
  private DateTime takeDate;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "participation_id_participation")
  private Participation participation;

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("idParticipationHistory", idParticipationHistory)
      .add("originalFileName", originalFileName)
      .add("fileRefLink", fileRefLink)
      .add("score", score)
      .add("takeDate", takeDate)
      .toString();
  }
}
