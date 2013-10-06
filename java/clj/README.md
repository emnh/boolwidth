# Getting started

Local java dependencies are managed by [local maven repository](http://www.pgrs.net/2011/10/30/using-local-jars-with-leiningen/).
First create the repository:

    cd ../lib
    bash create_maven.sh

# Setting up vimclojure nailgun

See https://github.com/sattvik/lein-tarsier.

## Install nailgun client:

    wget http://kotka.de/projects/vimclojure/vimclojure-nailgun-client-2.3.0.zip &&
    unzip vimclojure-nailgun-client-2.3.0.zip &&
    rm vimclojure-nailgun-client-2.3.0.zip &&
    cd vimclojure-nailgun-client/ &&
    make &&
    cp ng ~/bin/ &&
    rm -rf vimclojure-nailgun-client

## Install vimclojure.
    
## Add to .vimrc:

    " vimclojure
    let g:vimclojure#HighlightBuiltins = 1
    let g:vimclojure#ParenRainbow = 1
    let vimclojure#WantNailgun = 1
    let vimclojure#NailgunClient = "/usr/bin/ng-nailgun"

## Start it:

    lein vimclojure

Now you can open .clj files in Vim.
