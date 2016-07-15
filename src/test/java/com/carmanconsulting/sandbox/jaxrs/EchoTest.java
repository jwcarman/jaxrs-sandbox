package com.carmanconsulting.sandbox.jaxrs;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.junit.Test;

public class EchoTest extends ResourceTestCase<EchoResourceImpl> {
//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public EchoTest() {
        super(EchoResourceImpl.class);
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    protected EchoResourceImpl createResource() {
        return new EchoResourceImpl();
    }

    @Override
    protected List<Feature> getFeatures() {
        return Collections.singletonList(new LoggingFeature());
    }

    @Test
    public void testEchoHeader() {
        Response response = createWebTarget().path("echoHeader").request(MediaType.TEXT_PLAIN_TYPE).header("Origin", "Hello, JAX-RS!").get();
        assertEquals(200, response.getStatus());
        assertEquals("Hello, JAX-RS!", response.readEntity(String.class));
    }

    @Test
    public void testEchoPathParam() {
        assertEquals("Hello, JAX-RS!", createClientProxy(EchoResource.class).echoPathParam("Hello, JAX-RS!"));
    }
}
