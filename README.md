<img src="doc/bootleg-logo.svg" width="100px">

# bootleg

[![Github Action](https://github.com/retrogradeorbit/bootleg/workflows/Clojure%20CI/badge.svg)](https://github.com/retrogradeorbit/bootleg/actions?query=workflow%3A%22Clojure+CI%22)

Static website generation made simple. A powerful, fast, clojure html templating solution.

Bootleg is a command line tool that rapidly renders clojure based templates. With inbuilt support for html, hiccup, hickory, selmer, mustache, markdown, enlive, json, yaml and edn, it enables you to pull together disparate elements and glue them together to generate the static website of your dreams.

## Quickstart

Install for Linux:

```shell
$ curl -LO https://github.com/retrogradeorbit/bootleg/releases/download/v0.1.6-1/bootleg-0.1.6-1-linux-amd64.tgz
$ tar xvf bootleg-0.1.6-1-linux-amd64.tgz
$ mv bootleg ~/bin
```

Install for MacOS:

```shell
$ curl -LO https://github.com/retrogradeorbit/bootleg/releases/download/v0.1.6-1/bootleg-0.1.6-1-macos-amd64.zip
$ unzip bootleg-0.1.6-1-macos-amd64.zip
$ mv bootleg /usr/local/bin
```

Clone this repository and change into the `examples/quickstart` directory:

```shell
$ git clone https://github.com/retrogradeorbit/bootleg.git
$ cd bootleg/examples/quickstart
```

A simple page:

```clojure
$ cat example-simple.clj
[:html
 [:body
  [:h1 "A simple webpage"]
  [:p "Made with bootleg for maximum powers!"]]]
```

```html
$ bootleg example-simple.clj
<html><body><h1>A simple webpage</h1><p>Made with bootleg for maximum powers!</p></body></html>
```

A dynamic example:

```clojure
$ cat example-dynamic.clj
[:div.countdown
 (for [n (range 10 0 -1)]
   [:p n])
 [:p "blast off!"]]
```

```html
$ bootleg example-dynamic.clj
<div class="countdown"><p>10</p><p>9</p><p>8</p><p>7</p><p>6</p><p>5</p><p>4</p><p>3</p><p>2</p><p>1</p><p>blast off!</p></div>
```

Mustache:

```clojure
$ cat example-mustache.clj
(mustache "quickstart.html" (yaml "fields.yml"))
```

```html
$ cat quickstart.html
<h1>{{ title }}</h1>
<h2>by {{ author }}</h2>
<div>{{& body }}</div>
```

```yaml
$ cat fields.yml
title: Bootleg
author: Crispin
body: I'm going to rewrite all my sites with this!
```

```html
$ bootleg example-mustache.clj
<h1>Bootleg</h1>
<h2>by Crispin</h2>
<div>I'm going to rewrite all my sites with this!</div>
```

Markdown support. Evaluate from command line. Easy downloading of resources by url (for any command):

```html
$ bootleg -e '(markdown "https://raw.githubusercontent.com/retrogradeorbit/bootleg/master/README.md")'
<h1>bootleg</h1><p>Static website generation made simple. A powerful, fast, clojure templating solution that rocks!...
```

CSS selector based processing. The magic of enlive:

```clojure
$ cat example-enlive.clj
(-> (markdown "simple.md")
    (enlive/at [:p] (enlive/set-attr :style "color:green;")))
```

```markdown
$ cat simple.md
# Markdown support

This is some simple markdown
```

```html
$ bootleg example-enlive.clj
<h1>Markdown support</h1><p style="color:green;">This is some simple markdown</p>
```

Combine hiccup, mustache, markdown and enlive processing:

```clojure
$ cat example-combine.clj
(mustache "quickstart.html"
          (assoc (yaml "fields.yml")
                 :body (markdown "simple.md" :html)))
```

```html
$ bootleg example-combine.clj
<h1>Bootleg</h1>
<h2>by Crispin</h2>
<div><h1>Markdown support</h1><p>This is some simple markdown</p></div>
```

Data output with -d flag:

```clojure
$ cat example-data.clj
(-> (mustache "quickstart.html"
              (assoc (yaml "fields.yml")
                     :body (markdown "simple.md" :html)))
    (convert-to :hickory-seq))
```

```clojure
$ bootleg -d example-data.clj
({:type :element, :attrs nil, :tag :h1, :content ["Bootleg"]}
 "\n"
 {:type :element, :attrs nil, :tag :h2, :content ["by Crispin"]}
 "\n"
 {:type :element,
  :attrs nil,
  :tag :div,
  :content [{:type :element,
             :attrs nil,
             :tag :h1,
             :content ["Markdown support"]}
            {:type :element,
             :attrs nil,
             :tag :p,
             :content ["This is some simple markdown"]}]}
 "\n")
```

```shell
$ bootleg example-data.clj          # <- no -d flag means output will be html
<h1>Bootleg</h1>
<h2>by Crispin</h2>
<div><h1>Markdown support</h1><p>This is some simple markdown</p></div>

```

## Installation

Bootleg is distributed for linux as a single executable file. Download the latest tarball from https://github.com/retrogradeorbit/bootleg/releases and then extract it. Once extracted, move the binary to your path. For system wide installation try `/usr/local/bin` or for personal use `~/bin`

```shell
$ curl -LO https://github.com/retrogradeorbit/bootleg/releases/download/v0.1.6-1/bootleg-0.1.6-1-linux-amd64.tgz
$ tar xvf bootleg-0.1.6-1-linux-amd64.tgz
$ mv bootleg /usr/local/bin
```

### Other Platforms

Windows support is experimental. Download (https://github.com/retrogradeorbit/bootleg/releases/download/v0.1.6-1/bootleg-0.1.6-1-windows-amd64.zip) and unzip the archive. Copy the bootleg.exe binary to somewhere on your path.

The jar release file is also an option if you have java installed. You can run it as follows:

```shell
$ java -jar bootleg-0.1.6-1.jar
```

## Usage

Run at the command line for options:

```shell
$ bootleg -h
Static website generation made simple. A powerful, fast, clojure html templating solution.

Usage: bootleg [options] [clj-file]

Options:
  -h, --help           Print the command line help
  -v, --version        Print the version string and exit
  -e, --evaluate CODE  Pass in the hiccup to evaluate on the command line
  -d, --data           Output the rendered template as a clojure form instead of html
  -o, --output FILE    Write the output to the specified file instead of stdout
  -t, --traceback      Print the full exception traceback
  -c, --colour         Print outputs in colour where appropriate
      --color          Alias for --colour
```

## Overview

`bootleg` loads and evaluates the clj file specified on the command line, or evaluates the `CODE` form specified in the -e flag. The code can return any of the supported data structures listed below. `bootleg` will then automatically convert that format into html and write it out to standard out, or to `FILE` if specified. This conversion to html step can be prevented by calling with the `-d` flag. In this case the output will be a pretty formatted edn form of the output data structure.

`bootleg` supports five main markup data structures. Three are more flexible. And two are limited. We will begin describing the two limited data structures and why they are limited.

### hiccup

Hiccup is a standard clojure DSL syntax for representing markup as nested sequences of vectors, and is represented in option flags by the keyword `:hiccup`. An example of some hiccup: the html `<div><p>This is an example</p></div>` is represented in hiccup as `[:div [:p "This is an example"]]`.

Hiccup, as the term is used in bootleg, is refering to this vector form. It represents a single root element and its children. This means there are template fragments that *cannot* be represented like this. For example, the html snippet `<p>one</p><p>two</p>` cannot be represented as hiccup. It is comprised of two hiccup forms. `[:p "one"]` and `[:p "two"]`. See `hiccup-seq` below for information about this form.

### hickory

Hickory is a format used to internally represent document trees in clojure for programmatic processing. In option flags it is referenced by the keyword `:hickory`. It is very verbose and not suitable to write by hand. It is supported internally for passing between functions that use it. A simple example of some hickory: the html `<p>one</p>` is represented in hickory as `{:type :element, :attrs nil, :tag :p, :content ["one"]}`.

Both the hickory and enlive clojure projects use this format internally to represent and manipulate DOM trees.

Hickory, as the term is used in bootleg, is refering to this hashmap form. It represents a single root element and its children. This means there are template fragments that *cannot* be represented in hickory. For example, the html snippet `<p>one</p><p>two</p>` cannot be represented as hickory. It is comprised of two hickory forms. `{:type :element, :attrs nil, :tag :p, :content ["one"]}` and `{:type :element, :attrs nil, :tag :p, :content ["two"]}`. See `hickory-seq` below for information about this form.

### hiccup-seq

Hiccup-seq is simply a clojure sequence (or vector) of hiccup forms. In option flags it is referenced by the keyword `:hiccup-seq`. By wrapping multiple hiccup forms in a sequence, hiccup-seq can represent any single root element (and it's children) *and* any template fragment composed of sibling elements.

For example: the html snippet `<p>one</p><p>two</p>` is represented in hiccup-seq as: `([:p "one"] [:p "two"])`

### hickory-seq

Hickory-seq is simply a clojure sequence (or vector) of hickory forms. In option flags it is referenced by the keyword `:hickory-seq`. By wrapping multiple hickory forms in a sequence, hickory-seq can represent any single root element (and it's children) *and* any template fragment composed of sibling elements.

For example: the html snippet `<p>one</p><p>two</p>` is represented in hickory-seq as: `({:type :element, :attrs nil, :tag :p, :content ["one"]} {:type :element, :attrs nil, :tag :p, :content ["two"]})`

### html

html (or any type of xml) is represented internally as a string. This is a flexible type and can hold a root element and children, or a number of sibling elements sequentially.

## Inbuilt functions

The following functions are inbuilt into the clojure interpreter:

### Markup Processing Functions

#### markdown

`(markdown source & options)`

Load the markdown from the file specified in `source` and render it. `source` can be a local file path (relative to the executing hiccup file location) or a URL to gather the markdown from.

Options can be used to alter the behaviour of the function. Options are a list of keywords and can be specified in any order after the source parameter. Options can be:

 * `:data` Interpret the `source` argument as markdown data, not a file to load
 * `:hiccup-seq` Return the rendered markdown as a hiccup sequence data structure
 * `:hickory-seq` Return the rendered markdown as a hickory sequence data structure
 * `:html` Return the rendered markdown as an html string

eg.

```clojure
$ bootleg -e '(markdown "# heading\nparagraph" :data)'
<h1>heading</h1><p>paragraph</p>
```
```clojure
$ bootleg -d -e '(markdown "# heading\nparagraph" :data :hickory-seq)'
({:type :element, :attrs nil, :tag :h1, :content ["heading"]}
 {:type :element, :attrs nil, :tag :p, :content ["paragraph"]})
```

```clojure
$ bootleg -d -e '(markdown "# heading\nparagraph" :data :hiccup-seq)'
([:h1 {} "heading"] [:p {} "paragraph"])
```

```clojure
$ bootleg -d -e '(markdown "# heading\nparagraph" :data :html)'
"<h1>heading</h1><p>paragraph</p>"
```

#### mustache

`(mustache source vars & options)`

Load a mustache template from the file specified in `source` and render it substituting the vars from `vars`. `source` can be a local file path (relative to the executing hiccup file location) or a URL to gather the markdown from.

Options can be used to alter the behaviour of the function. Options are a list of keywords and can be specified in any order after the source parameter. Options can be:

 * `:data` Interpret the `source` argument as mustache data, not a file to load
 * `:hiccup` Return the rendered mustache as hiccup
 * `:hiccup-seq` Return the rendered mustache as a hiccup sequence data structure
 * `:hickory` Return the rendered mustache as hickory
 * `:hickory-seq` Return the rendered mustache as a hickory sequence data structure
 * `:html` Return the rendered mustache as an html string

eg.

```clojure
$ bootleg -e '(mustache "<p>{{var1}}</p><div>{{&var2}}</div>" {:var1 "value 1" :var2 "<p>markup</p>"} :data)'
<p>value 1</p><div><p>markup</p></div>
```

```clojure
$ bootleg -d -e '(mustache "<p>{{var1}}</p>" {:var1 "value 1"} :data :hiccup-seq)'
([:p {} "value 1"])
```

#### selmer

`(selmer source vars & options)`

Load a selmer template from the file specified in `source` and render it substituting the vars from `vars`. `source` can be a local file path (relative to the executing hiccup file location) or a URL to gather the markdown from.

Options can be used to alter the behaviour of the function. Options are a list of keywords and can be specified in any order after the source parameter. Options can be:

 * `:data` Interpret the `source` argument as selmer data, not a file to load
 * `:hiccup` Return the rendered selmer as hiccup
 * `:hiccup-seq` Return the rendered selmer as a hiccup sequence data structure
 * `:hickory` Return the rendered selmer as hickory
 * `:hickory-seq` Return the rendered selmer as a hickory sequence data structure
 * `:html` Return the rendered selmer as an html string

eg.

```clojure
$ bootleg -e '(selmer "<p>Hello {{name|capitalize}}!</p>" {:name "world"} :data)'
<p>Hello World!</p>
```

```clojure
$ bootleg -d -e '(selmer "<p>Hello {{name|capitalize}}!</p>" {:name "world"} :data :hiccup-seq)'
([:p {} "Hello World!"])
```

The `selmer` namespaces are also provided at their usual namespace locations.

 * selmer.filter-parser
 * selmer.filters
 * selmer.middleware
 * selmer.node
 * selmer.parser
 * selmer.tags
 * selmer.template-parser
 * selmer.util
 * selmer.validator

#### slurp

`(slurp source)`

Load the contents of a file, from a local or remote source, into memory and return it. This `slurp` can load from URLs. Does no interpretation of the file contents at all. Returns them as is.

#### html

`(html source & options)`

Loads the contents of a html or xml file and returns them in `:hiccup-seq` (by default).

Options can be:

 * `:data` Interpret the `source` argument as markdown data, not a file to load
 * `:hiccup` Return the rendered markdown as hiccup
 * `:hiccup-seq` Return the rendered markdown as a hiccup sequence data structure
 * `:hickory` Return the rendered markdown as hickory
 * `:hickory-seq` Return the rendered markdown as a hickory sequence data structure
 * `:html` Return the rendered markdown as an html string

eg.

```clojure
$ bootleg -d -e '(html "<h1>heading</h1><p>body</p>" :data :hiccup-seq)'
([:h1 {} "heading"] [:p {} "body"])
```

```clojure
$ bootleg -d -e '(html "<div><h1>heading</h1><p>body</p></div>" :data :hiccup)'
[:div {} [:h1 {} "heading"] [:p {} "body"]]
```

#### hiccup

`(hiccup source)`

Loads and evaluates the clojure source from another file.

### Filesystem Functions

#### glob

`(glob pattern)`

Returns a sequence of files that match the globbing pattern `pattern`. Supports `*`, `**`, `?`, `[abc]`, `[a-z]`, `[!a]` and relative file paths `.` and `..`. File paths are returned relative to the directory of the executing file.

```shell
$ bootleg -d -e '(glob "**/*.y?l")'
(".github/workflows/deploy.yml"
 ".circleci/config.yml"
 "examples/quickstart/fields.yml"
 "test/files/simple.yml")
```

#### symlink

`(symlink link target)`

Make a symlink from `link` to `target`. Operates idempotently. If the link already exists it does nothing. If the `link` exists but points to another target, changes the link to point to the specified `target`. If the `link` exists but is not a symbolic link, throws an exception. On success returns the path of the link.

#### mkdir

`(mkdir path)`

Make a directory `path`. Does not create any parent directories. Operates idempotently. If the direcotry exists, it does nothing. Otherwise it tries to create the directory. On success it returns the directory path.

#### mkdirs

`(mkdirs path)`

Make a directory `path` including all the parent directories. Operates idempotently. If the direcotry exists, it does nothing. Otherwise it tries to create the directories. On success it returns the final directory path.

#### spit

`(spit filename data)`

Write `data` into the specified `filename`. The filename is interpereted relative to the path of the current script.

### Var Loading Functions

#### yaml

`(yaml source)`

Load yaml data from `source` and process it. Returns the parsed data structure.

#### json

`(json source)`

Load json data from `source` and process it. Returns the parsed data structure.

#### edn

`(edn source)`

Load edn data from `source` and process it. Returns the parsed data structure.

### Data Testing

#### is-hiccup?

`(is-hiccup? data)`

Test the markup in `data`. Return `true` if the data is a hiccup form.

#### is-hiccup-seq?

`(is-hiccup-seq? data)`

Test the markup in `data`. Return `true` if the data is a sequence of hiccup forms.

#### is-hickory?

`(is-hickory? data)`

Test the markup in `data`. Return `true` if the data is a hickory form.

#### is-hickory-seq?

`(is-hickory-seq? data)`

Test the markup in `data`. Return `true` if the data is a sequence of hickory forms.

### Data Conversion

#### convert-to

`(convert-to data type)`

Convert one supported data type to another. Input data may be hiccup, hiccup-seq, hickory, hickory-seq or html.

Type may be `:hiccup`, `:hiccup-seq`, `:hickory`, `:hickory-seq` or `:html`.

Example:

```clojure
$ bootleg -d -e '(convert-to [:p#id.class "one"] :hickory)'
{:type :element,
 :attrs {:class "class", :id "id"},
 :tag :p,
 :content ["one"]}
```

```clojure
$ bootleg -d -e '(convert-to "<p class=\"class\" id=\"id\">one</p>" :hiccup)'
[:p {:class "class", :id "id"} "one"]
```

```clojure
$ bootleg -d -e '(convert-to "<p>one</p><p>two</p>" :hiccup-seq)'
([:p {} "one"] [:p {} "two"])
```

```clojure
$ bootleg -d -e '(convert-to {:type :element :tag :p :content ["one"]} :html)'
"<p>one</p>"
```

**Note:** Some conversions are lossy. Converting from html or any *-seq data type to hickory or hiccup may lose forms. Only the last form will be returned.

```clojure
$ bootleg -d -e '(convert-to "<p>one</p><p>two</p>" :hiccup)'
[:p {} "two"]
```

#### as-html

`(as-html data)`

Convert any supported input format passed into `data` to html output. Same as `(convert-to data :html)`

### Enlive Processing

Enlive html functions are to be found in the `enlive` namespace. A new version of `at` is supplied that provides automatic type coercion for the inputs and outputs.

In addition to this the standard enlive namespaces are available in their usual locations:

 * net.cgrand.enlive-html
 * net.cgrand.jsoup
 * net.cgrand.tagsoup
 * net.cgrand.xml

#### at

`(at data selector transform & more)`

Take the markup `data` and process every element matching `selector` through the transform `transform`

#### deftemplate

`(deftemplate name source args & forms)`

Defines a template as a function that returns a *hiccup-seq*.

** Note ** the original deftemplate returned a sequence of strings

#### defsnippet

`(defsnippet name source selector args & forms)`

Define a named snippet -- equivalent to (def name (snippet source selector args ...)).

Returns a hickory-seq on nodes.

### Enlive Transforms

#### content

`(content & values)`

Replaces the content of the element. Values can be any supported formats: hickory, hickory-seq, hiccup, hiccup-seq or html.

** note: ** This is different to the standard enlive `content` function. The standard function is present as `content*`. Passing html to `content` will embed that markup in the output. In contrast, passing html to `content*` will insert that html in an escaped form.

#### html-snippet

`(html-snippet & values)`

Concatenate values as a string and then parse it with tagsoup. html-snippet doesn't insert missing <html> or <body> tags.

#### html-content

`(html-content & values)`

Replaces the content of the element. Values are strings containing html code. This is present for backwards compatibility. The new `content` function can take and embed html code and should be used instead.

#### wrap

`(wrap tag attr)`

Wraps selected node into the given tag. eg. `(wrap :div)` or `(wrap :div {:class "foo"})`

#### unwrap

`unwrap`

Opposite to wrap, returns the content of the selected node.

#### set-attr

`(set-attr & key-value-pairs)`

Assocs attributes on the selected element. eg. `(set-attr :attr1 "val1" :attr2 "val2")`

#### remove-attr

`(remove-attr & attr-names)`

Sets given key value pairs as attributes for selected node. eg. `(remove-attr :attr1 :attr2)`

#### add-class

`(add-class & classes)`

Adds class(es) to the selected node. eg. `(add-class "foo" "bar")`

#### remove-class

`(add-class & classes)`

Removes class(es) from the selected node. eg. `(remove-class "foo" "bar")`

#### do->

`(do-> & fns)`

Chains (composes) several transformations. Applies functions from left to right. eg. `(do-> transformation1 transformation2)`

#### append

`(append & values)`

Appends the values to the content of the selected element. eg. `(append "xyz" a-node "abc")`

Values can be any supported formats: hickory, hickory-seq, hiccup, hiccup-seq or html.

#### prepend

`(prepend & values)`

Prepends the values to the content of the selected element. eg. `(prepend "xyz" a-node "abc")`

Values can be any supported formats: hickory, hickory-seq, hiccup, hiccup-seq or html.

#### after

`(after & values)`

Inserts the values after the current selection (node or fragment). eg. `(after "xyz" a-node "abc")`

Values can be any supported formats: hickory, hickory-seq, hiccup, hiccup-seq or html.

#### before

`(before & values)`

Inserts the values before the current selection (node or fragment). eg. `(before "xyz" a-node "abc")`

Values can be any supported formats: hickory, hickory-seq, hiccup, hiccup-seq or html.

#### substitute

`(substitute & values)`

Replaces the current selection (node or fragment). eg. `(substitute "xyz" a-node "abc")`

Values can be any supported formats: hickory, hickory-seq, hiccup, hiccup-seq or html.

#### move

`(move src-selector dest-selector)`
`(src-selector dest-selector combiner)`

Takes all nodes (under the current element) matched by src-selector, removes them and combines them with the elements matched by dest-selector. eg. `(move [:.footnote] [:#footnotes] content)`


#### content*

`(content* & values)`

The original enlive content transformer. Replaces the content of the element. Values can be hickory nodes, collection of hikory nodes, or plain text strings.

#### append*

`(append* & values)`

The original enlive append transformer. Appends the values to the content of the selected element. eg. `(append "xyz" a-node "abc")`

#### prepend*

`(prepend* & values)`

The original enlive prepend transformer. Prepends the values to the content of the selected element. eg. `(prepend "xyz" a-node "abc")`

#### after*

`(after* & values)`

The original enlive after transformer. Inserts the values after the current selection (node or fragment). eg. `(after "xyz" a-node "abc")`

#### before*

`(before* & values)`

The original enlive before transformer. Inserts the values before the current selection (node or fragment). eg. `(before "xyz" a-node "abc")`

#### substitute*

`(substitute* & values)`

The original enlive substitute transformer. Replaces the current selection (node or fragment). eg. `(substitute "xyz" a-node "abc")`

### Hickory

The `hickory` namespaces are provided at their usual namespace locations.

 * hickory.convert
 * hickory.hiccup-utils
 * hickory.render
 * hickory.select
 * hickory.utils
 * hickory.zip

### Other Functions

#### pprint

`(pprint form)`

Pretty print to stdout the passed in form. If `--colour` is passed on command line then print with colour highlighting.

#### parse-string

`(parse-string string)`

Parse the passed in string into a clojure type. Useful for converting strings to numbers, keywords, vectors or hashmaps. A binding of [edamame's](https://github.com/borkdude/edamame) `parse-string` is used for parsing.

## Building the executable

Ensure graalvm community edition 19.3.0 is installed in your home directory and the native image extension is also installed.

```shell
$ make clean
$ make all
```

The compiled file will be at `build/bootleg`

To use a different version of graal or one installed in a different path, add the GRAALVM suffix like:

```shell
$ make all GRAALVM_HOME=/path/to/graal-vm
```

## Examples

The following are some example sites built using bootleg for you to use as inspiration, or as a base for your own work.

 * [epiccastle.io](https://github.com/epiccastle/epiccastle.io)

### Submit Your Own Example

Have you built a website with bootleg? Is the source of this website open source? If so [open an issue](https://github.com/retrogradeorbit/bootleg/issues/new) on the project with a link to your github project with the source tree in it and I will add it to the examples list. This way there will be more reference material for bootleg users to draw apon!

## Thanks

`bootleg` leverages other people's amazing work. The following projects and people enable this to exist at all.

 * [Michiel Borkent](https://www.michielborkent.nl/) built [Sci](https://github.com/borkdude/sci)
 * [James Reeves](https://www.booleanknot.com/) built [Hiccup](https://github.com/weavejester/hiccup)
 * [Christophe Grand](http://clj-me.cgrand.net/) built [Enlive](https://github.com/cgrand/enlive)
 * [David Santiago](https://github.com/davidsantiago) built [Hickory](https://github.com/davidsantiago/hickory)
 * [Dmitri Sotnikov](https://yogthos.net/) built [Selmer](https://github.com/yogthos/Selmer)

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
