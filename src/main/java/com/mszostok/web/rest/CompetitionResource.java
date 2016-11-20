package com.mszostok.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mszostok.service.CompetitionService;
import com.mszostok.service.DescriptionService;
import com.mszostok.service.StorageService;
import com.mszostok.web.dto.CompetitionCollectionDto;
import com.mszostok.web.dto.CompetitionDto;
import com.mszostok.web.dto.CompetitionGeneralInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping(value = "/competitions")
public class CompetitionResource {

  @Autowired
  private StorageService storageService;

  @Autowired
  private CompetitionService competitionService;

  @Autowired
  private DescriptionService descriptionService;

  @PostConstruct
  public void init() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JodaModule());
  }

  @PostMapping
  public ResponseEntity<?> saveCompetition(@Valid @RequestBody final CompetitionDto competitionDto) {
    Integer id = competitionService.save(competitionDto);
    return new ResponseEntity<>(Collections.singletonMap("competitionId", id), CREATED);
  }

  @GetMapping("/active")
  public Collection<CompetitionCollectionDto> getAllActiveCompetitions() {
    //TODO: sort, pageable
    return competitionService.getAllActiveCompetitions();
  }

  @GetMapping("/{id}/general-info")
  public CompetitionGeneralInfoDto getGeneralInfoForCompetition(@PathVariable("id") final Integer competitionId) {
    CompetitionGeneralInfoDto generalInfoFor = competitionService.getGeneralInfoFor(competitionId);
    System.out.println(generalInfoFor.getStartDate());
    return generalInfoFor;
  }

  @PostMapping("/{id}/dataset/testing")
  @ResponseStatus(CREATED)
  public void handleTestingFileUpload(@RequestParam("file") final MultipartFile file,
                                      @PathVariable("id") final Integer competitionId) {
    storageService.storeTestingFile(file, competitionId);
  }

  @PostMapping("/{id}/dataset/training")
  @ResponseStatus(CREATED)
  public void handleTrainingFileUpload(@RequestParam("file") final MultipartFile file,
                                       @PathVariable("id") final Integer competitionId) {
    storageService.storeTrainingFile(file, competitionId);
  }

  @GetMapping("/{id}/description/introduction")
  public ResponseEntity<?> getIntroductionForCompetition(@PathVariable("id") final Integer competitionId) {
    String text = descriptionService.getIntroductionFor(competitionId);
    return new ResponseEntity<>(Collections.singletonMap("text", text), OK);
  }

  @GetMapping("/{id}/description/formula")
  public ResponseEntity<?> getFormulaForCompetition(@PathVariable("id") final Integer competitionId) {
    String text = descriptionService.getFormulaFor(competitionId);
    return new ResponseEntity<>(Collections.singletonMap("text", text), OK);
  }


  @GetMapping("/{id}/description/dataset")
  public ResponseEntity<?> getDatasetDescriptionForCompetition(@PathVariable("id") final Integer competitionId) {
    String text = descriptionService.getDatasetFor(competitionId);
    return new ResponseEntity<>(Collections.singletonMap("text", text), OK);
  }

  @GetMapping("/{id}/dataset/training")
  public ResponseEntity<Resource> serveTrainingFile(@PathVariable final Integer id) {
    Resource file = storageService.loadTrainingFileAsResource(id);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
      .body(file);
  }

  @GetMapping("/{id}/dataset/testing")
  public ResponseEntity<Resource> serveTestingFile(@PathVariable final Integer id) {
    Resource file = storageService.loadTestingFileAsResource(id);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
      .body(file);
  }

}
