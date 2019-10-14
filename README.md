# bootleg

Simple template processing command line tool to help build static websites

## Installation

Bootleg is distributed for linux as a single executable file. Download the latest tarball from https://github.com/retrogradeorbit/bootleg/releases and then extract it. Once extracted, move the binary to your path

    $ tar xvf bootleg-0.1-linux-amd64.tgz
    $ mv bootleg /usr/local/bin

## Usage

Run at the command line for options:

    $ bootleg
    Static site generator

    Usage: bootleg [options] yaml-file

    Options:
      -h, --help     Print the command line help
      -v, --version  Print the version string and exit

Run the command passing in a yaml file describing the template processing workflow. The programme will process the files and print the rendered template to standard out.

## Yaml

The YAML file can be comprised of any keys and values, but there are three special keys. When bootleg walks the yaml tree, it looks for a key called `load`. If load is found, then the template specified is loaded and rendered, and the rendered body is placed into that yaml value's position.

As siblings to the `load` keyword are `vars` and `process`. `vars` contains all the variables that will be substituted into the template (possibly after conversion into html) using moustache syntax. `process` contains a sequence of enlive template processing steps. enlive uses a css based selector system to isolate elements in the markup to apply transforms to.

At the most basic, you may have a yaml like this:

```yaml
---

load: my-template.html
vars:
  title: my page title
  author: retrogradeorbit
```

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
