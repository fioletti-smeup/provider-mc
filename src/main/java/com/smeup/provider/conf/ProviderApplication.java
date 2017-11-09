package com.smeup.provider.conf;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.smeup.provider.Provider;
import com.smeup.provider.mapper.FunParseExceptionMapper;
import com.smeup.provider.mapper.NotFoundExceptionMapper;
import com.smeup.provider.mapper.RuntimeExceptionMapper;

@ApplicationPath("/")
public class ProviderApplication extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(Provider.class);
        s.add(RuntimeExceptionMapper.class);
        s.add(NotFoundExceptionMapper.class);
        s.add(FunParseExceptionMapper.class);
        return s;
    }
}
