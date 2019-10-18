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

Enlive processing:

    $ bootleg -e '(-> [:div [:h1.blurb] [:p.blurb]] (at [:.blurb] (content "blurb content") ))'
    <div><h1 class="blurb">blurb content</h1><p class="blurb">blurb content</p></div>

Data output with -d flag:

    $ bootleg -d -e '(markdown "examples/quickstart/simple.md")'
    ([:h1 {} "Markdown support"] [:p {} "This is some simple markdown"])

Process files:

    $ echo '[:p#myid "an example"]' > example.clj
    $ bootleg -o example.html example.clj
    $ cat example.html
    <p id="myid">an example</p>

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
      -o, --output FILE    Write the output to the specified file instead of stdout

## Overview

`bootleg` supports five principle data structures. Three are more flexible. And two are limited. We will begin describing the two limited data structures and why they are limited.

### hiccup

Hiccup is a standard clojure DSL syntax for representing markup as nested sequences of vectors, and is represented in option flags by the keyword `:hiccup`. An example of some hiccup: the html `<div><p>This is an example</p></div>` is represented in hiccup as `[:div [:p "This is an example"]]`.

Hiccup is limited in that it can only represent a single root element and its children. This means there are template fragments that *cannot* be represented in hiccup. For example, the html snippet `<p>one</p><p>two</p>` cannot be represented as hiccup. It is comprised of two hiccup forms. `[:p "one"]` and `[:p "two"]`

### hickory

Hickory is a format used to internally represent document trees in clojure for programmatic processing. In option flags it is referenced by the keyword `:hickory`. It is very verbose and not suitable to write by hand. It is supported internally for passing between functions that use it. A simple example of some hickory: the html `<p>one</p>` is represented in hickory as `{:type :element, :attrs nil, :tag :p, :content ["one"]}`.

Both the hickory and enlive clojure projects use this format internally to represent and manipulate DOM trees.

Hickory is limited in that it can only represent a single root element and its children. This means there are template fragments that *cannot* be represented in hickory. For example, the html snippet `<p>one</p><p>two</p>` cannot be represented as hickory. It is comprised of two hickory forms. `{:type :element, :attrs nil, :tag :p, :content ["one"]}` and `{:type :element, :attrs nil, :tag :p, :content ["two"]}`

### hiccup-seq

Hiccup-seq is simply a clojure sequence (or vector) of hiccup forms. In option flags it is referenced by the keyword `:hiccup-seq`. By wrapping multiple hiccup forms in a sequence, hiccup-seq can now represent any single root element (and it's children) *and* any template fragment composed of sibling elements.

For example: the html snippet `<p>one</p><p>two</p>` is represented in hiccup-seq as: `([:p "one"] [:p "two"])`

### hickory-seq

Hickory-seq is simply a clojure sequence (or vector) of hickory forms. In option flags it is referenced by the keyword `:hickory-seq`. By wrapping multiple hickory forms in a sequence, hickory-seq can now represent any single root element (and it's children) *and* any template fragment composed of sibling elements.

For example: the html snippet `<p>one</p><p>two</p>` is represented in hickory-seq as: `({:type :element, :attrs nil, :tag :p, :content ["one"]} {:type :element, :attrs nil, :tag :p, :content ["two"]})`

### html

html (or any type of xml) is represented internally as a string. This is a flexible type and can hold a root element and children, or a number of sibling elements sequentially.

## Inbuilt functions

The following functions are inbuilt into the clojure interpreter:

### markdown

**(markdown source & options)**

Load the markdown from the file specified in `source` and render it. `source` can be a local file path (relative to the executing hiccup file location) or a URL to gather the markdown from.

Options can be used to alter the behaviour of the function. Options are a list of keywords and can be specified in any order after the source parameter. Options can be:

 * `:data` Interpret the `source` argument as markdown data, not a file to load
 * `:hiccup-seq` Return the rendered markdown as a hiccup sequence data structure
 * `:hickory-seq` Return the rendered markdown as a hickory sequence data structure
 * `:html` Return the rendered markdown as an html string

eg.

    $ bootleg -e '(markdown "# heading\nparagraph" :data)'
    <h1>heading</h1><p>paragraph</p>

    $ bootleg -d -e '(markdown "# heading\nparagraph" :data :hickory-seq)'
    ({:type :element, :attrs nil, :tag :h1, :content ["heading"]}
     {:type :element, :attrs nil, :tag :p, :content ["paragraph"]})

    $ bootleg -d -e '(markdown "# heading\nparagraph" :data :hiccup-seq)'
    ([:h1 {} "heading"] [:p {} "paragraph"])

    $ bootleg -d -e '(markdown "# heading\nparagraph" :data :html)'
    "<h1>heading</h1><p>paragraph</p>"


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
