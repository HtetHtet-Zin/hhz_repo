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
            String base_dn = "DC=" + domain + ",DC=com";
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, url);
            env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
            env.put(Context.SECURITY_PRINCIPAL, domain + "\\" + staffID);
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put(Context.REFERRAL, REFERRAL);

            DirContext ctx = new InitialDirContext(env);

            /*String filter = "(sAMAccountName=" + staffID + ")";
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> results = ctx.search(base_dn, filter, controls);

            while (results.hasMore()) {
                SearchResult result = results.next();
                System.out.println("DN: " + result.getNameInNamespace());

                Attributes attrs = result.getAttributes();
                NamingEnumeration<? extends Attribute> allAttrs = attrs.getAll();

                while (allAttrs.hasMore()) {
                    Attribute attr = allAttrs.next();
                    NamingEnumeration<?> values = attr.getAll();

                    System.out.print(attr.getID() + ": ");

                    while (values.hasMore()) {
                        Object value = values.next();
                        System.out.println(value);
                    }
                }

                System.out.println("------------------------");
            }*/

            ctx.close();
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

}
