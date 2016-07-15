package com.carmanconsulting.sandbox.jaxrs;

public class EchoResourceImpl implements EchoResource {
//----------------------------------------------------------------------------------------------------------------------
// EchoResource Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public String echoPathParam(String msg) {
        return msg;
    }

    @Override
    public String echoHeaderParam(String msg) {
        return msg;
    }
}
