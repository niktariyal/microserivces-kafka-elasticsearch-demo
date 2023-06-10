package com.microservices.demo.elastic.query.service.api;

import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import com.microservices.demo.elastic.query.service.security.TwitterQueryUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@PreAuthorize("isAuthenticated()")      //oauth2
@RestController
@RequestMapping(value = "/documents")
public class ElasticDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);

    private final ElasticQueryService elasticQueryService;

    @Value("${server.port}")
    private String port;

    public ElasticDocumentController(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    @Operation(summary = "Get all elastic documents.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.",content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400",description = "Not found."),
            @ApiResponse(responseCode = "500",description = "Internal server error.")
    })
    @GetMapping("")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments(){
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        LOG.info("Elasticsearch returned {} of documents",response.size());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasPermission(#id, 'ElasticQueryServiceResponseModel','READ')")
    @Operation(summary = "Get elastic document by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.",content = {
                    @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400",description = "Not found."),
            @ApiResponse(responseCode = "500",description = "Internal server error.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id){
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel
                = elasticQueryService.getDocumentById(id);
        LOG.info("Elasticsearch returned document with id {}", id);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

//    @PreAuthorize("hasRole('APP_USER_ROLE') || hasRole('APP_SUPER_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")  //oauth2 at method level
//    @PostAuthorize("hasPermission(returnObject, 'READ')")
//    @Operation(summary = "Get elastic document by text.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successful response.",content = {
//                    @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
//                    )
//            }),
//            @ApiResponse(responseCode = "400",description = "Not found."),
//            @ApiResponse(responseCode = "500",description = "Internal server error.")
//    })
//    @PostMapping("/get-document-by-text")
//    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(
//            @RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel){
//        List<ElasticQueryServiceResponseModel> response =
//                elasticQueryService.getDocumentByText(elasticQueryServiceRequestModel.getText());
//        LOG.info("Elasticsearch returned {} of documents on port {}", response.size(),port);
//        return ResponseEntity.ok(response);
//    }

    @PreAuthorize("hasRole('APP_USER_ROLE') || hasRole('APP_SUPER_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")  //oauth2 at method level
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    @Operation(summary = "Get elastic document by text.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.",content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400",description = "Not found."),
            @ApiResponse(responseCode = "500",description = "Internal server error.")
    })
    @PostMapping("/get-document-by-text")
    public ResponseEntity<ElasticQueryServiceAnalyticsResponseModel> getDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel,
            @AuthenticationPrincipal TwitterQueryUser principal,
            @RegisteredOAuth2AuthorizedClient("keycloak")OAuth2AuthorizedClient oAuth2AuthorizedClient){
        LOG.info("User {} querying documents for text {}",principal.getUsername(),
                elasticQueryServiceRequestModel.getText());
        ElasticQueryServiceAnalyticsResponseModel response =
                elasticQueryService.getDocumentByText(elasticQueryServiceRequestModel.getText(),
                        oAuth2AuthorizedClient.getAccessToken().getTokenValue());
        LOG.info("Elasticsearch returned {} of documents on port {}",
                response.getQueryResponseModels().size(),port);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocumentById(@PathVariable @NotEmpty String id){
        elasticQueryService.deleteDocumentById(id);
        LOG.info("Document with Id {} deleted successfully", id);
        return ResponseEntity.ok("Document delete successfully");
    }

    @PostMapping("/delete-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> deleteDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel){
        //implement logic
        return null;
    }

    private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel responseModel){
        ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.parseLong(responseModel.getId()))
                .userId(responseModel.getUserId())
                .text(responseModel.getText())
                .text2("Version 2 text")
                .build();
        responseModelV2.add(responseModel.getLinks());
        return responseModelV2;
    }
}
