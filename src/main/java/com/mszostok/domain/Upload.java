package com.mszostok.domain;

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

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "uploads", schema = "public")
public class Upload {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "uploads_id_upload_seq")
  @SequenceGenerator(name = "uploads_id_upload_seq", sequenceName = "uploads_id_upload_seq", allocationSize = 1)
  @Column(name = "id_upload", nullable = false, unique = true)
  private int idUpload;

  @Column(name = "original_file_name", nullable = false, length = 255)
  private String originalFileName;

  @Column(name = "logic_type", nullable = false, length = 50)
  private String logicType;

  @Column(name = "ref_link", nullable = false, length = 255)
  private String refLink;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "competitions_id_competition")
  private Competition competition;

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("idUpload", idUpload)
      .add("originalFileName", originalFileName)
      .add("logicType", logicType)
      .add("refLink", refLink)
      .toString();
  }
}
