package com.microservices.demo.elastic.query.service.security;

import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Component
public class QueryServicePermissionEvaluator implements PermissionEvaluator {

    public static final Logger LOG = LoggerFactory.getLogger(QueryServicePermissionEvaluator.class);

    private static final String SUPER_USER_ROLE = "APP_SUPER_USER_ROLE";
    private final HttpServletRequest httpServletRequest;

    public QueryServicePermissionEvaluator(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomain,
                                 Object permission) {
        LOG.info("QueryServicePermissionEvaluator | hasPermission | method init");
        if(isSuperUser()){
            LOG.info("QueryServicePermissionEvaluator | hasPermission | isSuperUser: true");
            return true;
        }
        if(targetDomain instanceof ElasticQueryServiceRequestModel){
            LOG.info("QueryServicePermissionEvaluator | hasPermission | targetDomain: ElasticQueryServiceRequestModel");
            return preAuthorize(authentication, ((ElasticQueryServiceRequestModel)targetDomain).getId(),permission);
        } else if(targetDomain instanceof ResponseEntity || targetDomain == null){
            if(targetDomain == null) {
                LOG.info("QueryServicePermissionEvaluator | hasPermission | targetDomain: null");
                return true;
            }
            ElasticQueryServiceAnalyticsResponseModel responseBody =
                    ((ResponseEntity<ElasticQueryServiceAnalyticsResponseModel>) targetDomain).getBody();
            Objects.requireNonNull(responseBody);
            return postAuthorize(authentication,responseBody.getQueryResponseModels(),permission);
        }
        return false;
    }

    private boolean isSuperUser() {
        return httpServletRequest.isUserInRole(SUPER_USER_ROLE);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        if(isSuperUser()){
            return true;
        }
        if(targetId == null){
            return false;
        }
        return preAuthorize(authentication,(String) targetId, permission);
    }

    private boolean preAuthorize(Authentication authentication, String id, Object permission) {
        LOG.info("QueryServicePermissionEvaluator | preAuthorize | method init");
        TwitterQueryUser twitterQueryUser = (TwitterQueryUser) authentication.getPrincipal();
        PermissionType userPermission = twitterQueryUser.getPermissions().get(id);
        return hasPermission((String) permission,userPermission);
    }

    private boolean postAuthorize(Authentication authentication,
                                  List<ElasticQueryServiceResponseModel> responseBody,
                                  Object permission) {
        LOG.info("QueryServicePermissionEvaluator | postAuthorize | method init");
        TwitterQueryUser twitterQueryUser = (TwitterQueryUser) authentication.getPrincipal();
        for (ElasticQueryServiceResponseModel responseModel : responseBody){
            PermissionType userPermission = twitterQueryUser.getPermissions().get(responseModel.getId());
            if(!hasPermission((String)permission, userPermission)){
                LOG.info("QueryServicePermissionEvaluator | postAuthorize | postAuthorize: false");
                return false;
            }
        }
        LOG.info("QueryServicePermissionEvaluator | postAuthorize | postAuthorize: true");
        return true;
    }

    public boolean hasPermission(String requiredPermission, PermissionType userPermission){
        return userPermission != null && requiredPermission.equals(userPermission.getType());
    }


}
