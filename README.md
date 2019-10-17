# bootleg

Static website generation made simple. A powerful, fast, clojure html templating solution.

Bootleg is a command line tool that rapidly renders clojure based templates. With inbuilt support for html, hiccup, hickory, mustache, enlive, json, yaml and edn, it enables you to pull together disparate elements and glue them together to generate the static website of your dreams.

## Quickstart

A simple page:

    $ bootleg -e '[:html [:head [:title "A simple webpage"]] [:body [:h1 "A simple webpage"] [:p "Made with " [:pre "bootleg"] " for maximum powers!"]]]'
    <html><head><title>A simple webpage</title></head><body><h1>A simple webpage</h1><p>Made with <pre>bootleg</pre> for maximum powers!</p></body></html>

A dynamic example:

    $ bootleg -e '[:div (for [n (range 10 0 -1)] [:div n]) [:div "blast off!"]]'
    <div><div>10</div><div>9</div><div>8</div><div>7</div><div>6</div><div>5</div><div>4</div><div>3</div><div>2</div><div>1</div><div>blast off!</div></div>

Mustache:

    $ cd examples/quickstart
    $ build -e '(mustache "quickstart.html" (yaml "fields.yml"))'
    <h1>Bootleg</h1>
    <h2>by Crispin</h2>
    <p>I'm going to rewrite all my sites with this!</p>

Markdown support. Easy downloading of resources by url (for any command):

    $ build -e '(markdown "https://raw.githubusercontent.com/retrogradeorbit/bootleg/master/README.md")'
    <h1>bootleg</h1><p>Static website generation made simple. A powerful, fast, clojure templating solution that rocks!</p>...

CSS selector based processing (via enlive):

    $ build -e '(-> [:div [:h1.blurb] [:p#myid.blurb]] (at [:.blurb] (content "replace all blurb content") ))'
    <div><h1 class="blurb">replace all blurb content</h1><p class="blurb" id="myid">replace all blurb content</p></div>

## Installation

Bootleg is distributed for linux as a single executable file. Download the latest tarball from https://github.com/retrogradeorbit/bootleg/releases and then extract it. Once extracted, move the binary to your path. For system wide installation try `/usr/local/bin` or for personal use `~/bin`

    $ tar xvf bootleg-0.1.1-linux-amd64.tgz
    $ mv bootleg /usr/local/bin

## Usage

Run at the command line for options:

    $ bootleg -h
    Static site generator

    Usage: bootleg [options] [clj-file]

    Options:
      -h, --help           Print the command line help
      -v, --version        Print the version string and exit
      -e, --evaluate CODE  Pass in the hiccup to evaluate on the command line
      -d, --data           Output the rendered template as a clojure form instead of html

## License

Copyright © 2019 Crispin Wellington

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
