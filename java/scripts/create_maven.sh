#!/bin/bash

# Deprecated in favor of lein-localrepo.
# But better yet, using leiningen to retrieve deps for maven central.

#glazedlists-1.8.0_java15.jar
#google-collect-1.0.jar
#maven_repository
#prefuse.jar
#scala-compiler.jar
#scala-library.jar
#staticproxy.jar
#trove-3.0.0a3.jar
#xmlpull-1.1.3.1.jar
#xpp3_min-1.1.4c.jar
#xstream-1.3.1.jar

lein localrepo install ../lib/prefuse.jar prefuse 1.0
lein localrepo install ../lib/xmlpull-1.1.3.1.jar xmlpull/xmlpull 1.1.3.1
lein localrepo install ../lib/xpp3_min-1.1.4c.jar xpp3_min/xpp3_min 1.1.4c
lein localrepo install ../lib/xstream-1.3.1.jar xstream/xstream 1.3.1
#mvn install:install-file -Dfile=xmlpull-1.1.3.1jar -DartifactId=xmlpull -Dversion=1.1.3.1 -DgroupId=xmlpull -Dpackaging=jar -DlocalRepositoryPath=maven_repository
#mvn install:install-file -Dfile=prefuse.jar -DartifactId=prefuse -Dversion=1.0.0 -DgroupId=prefuse -Dpackaging=jar -DlocalRepositoryPath=maven_repository
#mvn install:install-file -Dfile=xpp3_min-1.1.4c.jar -DartifactId=xpp3 -Dversion=1.1.4c -DgroupId=xpp3 -Dpackaging=jar -DlocalRepositoryPath=maven_repository
#mvn install:install-file -Dfile=xstream-1.3.1.jar -DartifactId=xstream -Dversion=1.3.1 -DgroupId=xstream -Dpackaging=jar -DlocalRepositoryPath=maven_repository
