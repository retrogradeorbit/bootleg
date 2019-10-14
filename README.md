# bootleg

Simple template processing command line tool to help build static websites

## Installation

Bootleg is distributed for linux as a single executable file. Download the latest tarball from https://github.com/retrogradeorbit/bootleg/releases and then extract it. Once extracted, move the binary to your path

    $ tar xvf bootleg-0.1.0-linux-amd64.tgz
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
load: my-template.html
vars:
  title: My page title
  author: Joe Average
```

Here, bootleg would load `my-template.html` from the same directory the yaml file is in. It would then substitute `{{ title }}` with "My page title" and `{{ author }}` with "Joe Average". The final rendered template would then be written out to standard out.

You could render it like:

    $ bootleg site/pages/main.yml > site/pages/index.html

If we want a nested example consider the following:

```yaml
load: ../site-template.html
vars:
    title: My page title
    author: Joe Average
    body:
        load: ../body-template.html
        vars:
            title: Monday, October 14, 2019
            blurb: My day
            body: Lorum ipsum etc.
    footer:
        load: ../footer-template.html
        vars:
            company: Little Ball Bearing Corp
            years: 2018-2019
```

This would load the `footer-template.html` file from the parent directory (to the yaml), and render it with the `company` and `years` vars. Then this entire rendered text would be present under the `footer` key.

Next it would load the `body-template.html` file from the parent directory, render it with `title` and `blurb` and `body`, and that entire rendered text would be available under the `body` key.

Next it would load the `site-template.html` file and render it with `title`, `author`, `body` and `footer`.

This final rendered template would be output.

**Note** in order to prevent the escaping of all html tags in the embedded content when it is injected as a var into the parent, *use the triple brace format* `{{{ body }}}`. So for above the site-template may look like:

```html
<html>
 <head>
  <title>{{ title }}</title>
 </head>
 <body>
  <h1>{{ title }}<h1>
  <h3>{{ author }}</h3>
  <div>{{{ body }}}</div>
 </body>
</html>
```

## Markdown

If a `load` instruction references a file with a markdown file extension, the file will be loaded as markdown and rendered into html. eg:

```yaml
load: template.html
vars:
    title: my title
    author: my name
    body:
        load: blogpost.md
```

Here, the rendered contents of `blogpost.md` will be injected into `template.html`

## Enlive

More info on enlive can be found here: https://github.com/cgrand/enlive

Often after loading a template or content you want to post process the markup. You can specify enlive processing steps with the `process` key.

Under process we have a list of transforms. Each transform has a `selector`, `transform` and `args`.

`selector` is a css selector to select the elements.

`transform` in the name of the enlive transform to apply

`args` is the extra arguments you wish to pass to the transformer as a list.

For example, here we load some markdown and then add some style to the `pre` and `code` tags, and add some extra classes to all the `img` tags:

```yaml
load: body.md
process:
  - selector: img
    transform: add-class
    args:
      - image
      - fit

  - selector: pre
    transform: set-attr
    args:
      - style
      - border-radius:8px;margin-bottom:32px;

  - selector: code
    transform: set-attr
    args:
      - style
      - padding:0px;
```

Here are the supported enlive tranforms

**content** replace the content of the element with the argument

**html-content** replace the content of the element with raw html

**add-class** add one or more names to the element's class list

**remove-class** remove one or more names from the element's class

**set-attr** set an attribute of the element to a value. First argument is the attribute name. Second argument is the value.

**remove-attr** remove an attribute from the element.

**prefix-path-to-attr** prefix the directory path of the yaml file to the specified attribute. This is useful for prefixing paths to images for example.

## Examples

Here is a real example from my static blog. This file lives at `blog/posts/1/main.yml`.

```yaml
---
load: ../template.html
process:
  - selector: img.blog-splash
    transform: add-class
    args:
      - image
      - fit

  - selector: img.blog-splash
    transform: prefix-path-to-attr
    args:
      - src

vars:
  title: Setting Up Acmetool With Route53 Hooks
  blurb: "Get free Let's Encrypt SSL certificates using Acmetool's DNS Hooks option when using Amazon Route53 as your DNS service provider."
  author: Crispin Wellington
  date: Oct 6, 2019

  splash:
    image: splash.png
    author: Robert Anasch
    url: "https://unsplash.com/@diesektion?utm_medium=referral&utm_campaign=photographer-credit&utm_content=creditBadge"

  body:
    load: body.md
    process:
      - selector: img
        transform: add-class
        args:
          - image
          - fit

      - selector: img
        transform: prefix-path-to-attr
        args:
          - src

      - selector: pre
        transform: set-attr
        args:
          - style
          - border-radius:8px;margin-bottom:32px;

      - selector: code
        transform: set-attr
        args:
          - style
          - padding:0px;

```

The page is rendered like:

    $ bootleg blog/posts/1/main.yml > blog-1.html

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
