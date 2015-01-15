DATEX II for Java
=================

[![Build Status](https://travis-ci.org/cdvcz/datex2.svg?branch=master)](https://travis-ci.org/cdvcz/datex2)

DATEX II Open Java API and examples for easier use of DATEX II in Java
environment.

Due to the nature of DATEX II protocol it is not simple client/server
architecture but both sides (suppliers and clients) can be both
request originators and servers.

It uses several Java ecosystem technologies, most notable Spring and
CXF.

The source code is licensed with BSD 3-clause license.

Created by [Centrum dopravního výzkumu, v.v.i.](http://www.cdv./cz)
(Transport Research Centre - Czech governmental research institute based in
Brno, Czech Republic).

DATEX II Version
----------------
Branch `master` currently uses DATEX 2.2 with Level B extension for parking, Parking Extensions v1.0a.
In future several branches can be made for several DATEX versions. Version numbering is yet to be defined but probably something like `0.1-2.2`.

Usage
-----

Releases can be found in Maven Central Repository, yet no release has been made to this day. Snapshots are in OSSRH repository:
```xml
<repository>
  <id>ossrh</id>
  <url>https://oss.sonatype.org/content/repositories/snaphots</url>
  <releases>
    <enabled>false</enabled>
  </releases>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</repository>
```

There is no documentation yet but you can **use the force and read the source** and look at example supplier and client source code, see submodules `datex2-supplier` and `datex2-client`.

Good starting points:
- [SimpleClient.java](datex2-client/src/main/java/cz/cdv/datex2/client/SimpleClient.java)
- [Client.java](datex2-client/src/main/java/cz/cdv/datex2/client/Client.java)
- [Supplier.java](datex2-supplier/src/main/java/cz/cdv/datex2/supplier/Supplier.java)
