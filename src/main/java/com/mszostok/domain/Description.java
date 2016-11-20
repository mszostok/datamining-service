package com.mszostok.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "descriptions", schema = "public")
@Getter
@Setter
public class Description {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "descriptions_id_description_seq")
  @SequenceGenerator(name = "descriptions_id_description_seq", sequenceName = "descriptions_id_description_seq", allocationSize = 1)
  @Column(name = "id_description", nullable = false, unique = true)
  private int idDescription;

  @Column(name = "competition", nullable = false)
  private String introduction;

  @Column(name = "formula", nullable = false)
  private String formula;

  @Column(name = "dataset", nullable = false)
  private String dataset;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "competitions_id_competition")
  private Competition competition;

}
