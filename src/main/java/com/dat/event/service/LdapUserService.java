package com.dat.event.service;

public interface LdapUserService {

    boolean isADUser(String username, String password);
}
