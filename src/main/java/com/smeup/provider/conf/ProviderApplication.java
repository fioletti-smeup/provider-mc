package com.smeup.provider.conf;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.smeup.provider.FunService;
import com.smeup.provider.LoginService;
import com.smeup.provider.LogoutService;
import com.smeup.provider.OpenAPI;
import com.smeup.provider.filter.AuthFilter;
import com.smeup.provider.mapper.AuthorizationExceptionMapper;
import com.smeup.provider.mapper.CommunicationExceptionMapper;
import com.smeup.provider.mapper.FunParseExceptionMapper;
import com.smeup.provider.mapper.NotFoundExceptionMapper;
import com.smeup.provider.mapper.RuntimeExceptionMapper;
import com.smeup.provider.mapper.TokenVerificationExceptionMapper;
import com.smeup.provider.mapper.XMLParseExceptionMapper;

@ApplicationPath(ProviderApplication.API_CONTEXT)
public class ProviderApplication extends javax.ws.rs.core.Application {

    public static final String API_CONTEXT = "";

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> resources = new HashSet<Class<?>>();

        resources.add(OpenAPI.class);
        resources.add(AuthFilter.class);
        resources.add(LoginService.class);
        resources.add(LogoutService.class);
        resources.add(FunService.class);
        resources.add(TokenVerificationExceptionMapper.class);
        resources.add(AuthorizationExceptionMapper.class);
        resources.add(CommunicationExceptionMapper.class);
        resources.add(FunParseExceptionMapper.class);
        resources.add(NotFoundExceptionMapper.class);
        resources.add(RuntimeExceptionMapper.class);
        resources.add(XMLParseExceptionMapper.class);

        return resources;
    }
}
