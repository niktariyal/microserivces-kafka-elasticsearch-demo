package com.microservices.demo.elastic.query.service.api;

import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/documents/v2")
public class ElasticDocumentControllerV2 {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentControllerV2.class);

    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentControllerV2(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @GetMapping("/")
    public ResponseEntity<List<ElasticQueryServiceResponseModelV2>> getAllDocuments(){
        List<ElasticQueryServiceResponseModelV2> response =
                getModelsResponseV2(elasticQueryService.getAllDocuments());
        LOG.info("Elasticsearch returned {} of documents",response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentById(@PathVariable @NotEmpty String id){
        ElasticQueryServiceResponseModelV2 elasticQueryServiceResponseModelV2
                =getModelResponseV2(elasticQueryService.getDocumentById(id));
        LOG.info("Elasticsearch returned document with id {}", id);
        return ResponseEntity.ok(elasticQueryServiceResponseModelV2);
    }

    @PostMapping("/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModelV2>> getDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel){
        List<ElasticQueryServiceResponseModelV2> response =
                getModelsResponseV2(elasticQueryService.getDocumentByText(elasticQueryServiceRequestModel.getText()));
        LOG.info("Elasticsearch returned {} of documents", response.size());
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

        return null;
    }

    public ElasticQueryServiceResponseModelV2 getModelResponseV2(
            ElasticQueryServiceResponseModel responseModel){
        ElasticQueryServiceResponseModelV2 responseModelV2 =
                ElasticQueryServiceResponseModelV2.builder()
                    .id(Long.parseLong(responseModel.getId()))
                    .text(responseModel.getText())
                    .userId(responseModel.getUserId())
                    .createdAt(responseModel.getCreatedAt())
                    .build();
        responseModelV2.add(responseModel.getLinks());
        return responseModelV2;
    }

    public List<ElasticQueryServiceResponseModelV2> getModelsResponseV2(
            List<ElasticQueryServiceResponseModel> responseModels){
        return responseModels.stream().map(this::getModelResponseV2).collect(Collectors.toList());
    }
}
