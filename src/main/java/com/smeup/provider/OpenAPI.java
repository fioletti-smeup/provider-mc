package com.smeup.provider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.smeup.provider.conf.ProviderApplication;
import com.smeup.provider.log.Logged;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@ApplicationScoped
@Path("openapi.yaml")
@Produces(MediaType.TEXT_PLAIN)
@Logged
public class OpenAPI {

    private Template template;

    @Inject
    private ServletContext servletContext;

    @PostConstruct
    public void init() {

        final Configuration configuration = new Configuration(
                Configuration.VERSION_2_3_23);
        configuration.setClassForTemplateLoading(this.getClass(), "");

        Template template;
        try {
            template = configuration.getTemplate("openapi.yaml");
            setTemplate(template);
        } catch (final IOException e) {

            throw new Error(e);
        }

    }

    @GET
    public Response getOpenApiYAML() {

        final Template template = getTemplate();

        final StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(final OutputStream os)
                    throws IOException, WebApplicationException {
                final Writer writer = new BufferedWriter(
                        new OutputStreamWriter(os));
                try {
                    final Map<String, Object> root = new HashMap<>();
                    final String contextPath = getServletContext()
                            .getContextPath();
                    final String apiContext = ProviderApplication.API_CONTEXT;
                    final String apitURL = apiContext.isEmpty() ? contextPath
                            : String.join("/", contextPath, apiContext);
                    root.put("apiURL", apitURL);
                    template.process(root, writer);
                } catch (final TemplateException e) {

                    throw new Error(e);
                }
            }
        };

        return Response.ok(stream).build();
    }

    public Template getTemplate() {
        return this.template;
    }

    public void setTemplate(final Template template) {
        this.template = template;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
