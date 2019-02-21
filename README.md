[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Build Status](https://travis-ci.com/amtgroup/squash-tm-plugins.svg?branch=master)](https://travis-ci.com/amtgroup/squash-tm-plugins)

# squash-tm-plugins
This repository provides a set of plugins developed and used by AMT GROUP for Squash TM.

Please visit https://www.squashtest.org/ for more details about Squash Test Management.

# plugin.authentication.ldap
## Installation
Copy `plugin.authentication-<version>.jar`to `<squash-tm>/plugins` directory
(e.g. during a docker image build).

## Configuration
| Property                                   | Default value (if any) | Description                                             |
|--------------------------------------------|------------------------|---------------------------------------------------------|
| authentication.ldap.server.url             | ldap://localhost:389   | LDAP server url                                         |
| authentication.ldap.server.managerDn       |                        | Username to connect to LDAP server with                 |
| authentication.ldap.server.managerPassword |                        | Password to connect to LDAP server with                 |
| authentication.ldap.user.dnPatterns        |                        | User full DN pattern to bind with during authentication |
| authentication.ldap.user.searchBase        |                        | User search base                                        |
| authentication.ldap.user.searchFilter      |                        | User search filter (use `{0}` for username)             |
| authentication.ldap.user.searchSubtree     |                        | Whether to perform subtree user search                  |

Bind LDAP authentication is used to verify user credentials. For it to succeed, user full DN (distinguished name) must be available.
There are actually 2 ways how a system can determine user's full name - the actual method used depends on what configuration properties are set:
1. Simple - plugin is configured with a static DN pattern, e.g:
    * `authentication.ldap.user.dnPatterns=uid={0},ou=users,dc=example,dc=com`
2. Filter-based (recommended) - during authentication, plugin performs a LDAP search
    * `authentication.ldap.user.searchBase=ou=users,dc=example,dc=com`
    * `authentication.ldap.user.searchFilter=(&(uid={0})(objectClass=inetOrgPerson)(memberOf=cn=squash-tm-users,ou=groups,dc=example,dc=com))` 

## Spring LDAP Dependencies
Squash TM before version 1.19.0 does not include Spring LDAP dependencies. For this plugin to work,
plugin jar includes all the classes from `spring-security-ldap` and `spring-ldap-core`
(using `maven-dependency-plugin`'s `unpack-dependencies`).
