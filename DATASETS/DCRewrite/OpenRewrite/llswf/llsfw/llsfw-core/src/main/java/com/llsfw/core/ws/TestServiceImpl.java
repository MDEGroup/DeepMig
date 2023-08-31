package com.llsfw.core.ws;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;

import com.llsfw.core.service.ws.TestUserService;

@WebService
public class TestServiceImpl implements TestService {

    @Autowired
    private TestUserService tus;

    @Override
    public String getSayHello() {
        return this.tus.getSayHello();
    }

    @Override
    public String getName(String name) {
        return "hello " + name;
    }

}
