package com.hypers.azkaban;

import azkaban.user.Role;
import azkaban.user.User;
import azkaban.user.UserManagerException;
import azkaban.utils.Props;
import com.bluehonour.azkaban.OpenLdapUserManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zapodot.junit.ldap.EmbeddedLdapRule;
import org.zapodot.junit.ldap.EmbeddedLdapRuleBuilder;

import static org.junit.Assert.*;

public class LdapUserManagerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public EmbeddedLdapRule embeddedLdapRule = EmbeddedLdapRuleBuilder
            .newInstance()
            .usingDomainDsn("dc=example,dc=com")
            .withSchema("custom-schema.ldif")
            .importingLdifs("default.ldif")
            .bindingToPort(11389)
            .usingBindDSN("cn=read-only-admin,dc=example,dc=com")
            .usingBindCredentials("password")
            .build();

    private OpenLdapUserManager userManager;

    @Before
    public void setUp() throws Exception {
        Props props = getProps();
        userManager = new OpenLdapUserManager(props);
    }

    private Props getProps() {
        Props props = new Props();
        props.put(OpenLdapUserManager.LDAP_HOST, "localhost");
        props.put(OpenLdapUserManager.LDAP_PORT, "11389");
        props.put(OpenLdapUserManager.LDAP_USE_SSL, "false");
        props.put(OpenLdapUserManager.LDAP_USER_BASE, "dc=example,dc=com");
        props.put(OpenLdapUserManager.LDAP_USERID_PROPERTY, "uid");
        props.put(OpenLdapUserManager.LDAP_EMAIL_PROPERTY, "mail");
        props.put(OpenLdapUserManager.LDAP_BIND_ACCOUNT, "cn=read-only-admin,dc=example,dc=com");
        props.put(OpenLdapUserManager.LDAP_BIND_PASSWORD, "password");
        props.put(OpenLdapUserManager.LDAP_ALLOWED_GROUPS, "");
        props.put(OpenLdapUserManager.LDAP_GROUP_SEARCH_BASE, "dc=example,dc=com");
        return props;
    }

    @Test
    public void testGetUser() throws Exception {
        User user = userManager.getUser("gauss", "password");

        assertEquals("gauss", user.getUserId());
        assertEquals("gauss@ldap.example.com", user.getEmail());
    }

    @Test
    public void testGetUserWithAllowedGroup() throws Exception {
        Props props = getProps();
        props.put(OpenLdapUserManager.LDAP_ALLOWED_GROUPS, "svc-test");
        final OpenLdapUserManager manager = new OpenLdapUserManager(props);

        User user = manager.getUser("gauss", "password");

        assertEquals("gauss", user.getUserId());
        assertEquals("gauss@ldap.example.com", user.getEmail());
    }

    @Test
    public void testGetUserWithAllowedGroupThatGroupOfNames() throws Exception {
        Props props = getProps();
        props.put(OpenLdapUserManager.LDAP_ALLOWED_GROUPS, "svc-test2");
        final OpenLdapUserManager manager = new OpenLdapUserManager(props);

        User user = manager.getUser("gauss", "password");

        assertEquals("gauss", user.getUserId());
        assertEquals("gauss@ldap.example.com", user.getEmail());
    }


    @Test
    public void testGetUserWithEmbeddedGroup() throws Exception {
        Props props = getProps();
        props.put(OpenLdapUserManager.LDAP_ALLOWED_GROUPS, "svc-test");
        props.put(OpenLdapUserManager.LDAP_EMBEDDED_GROUPS, "true");
        final OpenLdapUserManager manager = new OpenLdapUserManager(props);

        User user = manager.getUser("gauss", "password");

        assertEquals("gauss", user.getUserId());
        assertEquals("gauss@ldap.example.com", user.getEmail());
    }

    @Test
    public void testGetUserWithInvalidPasswordThrowsUserManagerException() throws Exception {
        thrown.expect(UserManagerException.class);
        userManager.getUser("gauss", "invalid");
    }

    @Test
    public void testGetUserWithInvalidUsernameThrowsUserManagerException() throws Exception {
        thrown.expect(UserManagerException.class);
        userManager.getUser("invalid", "password");
    }

    @Test
    public void testGetUserWithEmptyPasswordThrowsUserManagerException() throws Exception {
        thrown.expect(UserManagerException.class);
        userManager.getUser("gauss", "");
    }

    @Test
    public void testGetUserWithEmptyUsernameThrowsUserManagerException() throws Exception {
        thrown.expect(UserManagerException.class);
        userManager.getUser("", "invalid");
    }

    @Test
    public void testValidateUser() throws Exception {
        assertTrue(userManager.validateUser("gauss"));
        assertFalse(userManager.validateUser("invalid"));
    }

    @Test
    public void testGetRole() throws Exception {
        Role role = userManager.getRole("admin");

        assertTrue(role.getPermission().isPermissionNameSet("ADMIN"));
    }

    @Test
    public void testInvalidEmailPropertyDoesNotThrowNullPointerException() throws Exception {
        Props props = getProps();
        props.put(OpenLdapUserManager.LDAP_EMAIL_PROPERTY, "invalidField");
        userManager = new OpenLdapUserManager(props);
        User user = userManager.getUser("gauss", "password");

        assertEquals("gauss", user.getUserId());
        assertEquals("", user.getEmail());
    }

    @Test
    public void testInvalidIdPropertyThrowsUserManagerException() throws Exception {
        thrown.expect(UserManagerException.class);

        Props props = getProps();
        props.put(OpenLdapUserManager.LDAP_USERID_PROPERTY, "invalidField");
        userManager = new OpenLdapUserManager(props);
        userManager.getUser("gauss", "password");
    }

    @Test
    public void testEscapeLDAPSearchFilter() throws Exception {
        assertEquals("No special characters to escape", "Hi This is a test #çà", OpenLdapUserManager.escapeLDAPSearchFilter("Hi This is a test #çà"));
        assertEquals("LDAP Christams Tree", "Hi \\28This\\29 = is \\2A a \\5C test # ç à ô", OpenLdapUserManager.escapeLDAPSearchFilter("Hi (This) = is * a \\ test # ç à ô"));
    }
}