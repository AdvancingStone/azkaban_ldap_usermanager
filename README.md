Azkaban Ldap UserManager
========================

enhanced ldap usernamager. 基于LDAP认证，基于配置文件授权

Copied and Improved Via https://github.com/researchgate/azkaban-ldap-usermanager

This plugin enables ldap authentication for the Azkaban workflow manager (https://azkaban.github.io/)

This plugin is work in progress, configuration options may change.

Installation
------------

Build the plugin

```
gradle build
```

and place the created jar from ./build/libs into the ./extlib folder of Azkaban (see also http://azkaban.github.io/azkaban/docs/latest/#custom-usermanager) for details.

In your azkaban.properties file set the UserManager to the new Ldap one:

```
user.manager.class=com.bluehonour.azkaban.LdapUserManager
```

Configuration
-------------
openldap

The following configuration options are currently available:

```
user.manager.ldap.host=ldap.example.com
user.manager.ldap.port=636
user.manager.ldap.useSsl=true
user.manager.ldap.userBase=dc=example,dc=com
user.manager.ldap.userIdProperty=uid
user.manager.ldap.emailProperty=mail
user.manager.ldap.bindAccount=cn=read-only-admin,dc=example,dc=com
user.manager.ldap.bindPassword=password
user.manager.ldap.allowedGroups=azkaban-ldap-group
user.manager.ldap.adminGroups=admin
user.manager.ldap.readGroups=read
user.manager.ldap.writeGroups=write
user.manager.ldap.executeGroups=execute
user.manager.ldap.scheduleGroups=schedule
user.manager.ldap.createProjectsGroups=createProjects
user.manager.ldap.groupSearchBase=ou=Group,dc=example,dc=com
user.manager.ldap.embeddedGroups=false
```
adldap

配置 azkaban-web-server/conf/azkaban.properties

```sh
# Azkaban UserManager class
# user.manager.class=azkaban.user.XmlUserManager
# user.manager.xml.file=/opt/software/azkaban/azkaban-web-server-0.1.0-SNAPSHOT/conf/azkaban-users.xm
user.manager.class=azkaban.ldap.LdapUserManager
user.manager.ldap.host=
user.manager.ldap.port=389
user.manager.ldap.useSsl=false
user.manager.ldap.userBase=CN=Users,DC=bluehonour,DC=com
user.manager.ldap.userIdProperty=cn
user.manager.ldap.emailProperty=mail
user.manager.ldap.bindAccount=CN=Administrator,CN=Users,DC=bluehonour,DC=com
user.manager.ldap.groupSearchBase=CN=Users,DC=bluehonour,DC=com
user.manager.ldap.bindPassword=
user.manager.ldap.allowedGroups=azkabanAllowedGroup
user.manager.ldap.adminGroups=azkabanAdminGroup
user.manager.ldap.readGroups=azkabanReadGroup
user.manager.ldap.writeGroups=azkabanWriteGroup
user.manager.ldap.executeGroups=azkabanExecuteGroup
user.manager.ldap.scheduleGroups=azkabanScheduleGroup
user.manager.ldap.createProjectsGroups=azkabanCreateProjectsGroup
user.manager.ldap.embeddedGroups=false
```

修改以下属性：

```
host、userBase、bindAccount、groupSearchBase、bindPassword
```

在ldap中新建以下组：

```
azkabanAllowedGroup、azkabanAdminGroup、azkabanReadGroup、azkabanWriteGroup、azkabanExecuteGroup、azkabanScheduleGroup、azkabanCreateProjectsGroup
```
![ldap](.//pic//ldap.png)