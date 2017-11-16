package com.smeup.provider.conf;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.smeup.provider.AuthFilter;
import com.smeup.provider.FunProvider;
import com.smeup.provider.LoginProvider;
import com.smeup.provider.LogoutProvider;
import com.smeup.provider.mapper.CommunicationExceptionMapper;
import com.smeup.provider.mapper.FunParseExceptionMapper;
import com.smeup.provider.mapper.NotFoundExceptionMapper;
import com.smeup.provider.mapper.RuntimeExceptionMapper;
import com.smeup.provider.mapper.XMLParseExceptionMapper;

@ApplicationPath("/")
public class ProviderApplication extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(AuthFilter.class);
        s.add(LoginProvider.class);
        s.add(LogoutProvider.class);
        s.add(FunProvider.class);
        s.add(CommunicationExceptionMapper.class);
        s.add(FunParseExceptionMapper.class);
        s.add(NotFoundExceptionMapper.class);
        s.add(RuntimeExceptionMapper.class);
        s.add(XMLParseExceptionMapper.class);
        return s;
    }
}
