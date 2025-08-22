package com.dat.event.service.impl;

import com.dat.event.service.LdapUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

@Service
public class LdapUserServiceImpl implements LdapUserService {

    @Value("${ldap.url}")
    private String url;

    @Value("${ldap.domain}")
    private String domain;

    @Value("${ldap.INITIAL_CONTEXT_FACTORY}")
    private String INITIAL_CONTEXT_FACTORY;

    @Value("${ldap.SECURITY_AUTHENTICATION}")
    private String SECURITY_AUTHENTICATION;
    @Value("${ldap.REFERRAL}")
    private String REFERRAL;

    @Override
    public boolean isADUser(String staffID, String password) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, url);
            env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
            env.put(Context.SECURITY_PRINCIPAL, domain + "\\" + staffID);
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put(Context.REFERRAL, REFERRAL);

            DirContext ctx = new InitialDirContext(env);

            ctx.close();
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

}
