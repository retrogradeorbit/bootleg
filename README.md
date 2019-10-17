# bootleg

Static website generation made simple. A powerful, fast, clojure html templating solution.

Bootleg is a command line tool that rapidly renders clojure based templates. With inbuilt support for html, hiccup, hickory, mustache, enlive, json, yaml and edn, it enables you to pull together disparate elements and glue them together to generate the static website of your dreams.

## Quickstart

A simple page:

    $ bootleg -e '[:html [:body [:h1 "A simple webpage"] [:p "Made with bootleg for maximum powers!"]]]'
    <html><body><h1>A simple webpage</h1><p>Made with bootleg for maximum powers!</p></body></html>

A dynamic example:

    $ bootleg -e '[:div.countdown (for [n (range 10 0 -1)] [:p n]) [:p "blast off!"]]'
    <div class="countdown"><p>10</p><p>9</p><p>8</p><p>7</p><p>6</p><p>5</p><p>4</p><p>3</p><p>2</p><p>1</p><p>blast off!</p></div>

Mustache:

    $ cd examples/quickstart
    $ bootleg -e '(mustache "quickstart.html" (yaml "fields.yml"))'
    <h1>Bootleg</h1>
    <h2>by Crispin</h2>
    <p>I'm going to rewrite all my sites with this!</p>

Markdown support. Easy downloading of resources by url (for any command):

    $ bootleg -e '(markdown "https://raw.githubusercontent.com/retrogradeorbit/bootleg/master/README.md")'
    <h1>bootleg</h1><p>Static website generation made simple. A powerful, fast, clojure templating solution that rocks!</p>...

CSS selector based processing. The magic of enlive:

    $ bootleg -e '(-> (markdown "examples/quickstart/simple.md") (at [:p] (set-attr :style "color:green;")))'
    <h1>Markdown support</h1><p style="color:green;">This is some simple markdown</p>

Fast startup and processing:

    $ time bootleg -e '(-> [:div [:h1.blurb] [:p.blurb]] (at [:.blurb] (content "blurb content") ))'
    <div><h1 class="blurb">blurb content</h1><p class="blurb">blurb content</p></div>

    real	0m0.007s
    user	0m0.004s
    sys	0m0.003s

Data output with -d flag:

    $ bootleg -d -e '(markdown "examples/quickstart/simple.md")'
    ([:h1 {} "Markdown support"] [:p {} "This is some simple markdown"])

## Installation

Bootleg is distributed for linux as a single executable file. Download the latest tarball from https://github.com/retrogradeorbit/bootleg/releases and then extract it. Once extracted, move the binary to your path. For system wide installation try `/usr/local/bin` or for personal use `~/bin`

    $ tar xvf bootleg-0.1.1-linux-amd64.tgz
    $ mv bootleg /usr/local/bin

## Usage

Run at the command line for options:

    $ bootleg -h
    Static website generation made simple. A powerful, fast, clojure html templating solution.

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
