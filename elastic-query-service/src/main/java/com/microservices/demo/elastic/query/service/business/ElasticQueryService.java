package com.microservices.demo.elastic.query.service.business;

import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;

import java.util.List;

public interface ElasticQueryService {

    ElasticQueryServiceResponseModel getDocumentById(String id);

//    List<ElasticQueryServiceResponseModel> getDocumentByText(String text);

    ElasticQueryServiceAnalyticsResponseModel getDocumentByText(String text, String accessToken);

    List<ElasticQueryServiceResponseModel> getAllDocuments();

    void deleteDocumentById(String id);

    List<ElasticQueryServiceResponseModel> deleteDocumentsByText(String text);
}
