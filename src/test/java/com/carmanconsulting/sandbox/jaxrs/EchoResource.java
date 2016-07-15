package com.carmanconsulting.sandbox.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface EchoResource {

    @GET
    @Path("/echo/{msg}")
    @Produces(MediaType.TEXT_PLAIN)
    String echoPathParam(@PathParam("msg") String msg);

    @GET
    @Path("/echoHeader")
    @Produces(MediaType.TEXT_PLAIN)
    String echoHeaderParam(@HeaderParam("Origin") String msg);
}
