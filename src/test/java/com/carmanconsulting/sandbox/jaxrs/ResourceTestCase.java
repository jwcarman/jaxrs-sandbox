package com.carmanconsulting.sandbox.jaxrs;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.testutil.common.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ResourceTestCase<R> extends Assert {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final String WADL_SUFFIX = "?_wadl";
    @Rule
    public TestName testName = new TestName();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Server server;
    private int port;
    private final Class<R> resourceClass;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public ResourceTestCase(Class<R> resourceClass) {
        this.resourceClass = resourceClass;
    }

//----------------------------------------------------------------------------------------------------------------------
// Abstract Methods
//----------------------------------------------------------------------------------------------------------------------

    protected abstract R createResource();

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public Logger getLogger() {
        return logger;
    }

    public int getPort() {
        return port;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    protected <T> T createClientProxy(Class<T> proxyInterface) {
        if(!proxyInterface.isAssignableFrom(resourceClass)) {
            throw new IllegalArgumentException(String.format("Class %s is not a superclass/superinterface of %s", proxyInterface.getName(), resourceClass.getName()));
        }
        return JAXRSClientFactory.create(getAddress(), proxyInterface, getClientProxyProviders());
    }

    protected WebTarget createWebTarget() {
        return ClientBuilder.newClient().target(getAddress());
    }

    @After
    public void destroy() throws Exception {
        server.stop();
        server.destroy();
    }

    protected String getAddress() {
        return String.format("http://localhost:%d/%s/", port, resourceClass.getSimpleName());
    }

    protected List<Object> getClientProxyProviders() {
        return Collections.emptyList();
    }

    protected List<Feature> getFeatures() {
        return Collections.emptyList();
    }

    protected List<Object> getProviders() {
        return Collections.emptyList();
    }

    @Before
    public void initialize() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.port = 500 + Integer.parseInt(TestUtil.getNewPortNumber(getClass().getName(), testName.getMethodName()));
        startServer();
        waitForWADL();
    }

    private void startServer() throws Exception {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(resourceClass);
        sf.setFeatures(getFeatures());
        sf.setProviders(getProviders());
        sf.setResourceProvider(resourceClass, new SingletonResourceProvider(createResource(), true));
        sf.setAddress(getAddress());
        server = sf.create();
    }

    private void waitForWADL() throws Exception {
        String wadlAddress = getWadlAddress();
        Invocation.Builder client = ClientBuilder.newClient().target(wadlAddress).request();

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            Response response = client.get();
            if (response.getStatus() == 200) {
                logger.info("WADL {} available, proceeding with tests...", wadlAddress);
                return;
            } else {
                logger.info("WADL {} not available yet, waiting 1 more second...", wadlAddress);
            }
        }
    }

    private String getWadlAddress() {
        return getAddress() + WADL_SUFFIX;
    }
}
