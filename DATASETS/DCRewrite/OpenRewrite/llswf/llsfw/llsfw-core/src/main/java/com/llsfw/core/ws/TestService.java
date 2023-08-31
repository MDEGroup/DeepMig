package com.llsfw.core.ws;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface TestService {

    public String getSayHello();

    public String getName(@WebParam(name = "name") String name);

}
