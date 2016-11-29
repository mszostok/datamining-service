package com.mszostok.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mszostok.enums.FileLogicType;
import com.mszostok.service.CompetitionService;
import com.mszostok.service.DescriptionService;
import com.mszostok.service.ScoreComputationService;
import com.mszostok.service.StorageService;
import com.mszostok.web.dto.CompetitionCollectionDto;
import com.mszostok.web.dto.CompetitionDto;
import com.mszostok.web.dto.CompetitionGeneralInfoDto;
import com.mszostok.web.dto.ScoreFnDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequestMapping(value = "/competitions")
@Api(value = "/competitions", description = "Operations about competitions")
public class CompetitionResource {

  private final StorageService storageService;

  private final CompetitionService competitionService;

  private final DescriptionService descriptionService;

  @Autowired
  public CompetitionResource(final StorageService storageService, final CompetitionService competitionService,
                             final DescriptionService descriptionService) {
    Assert.notNull(storageService, "Storage Svc must not be null");
    Assert.notNull(competitionService, "Competition Svc must not be null");
    Assert.notNull(descriptionService, "Description Svc must not be null");
    this.storageService = storageService;
    this.competitionService = competitionService;
    this.descriptionService = descriptionService;
  }

  @PostConstruct
  public void init() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JodaModule());
  }

  @ApiOperation(value = "Get all active competition", response = CompetitionCollectionDto.class, responseContainer = "List")
  @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @ResponseStatus(OK)
  @RequestMapping(value = "/active", method = GET)
  public Collection<CompetitionCollectionDto> getAllActiveCompetitions() {
    //TODO: sort, pageable
    return competitionService.getAllActiveCompetitions();
  }

  @ApiOperation(value = "Get general info for requested competition")
  @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @ResponseStatus(OK)
  @RequestMapping(value = "/{id}/general-info", method = GET)
  public CompetitionGeneralInfoDto getGeneralInfoForCompetition(@PathVariable("id") final Integer competitionId) {
    CompetitionGeneralInfoDto generalInfoFor = competitionService.getGeneralInfoFor(competitionId);
    System.out.println(generalInfoFor.getStartDate());
    return generalInfoFor;
  }

  @ApiOperation(value = "Save competition")
  @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @RequestMapping(method = POST)
  public ResponseEntity<?> saveCompetition(@Valid @RequestBody final CompetitionDto competitionDto) {
    Integer id = competitionService.save(competitionDto);
    return new ResponseEntity<>(Collections.singletonMap("competitionId", id), CREATED);
  }


  @ApiOperation(value = "Upload testing file for selected competition")
  @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @ResponseStatus(CREATED)
  @RequestMapping(value = "/{id}/dataset/testing", method = POST)
  public void handleTestingFileUpload(@RequestParam("file") @RequestPart final MultipartFile file,
                                      @PathVariable("id") final Integer competitionId) {
    storageService.storeTestingFile(file, competitionId);
  }

  @ApiOperation(value = "Upload training file for selected competition")
  @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @ResponseStatus(CREATED)
  @RequestMapping(value = "/{id}/dataset/training", method = POST)
  public void handleTrainingFileUpload(@RequestParam("file") @RequestPart final MultipartFile file,
                                       @PathVariable("id") final Integer competitionId) {
    storageService.storeTrainingFile(file, competitionId);
  }

  @ApiOperation(value = "Get introduction description for requested competition")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @RequestMapping(value = "/{id}/description/introduction", method = GET)
  public ResponseEntity<?> getIntroductionForCompetition(@PathVariable("id") final Integer competitionId) {
    String text = descriptionService.getIntroductionFor(competitionId);
    return new ResponseEntity<>(Collections.singletonMap("text", text), OK);
  }

  @ApiOperation(value = "Get formula description for requested competition")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @RequestMapping(value = "/{id}/description/formula", method = GET)
  public ResponseEntity<?> getFormulaForCompetition(@PathVariable("id") final Integer competitionId) {
    String text = descriptionService.getFormulaFor(competitionId);
    return new ResponseEntity<>(Collections.singletonMap("text", text), OK);
  }

  @ApiOperation(value = "Get dataset description for requested competition")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @RequestMapping(value = "/{id}/description/dataset", method = GET)
  public ResponseEntity<?> getDatasetDescriptionForCompetition(@PathVariable("id") final Integer competitionId) {
    String text = descriptionService.getDatasetFor(competitionId);
    return new ResponseEntity<>(Collections.singletonMap("text", text), OK);
  }

  @ApiOperation(value = "Download training file for requested competition")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @RequestMapping(value = "/{id}/dataset/training", method = GET)
  public ResponseEntity<Resource> serveTrainingFile(@PathVariable final Integer id) {
    Resource file = storageService.loadFileAsResource(id, FileLogicType.TRAINING);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
      .body(file);
  }

  @ApiOperation(value = "Download testing file for requested competition")
  @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @RequestMapping(value = "/{id}/dataset/testing", method = GET)
  public ResponseEntity<Resource> serveTestingFile(@PathVariable final Integer id) {
    Resource file = storageService.loadFileAsResource(id, FileLogicType.TESTING);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
      .body(file);

  }

  @ApiOperation(value = "Send submission file for active competition")
  @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @ResponseStatus(NO_CONTENT)
  @RequestMapping(value = "/{id}/submission", method = POST)
  public void postSubmission(@RequestParam("file") @RequestPart final MultipartFile file,
                             @PathVariable("id") final Integer competitionId) {
    competitionService.processSubmission(file, competitionId);
  }

  @ApiOperation(value = "Get all available score functions", response = ScoreFnDto.class, responseContainer = "List")
  @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Something went wrong in Server")
      })
  @RequestMapping(value = "/score-functions", method = GET)
  public Collection<ScoreFnDto> getAllScoreFunctions() {
    return EnumSet.allOf(ScoreComputationService.ScoreFunctionType.class)
      .stream().map(ScoreFnDto::new)
      .collect(Collectors.toList());
  }
}
