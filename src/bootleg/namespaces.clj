(ns bootleg.namespaces
  (:require [bootleg.utils :as utils]
            [bootleg.enlive :as enlive]
            [bootleg.selmer :as selmer]
            [hickory.convert]
            [hickory.hiccup-utils]
            [hickory.render]
            [hickory.select]
            [hickory.utils]
            [hickory.zip]
            [net.cgrand.enlive-html :as enlive-html]
            [net.cgrand.jsoup]
            [net.cgrand.reload]
            [net.cgrand.tagsoup]
            [net.cgrand.xml]
            [selmer.filter-parser]
            [selmer.filters]
            [selmer.middleware]
            [selmer.node]
            [selmer.parser]
            [selmer.tags]
            [selmer.template-parser]
            [selmer.util]
            [selmer.validator]
            [edamame.core :refer [parse-string]]
            [clojure.walk]
            [clojure.tools.cli]
            ))

(def namespaces
  {'Math {
          'abs #(Math/abs %1)
          'acos #(Math/acos %1)
          'asin #(Math/asin %1)
          'atan #(Math/atan %1)
          'atan2 #(Math/atan2 %1 %2)
          'cbrt #(Math/cbrt %1)
          'ceil #(Math/ceil %1)
          'copySign #(Math/copySign %1 %2)
          'cos #(Math/cos %1)
          'cosh #(Math/cosh %1)
          'exp #(Math/exp %1)
          'expm1 #(Math/expm1 %1)
          'floor #(Math/floor %1)
          'floorDiv #(Math/floorDiv %1 %2)
          'floorMod #(Math/floorMod %1 %2)
          'log #(Math/log %1)
          'log10 #(Math/log10 %1)
          'pow #(Math/pow %1 %2)
          'random #(Math/random)
          'rint #(Math/rint %1)
          'round #(Math/round %1)
          'signum #(Math/signum %1)
          'sin #(Math/sin %1)
          'sinh #(Math/sinh %1)
          'sqrt #(Math/sqrt %1)
          'tan #(Math/tan %1)
          'tanh #(Math/tanh %1)
          'toDegrees #(Math/toDegrees %1)
          'toIntExact #(Math/toIntExact %1)
          'toRadians #(Math/toRadians %1)
          'E Math/E
          'PI Math/PI}
   'bootleg.utils {
                   'hickory-seq-add-missing-types bootleg.utils/hickory-seq-add-missing-types
                   'hickory-seq-convert-dtd bootleg.utils/hickory-seq-convert-dtd
                   'html->hiccup-seq bootleg.utils/html->hiccup-seq
                   'html->hiccup bootleg.utils/html->hiccup
                   'hiccup-seq->html bootleg.utils/hiccup-seq->html
                   'hiccup->html bootleg.utils/hiccup->html
                   'hiccup->hickory bootleg.utils/hiccup->hickory
                   'hickory->hiccup bootleg.utils/hickory->hiccup
                   'hiccup-seq->hickory-seq bootleg.utils/hiccup-seq->hickory-seq
                   'hickory-seq->hiccup-seq bootleg.utils/hickory-seq->hiccup-seq
                   'html->hickory-seq bootleg.utils/html->hickory-seq
                   'html->hickory bootleg.utils/html->hickory
                   'hickory-seq->html bootleg.utils/hickory-seq->html
                   'hickory->html bootleg.utils/hickory->html
                   'is-hiccup? bootleg.utils/is-hiccup?
                   'is-hickory? bootleg.utils/is-hickory?
                   'is-hickory-seq? bootleg.utils/is-hickory-seq?
                   'is-hiccup-seq? bootleg.utils/is-hiccup-seq?
                   'markup-type bootleg.utils/markup-type
                   'conversion-fns bootleg.utils/conversion-fns
                   'convert-to bootleg.utils/convert-to
                   'html-output-to bootleg.utils/html-output-to
                   'as-html bootleg.utils/as-html
                   }
   'hickory.convert {
                     'hiccup-to-hickory hickory.convert/hiccup-to-hickory
                     'hiccup-fragment-to-hickory hickory.convert/hiccup-fragment-to-hickory
                     'hickory-to-hiccup hickory.convert/hickory-to-hiccup
                     }
   'hickory.hiccup-utils {
                          'tag-well-formed? hickory.hiccup-utils/tag-well-formed?
                          'tag-name hickory.hiccup-utils/tag-name
                          'class-names hickory.hiccup-utils/class-names
                          'id hickory.hiccup-utils/id
                          'normalize-form hickory.hiccup-utils/normalize-form
                          }
   'hickory.render {
                    'hickory-to-html hickory.render/hickory-to-html
                    'hiccup-to-html hickory.render/hiccup-to-html
                    }
   'hickory.select {
                    'until hickory.select/until
                    'count-until hickory.select/count-until
                    'next-pred hickory.select/next-pred
                    'prev-pred hickory.select/prev-pred
                    'left-pred hickory.select/left-pred
                    'right-pred hickory.select/right-pred
                    'up-pred hickory.select/up-pred
                    'next-of-node-type hickory.select/next-of-node-type
                    'prev-of-node-type hickory.select/prev-of-node-type
                    'left-of-node-type hickory.select/left-of-node-type
                    'right-of-node-type hickory.select/right-of-node-type
                    'after-subtree hickory.select/after-subtree
                    'select-next-loc hickory.select/select-next-loc
                    'select-locs hickory.select/select-locs
                    'select hickory.select/select
                    'node-type hickory.select/node-type
                    'tag hickory.select/tag
                    'attr hickory.select/attr
                    'id hickory.select/id
                    'class hickory.select/class
                    'any hickory.select/any
                    'element hickory.select/element
                    'element-child hickory.select/element-child
                    'root hickory.select/root
                    'find-in-text hickory.select/find-in-text
                    'n-moves-until hickory.select/n-moves-until
                    'nth-of-type hickory.select/nth-of-type
                    'nth-last-of-type hickory.select/nth-last-of-type
                    'nth-child hickory.select/nth-child
                    'nth-last-child hickory.select/nth-last-child
                    'first-child hickory.select/first-child
                    'last-child hickory.select/last-child
                    'and hickory.select/and
                    'or hickory.select/or
                    'not hickory.select/not
                    'el-not hickory.select/el-not
                    'ordered-adjacent hickory.select/ordered-adjacent
                    'child hickory.select/child
                    'follow-adjacent hickory.select/follow-adjacent
                    'precede-adjacent hickory.select/precede-adjacent
                    'ordered hickory.select/ordered
                    'descendant hickory.select/descendant
                    'follow hickory.select/follow
                    'precede hickory.select/precede
                    'has-descendant hickory.select/has-descendant
                    'has-child hickory.select/has-child
                    }
   'hickory.utils {
                   'void-element hickory.utils/void-element
                   'unescapable-content hickory.utils/unescapable-content
                   'html-escape hickory.utils/html-escape
                   'starts-with hickory.utils/starts-with
                   'lower-case-keyword hickory.utils/lower-case-keyword
                   'render-doctype hickory.utils/render-doctype
                   }
   'hickory.zip {
                 'hickory-zip hickory.zip/hickory-zip
                 'hiccup-zip hickory.zip/hickory-zip
                 }
   'enlive {
            '*options* net.cgrand.enlive-html/*options*
            'ns-options net.cgrand.enlive-html/ns-options
            'set-ns-options! net.cgrand.enlive-html/set-ns-options!
            'alter-ns-options! net.cgrand.enlive-html/alter-ns-options!
            'set-ns-parser! net.cgrand.enlive-html/set-ns-parser!
            'xml-parser net.cgrand.enlive-html/xml-parser
            'get-resource net.cgrand.enlive-html/get-resource
            'register-resource! net.cgrand.enlive-html/register-resource!
            'html-resource net.cgrand.enlive-html/html-resource
            'xml-resource net.cgrand.enlive-html/xml-resource
            'self-closing-tags net.cgrand.enlive-html/self-closing-tags
            'append! net.cgrand.enlive-html/append!
            'emit-tag net.cgrand.enlive-html/emit-tag
            'emit* net.cgrand.enlive-html/emit*
            'annotate net.cgrand.enlive-html/annotate
            'as-nodes net.cgrand.enlive-html/as-nodes
            'flatten-nodes-coll net.cgrand.enlive-html/flatten-nodes-coll
            'flatmap net.cgrand.enlive-html/flatmap
            'attr-values net.cgrand.enlive-html/attr-values
            'zip-pred net.cgrand.enlive-html/zip-pred
            'pred net.cgrand.enlive-html/pred
            'text-pred net.cgrand.enlive-html/text-pred
            're-pred net.cgrand.enlive-html/re-pred
            'whitespace net.cgrand.enlive-html/whitespace
            'any net.cgrand.enlive-html/any
            'tag= net.cgrand.enlive-html/tag=
            'id= net.cgrand.enlive-html/id=
            'attr-has net.cgrand.enlive-html/attr-has
            'has-class net.cgrand.enlive-html/has-class
            'intersection net.cgrand.enlive-html/intersection
            'union net.cgrand.enlive-html/union
            'cacheable net.cgrand.enlive-html/cacheable
            'cacheable? net.cgrand.enlive-html/cacheable?
            'fragment-selector? net.cgrand.enlive-html/fragment-selector?
            'node-selector? net.cgrand.enlive-html/node-selector?
            'transform net.cgrand.enlive-html/transform
            'lockstep-transform net.cgrand.enlive-html/lockstep-transform
            'at* net.cgrand.enlive-html/at*
            'zip-select-nodes* net.cgrand.enlive-html/zip-select-nodes*
            'select-nodes* net.cgrand.enlive-html/select-nodes*
            'zip-select-fragments* net.cgrand.enlive-html/zip-select-fragments*
            'select-fragments* net.cgrand.enlive-html/select-fragments*
            'select net.cgrand.enlive-html/select
            'zip-select net.cgrand.enlive-html/zip-select
            'content* net.cgrand.enlive-html/content
            'html-snippet net.cgrand.enlive-html/html-snippet
            'html-content net.cgrand.enlive-html/html-content
            'content enlive/content
            'wrap net.cgrand.enlive-html/wrap
            'unwrap net.cgrand.enlive-html/unwrap
            'replace-vars net.cgrand.enlive-html/replace-vars
            'replace-words net.cgrand.enlive-html/replace-words
            'set-attr net.cgrand.enlive-html/set-attr
            'remove-attr net.cgrand.enlive-html/remove-attr
            'add-class net.cgrand.enlive-html/add-class
            'remove-class net.cgrand.enlive-html/remove-class
            'do-> net.cgrand.enlive-html/do->
            'append net.cgrand.enlive-html/append
            'prepend net.cgrand.enlive-html/prepend
            'after net.cgrand.enlive-html/after
            'before net.cgrand.enlive-html/before
            'substitute net.cgrand.enlive-html/substitute
            'move net.cgrand.enlive-html/move
            'strict-mode* net.cgrand.enlive-html/strict-mode*
            'attr? net.cgrand.enlive-html/attr?
            'attr= net.cgrand.enlive-html/attr=
            'attr-starts net.cgrand.enlive-html/attr-starts
            'attr-ends net.cgrand.enlive-html/attr-ends
            'attr-contains net.cgrand.enlive-html/attr-contains
            'attr|= net.cgrand.enlive-html/attr|=
            'root net.cgrand.enlive-html/root
            'nth-child net.cgrand.enlive-html/nth-child
            'nth-last-child net.cgrand.enlive-html/nth-last-child
            'nth-of-type net.cgrand.enlive-html/nth-of-type
            'nth-last-of-type net.cgrand.enlive-html/nth-last-of-type
            'first-child net.cgrand.enlive-html/first-child
            'last-child net.cgrand.enlive-html/last-child
            'first-of-type net.cgrand.enlive-html/first-of-type
            'last-of-type net.cgrand.enlive-html/last-of-type
            'only-child net.cgrand.enlive-html/only-child
            'only-of-type net.cgrand.enlive-html/only-of-type
            'void net.cgrand.enlive-html/void
            'odd net.cgrand.enlive-html/odd
            'even net.cgrand.enlive-html/even
            'has net.cgrand.enlive-html/has
            'but-node net.cgrand.enlive-html/but-node
            'but net.cgrand.enlive-html/but
            'left net.cgrand.enlive-html/left
            'lefts net.cgrand.enlive-html/lefts
            'right net.cgrand.enlive-html/right
            'rights net.cgrand.enlive-html/rights
            'any-node net.cgrand.enlive-html/any-node
            'this-node net.cgrand.enlive-html/this-node
            'text-node net.cgrand.enlive-html/text-node
            'comment-node net.cgrand.enlive-html/comment-node
            'text net.cgrand.enlive-html/text
            'texts net.cgrand.enlive-html/texts
            'sniptest* net.cgrand.enlive-html/sniptest*
            'html net.cgrand.enlive-html/html

            ;; macros
            'at (with-meta @#'enlive/at {:sci/macro true})
            'transformation (with-meta @#'enlive/transformation {:sci/macro true})
            'lockstep-transformation (with-meta @#'enlive/lockstep-transformation {:sci/macro true})
            'snippet* (with-meta @#'enlive/snippet* {:sci/macro true})
            'snippet (with-meta @#'enlive/snippet {:sci/macro true})
            'template (with-meta @#'enlive/template {:sci/macro true})
            'defsnippet (with-meta @#'enlive/defsnippet {:sci/macro true})
            'deftemplate (with-meta @#'enlive/deftemplate {:sci/macro true})
            'defsnippets (with-meta @#'enlive/defsnippets {:sci/macro true})
            'transform-content (with-meta @#'enlive/transform-content {:sci/macro true})
            'clone-for (with-meta @#'enlive/clone-for {:sci/macro true})
            'strict-mode (with-meta @#'enlive/strict-mode {:sci/macro true})
            'let-select (with-meta @#'enlive/let-select {:sci/macro true})
            'sniptest (with-meta @#'enlive/sniptest {:sci/macro true})
            }

   'net.cgrand.enlive-html {
                            '*options* net.cgrand.enlive-html/*options*
                            'ns-options net.cgrand.enlive-html/ns-options
                            'set-ns-options! net.cgrand.enlive-html/set-ns-options!
                            'alter-ns-options! net.cgrand.enlive-html/alter-ns-options!
                            'set-ns-parser! net.cgrand.enlive-html/set-ns-parser!
                            'xml-parser net.cgrand.enlive-html/xml-parser
                            'get-resource net.cgrand.enlive-html/get-resource
                            'register-resource! net.cgrand.enlive-html/register-resource!
                            'html-resource net.cgrand.enlive-html/html-resource
                            'xml-resource net.cgrand.enlive-html/xml-resource
                            'self-closing-tags net.cgrand.enlive-html/self-closing-tags
                            'append! net.cgrand.enlive-html/append!
                            'emit-tag net.cgrand.enlive-html/emit-tag
                            'emit* net.cgrand.enlive-html/emit*
                            'annotate net.cgrand.enlive-html/annotate
                            'as-nodes net.cgrand.enlive-html/as-nodes
                            'flatten-nodes-coll net.cgrand.enlive-html/flatten-nodes-coll
                            'flatmap net.cgrand.enlive-html/flatmap
                            'attr-values net.cgrand.enlive-html/attr-values
                            'zip-pred net.cgrand.enlive-html/zip-pred
                            'pred net.cgrand.enlive-html/pred
                            'text-pred net.cgrand.enlive-html/text-pred
                            're-pred net.cgrand.enlive-html/re-pred
                            'whitespace net.cgrand.enlive-html/whitespace
                            'any net.cgrand.enlive-html/any
                            'tag= net.cgrand.enlive-html/tag=
                            'id= net.cgrand.enlive-html/id=
                            'attr-has net.cgrand.enlive-html/attr-has
                            'has-class net.cgrand.enlive-html/has-class
                            'intersection net.cgrand.enlive-html/intersection
                            'union net.cgrand.enlive-html/union
                            'cacheable net.cgrand.enlive-html/cacheable
                            'cacheable? net.cgrand.enlive-html/cacheable?
                            'fragment-selector? net.cgrand.enlive-html/fragment-selector?
                            'node-selector? net.cgrand.enlive-html/node-selector?
                            'transform net.cgrand.enlive-html/transform
                            'lockstep-transform net.cgrand.enlive-html/lockstep-transform
                            'at* net.cgrand.enlive-html/at*
                            'zip-select-nodes* net.cgrand.enlive-html/zip-select-nodes*
                            'select-nodes* net.cgrand.enlive-html/select-nodes*
                            'zip-select-fragments* net.cgrand.enlive-html/zip-select-fragments*
                            'select-fragments* net.cgrand.enlive-html/select-fragments*
                            'select net.cgrand.enlive-html/select
                            'zip-select net.cgrand.enlive-html/zip-select
                            'content* net.cgrand.enlive-html/content
                            'html-snippet net.cgrand.enlive-html/html-snippet
                            'html-content net.cgrand.enlive-html/html-content
                            'wrap net.cgrand.enlive-html/wrap
                            'unwrap net.cgrand.enlive-html/unwrap
                            'replace-vars net.cgrand.enlive-html/replace-vars
                            'replace-words net.cgrand.enlive-html/replace-words
                            'set-attr net.cgrand.enlive-html/set-attr
                            'remove-attr net.cgrand.enlive-html/remove-attr
                            'add-class net.cgrand.enlive-html/add-class
                            'remove-class net.cgrand.enlive-html/remove-class
                            'do-> net.cgrand.enlive-html/do->
                            'append* net.cgrand.enlive-html/append
                            'prepend* net.cgrand.enlive-html/prepend
                            'after* net.cgrand.enlive-html/after
                            'before* net.cgrand.enlive-html/before
                            'substitute* net.cgrand.enlive-html/substitute
                            'move net.cgrand.enlive-html/move
                            'strict-mode* net.cgrand.enlive-html/strict-mode*
                            'attr? net.cgrand.enlive-html/attr?
                            'attr= net.cgrand.enlive-html/attr=
                            'attr-starts net.cgrand.enlive-html/attr-starts
                            'attr-ends net.cgrand.enlive-html/attr-ends
                            'attr-contains net.cgrand.enlive-html/attr-contains
                            'attr|= net.cgrand.enlive-html/attr|=
                            'root net.cgrand.enlive-html/root
                            'nth-child net.cgrand.enlive-html/nth-child
                            'nth-last-child net.cgrand.enlive-html/nth-last-child
                            'nth-of-type net.cgrand.enlive-html/nth-of-type
                            'nth-last-of-type net.cgrand.enlive-html/nth-last-of-type
                            'first-child net.cgrand.enlive-html/first-child
                            'last-child net.cgrand.enlive-html/last-child
                            'first-of-type net.cgrand.enlive-html/first-of-type
                            'last-of-type net.cgrand.enlive-html/last-of-type
                            'only-child net.cgrand.enlive-html/only-child
                            'only-of-type net.cgrand.enlive-html/only-of-type
                            'void net.cgrand.enlive-html/void
                            'odd net.cgrand.enlive-html/odd
                            'even net.cgrand.enlive-html/even
                            'has net.cgrand.enlive-html/has
                            'but-node net.cgrand.enlive-html/but-node
                            'but net.cgrand.enlive-html/but
                            'left net.cgrand.enlive-html/left
                            'lefts net.cgrand.enlive-html/lefts
                            'right net.cgrand.enlive-html/right
                            'rights net.cgrand.enlive-html/rights
                            'any-node net.cgrand.enlive-html/any-node
                            'this-node net.cgrand.enlive-html/this-node
                            'text-node net.cgrand.enlive-html/text-node
                            'comment-node net.cgrand.enlive-html/comment-node
                            'text net.cgrand.enlive-html/text
                            'texts net.cgrand.enlive-html/texts
                            'sniptest* net.cgrand.enlive-html/sniptest*
                            'html net.cgrand.enlive-html/html

                            ;; macros
                            'at (with-meta @#'enlive/at {:sci/macro true})
                            'transformation (with-meta @#'enlive/transformation {:sci/macro true})
                            'lockstep-transformation (with-meta @#'enlive/lockstep-transformation {:sci/macro true})
                            'snippet* (with-meta @#'enlive/snippet* {:sci/macro true})
                            'snippet (with-meta @#'enlive/snippet {:sci/macro true})
                            'template (with-meta @#'enlive/template {:sci/macro true})
                            'defsnippet (with-meta @#'enlive/defsnippet {:sci/macro true})
                            'deftemplate (with-meta @#'enlive/deftemplate {:sci/macro true})
                            'defsnippets (with-meta @#'enlive/defsnippets {:sci/macro true})
                            'transform-content (with-meta @#'enlive/transform-content {:sci/macro true})
                            'clone-for (with-meta @#'enlive/clone-for {:sci/macro true})
                            'strict-mode (with-meta @#'enlive/strict-mode {:sci/macro true})
                            'let-select (with-meta @#'enlive/let-select {:sci/macro true})
                            'sniptest (with-meta @#'enlive/sniptest {:sci/macro true})

                            'content enlive/content
                            'append enlive/append
                            'prepend enlive/prepend
                            'after enlive/after
                            'before enlive/before
                            'substitute enlive/substitute
                            }

   'net.cgrand.jsoup {'parser net.cgrand.jsoup/parser}
   'net.cgrand.tagsoup {
                        'parser net.cgrand.tagsoup/parser
                        }

   'net.cgrand.xml {
                    'tag net.cgrand.xml/tag
                    'attrs net.cgrand.xml/attrs
                    'content net.cgrand.xml/content
                    'tag? net.cgrand.xml/tag?
                    'comment? net.cgrand.xml/comment?
                    'dtd? net.cgrand.xml/dtd?
                    'xml-zip net.cgrand.xml/xml-zip
                    'startparse-sax net.cgrand.xml/startparse-sax
                    'parse net.cgrand.xml/parse
                    }

   'selmer.filter-parser {
                          'escape-html* selmer.filter-parser/escape-html*
                          'strip-doublequotes selmer.filter-parser/strip-doublequotes
                          'escape-html selmer.filter-parser/escape-html
                          'fix-filter-args selmer.filter-parser/fix-filter-args
                          'lookup-args selmer.filter-parser/lookup-args
                          'filter-str->fn selmer.filter-parser/filter-str->fn
                          'safe-filter selmer.filter-parser/safe-filter
                          'literal? selmer.filter-parser/literal?
                          'get-accessor selmer.filter-parser/get-accessor
                          'split-value selmer.filter-parser/split-value
                          'compile-filter-body selmer.filter-parser/compile-filter-body
                          }

   'selmer.filters {
                    'valid-date-formats selmer.filters/valid-date-formats
                    'fix-date selmer.filters/fix-date
                    'parse-number selmer.filters/parse-number
                    'throw-when-expecting-seqable selmer.filters/throw-when-expecting-seqable
                    'throw-when-expecting-number selmer.filters/throw-when-expecting-number
                    'filters selmer.filters/filters
                    'get-filter selmer.filters/get-filter
                    'call-filter selmer.filters/call-filter
                    'add-filter! selmer.filters/add-filter!
                    'remove-filter! selmer.filters/remove-filter!
                    }

   'selmer.middleware {
                       'handle-template-parsing-error selmer.middleware/handle-template-parsing-error
                       'wrap-error-page selmer.middleware/wrap-error-page
                       }

   'selmer.node {
                 'INode selmer.node/INode
                 }

   'selmer.parser {
                   'templates selmer.parser/templates
                   'cache? selmer.parser/cache?
                   'cache-on! selmer.parser/cache-on!
                   'cache-off! selmer.parser/cache-off!
                   'set-resource-path! selmer.parser/set-resource-path!
                   'update-tag selmer.parser/update-tag
                   'set-closing-tags! selmer.parser/set-closing-tags!
                   'remove-tag! selmer.parser/remove-tag!
                   'render-template selmer.parser/render-template
                   'render selmer.parser/render
                   'render-file selmer.parser/render-file
                   'expr-tag selmer.parser/expr-tag
                   'filter-tag selmer.parser/filter-tag
                   'parse-tag selmer.parser/parse-tag
                   'append-node selmer.parser/append-node
                   'update-tags selmer.parser/update-tags
                   'tag-content selmer.parser/tag-content
                   'skip-short-comment-tag selmer.parser/skip-short-comment-tag
                   'add-node selmer.parser/add-node
                   'parse* selmer.parser/parse*
                   'parse-input selmer.parser/parse-input
                   'parse-file selmer.parser/parse-file
                   'parse selmer.parser/parse
                   'known-variables selmer.parser/known-variables

                   ;; macros
                   'add-tag! (with-meta @#'selmer/add-tag! {:sci/macro true})
                   }

   'selmer.tags {
                 'create-value-mappings selmer.tags/create-value-mappings
                 'aggregate-args selmer.tags/aggregate-args
                 'compile-filters selmer.tags/compile-filters
                 'apply-filters selmer.tags/apply-filters
                 'for-handler selmer.tags/for-handler
                 'render-if selmer.tags/render-if
                 'if-result selmer.tags/if-result
                 'if-default-handler selmer.tags/if-default-handler
                 'match-comparator selmer.tags/match-comparator
                 ;;'num? selmer.tags/num?
                 'parse-numeric-params selmer.tags/parse-numeric-params
                 'render-if-numeric selmer.tags/render-if-numeric
                 'if-numeric-handler selmer.tags/if-numeric-handler
                 'render-if-any-all selmer.tags/render-if-any-all
                 'if-handler selmer.tags/if-handler
                 'compare-tag selmer.tags/compare-tag
                 'parse-eq-args selmer.tags/parse-eq-args
                 'ifequal-handler selmer.tags/ifequal-handler
                 'ifunequal-handler selmer.tags/ifunequal-handler
                 'block-handler selmer.tags/block-handler
                 'sum-handler selmer.tags/sum-handler
                 'now-handler selmer.tags/now-handler
                 'comment-handler selmer.tags/comment-handler
                 'first-of-handler selmer.tags/first-of-handler
                 'read-verbatim selmer.tags/read-verbatim
                 'verbatim-handler selmer.tags/verbatim-handler
                 'compile-args selmer.tags/compile-args
                 'with-handler selmer.tags/with-handler
                 'script-handler selmer.tags/script-handler
                 'style-handler selmer.tags/style-handler
                 'cycle-handler selmer.tags/cycle-handler
                 'safe-handler selmer.tags/safe-handler
                 'debug-handler selmer.tags/debug-handler
                 'expr-tags selmer.tags/expr-tags
                 'closing-tags selmer.tags/closing-tags
                 'render-tags selmer.tags/render-tags
                 'tag-handler selmer.tags/tag-handler
                 }

   'selmer.template-parser {
                            'get-tag-params selmer.template-parser/get-tag-params
                            'parse-defaults selmer.template-parser/parse-defaults
                            'split-include-tag selmer.template-parser/split-include-tag
                            'string->reader selmer.template-parser/string->reader
                            'insert-includes selmer.template-parser/insert-includes
                            'get-parent selmer.template-parser/get-parent
                            'write-tag? selmer.template-parser/write-tag?
                            'consume-block selmer.template-parser/consume-block
                            'rewrite-super selmer.template-parser/rewrite-super
                            'read-block selmer.template-parser/read-block
                            'process-block selmer.template-parser/process-block
                            'wrap-in-expression-tag selmer.template-parser/wrap-in-expression-tag
                            'wrap-in-variable-tag selmer.template-parser/wrap-in-variable-tag
                            'trim-regex selmer.template-parser/trim-regex
                            'trim-variable-tag selmer.template-parser/trim-variable-tag
                            'trim-expression-tag selmer.template-parser/trim-expression-tag
                            'to-expression-string selmer.template-parser/to-expression-string
                            'add-default selmer.template-parser/add-default
                            'try-add-default selmer.template-parser/try-add-default
                            'add-defaults-to-variable-tag selmer.template-parser/add-defaults-to-variable-tag
                            'add-defaults-to-expression-tag selmer.template-parser/add-defaults-to-expression-tag
                            'get-template-path selmer.template-parser/get-template-path
                            'read-template selmer.template-parser/read-template
                            'preprocess-template selmer.template-parser/preprocess-template
                            }

   'selmer.util {
                 'set-custom-resource-path! selmer.util/set-custom-resource-path!
                 'turn-off-escaping! selmer.util/turn-off-escaping!
                 'turn-on-escaping! selmer.util/turn-on-escaping!
                 'pattern selmer.util/pattern
                 'read-char selmer.util/read-char
                 'assoc-in* selmer.util/assoc-in*
                 '*tag-open* selmer.util/*tag-open*
                 '*tag-close* selmer.util/*tag-close*
                 '*filter-open* selmer.util/*filter-open*
                 '*filter-close* selmer.util/*filter-close*
                 '*tag-second* selmer.util/*tag-second*
                 '*short-comment-second* selmer.util/*short-comment-second*
                 '*tag-second-pattern* selmer.util/*tag-second-pattern*
                 '*filter-open-pattern* selmer.util/*filter-open-pattern*
                 '*filter-close-pattern* selmer.util/*filter-close-pattern*
                 '*filter-pattern* selmer.util/*filter-pattern*
                 '*tag-open-pattern* selmer.util/*tag-open-pattern*
                 '*tag-close-pattern* selmer.util/*tag-close-pattern*
                 '*tag-pattern* selmer.util/*tag-pattern*
                 '*include-pattern* selmer.util/*include-pattern*
                 '*extends-pattern* selmer.util/*extends-pattern*
                 '*block-pattern* selmer.util/*block-pattern*
                 '*block-super-pattern* selmer.util/*block-super-pattern*
                 '*endblock-pattern* selmer.util/*endblock-pattern*
                 '*tags* selmer.util/*tags*
                 'check-tag-args selmer.util/check-tag-args
                 'read-tag-info selmer.util/read-tag-info
                 'peek-rdr selmer.util/peek-rdr
                 'read-tag-content selmer.util/read-tag-content
                 'open-tag? selmer.util/open-tag?
                 'open-short-comment? selmer.util/open-short-comment?
                 'split-by-args selmer.util/split-by-args
                 'resource-path selmer.util/resource-path
                 'resource-last-modified selmer.util/resource-last-modified
                 'check-template-exists selmer.util/check-template-exists
                 'default-missing-value-formatter selmer.util/default-missing-value-formatter
                 '*missing-value-formatter* selmer.util/*missing-value-formatter*
                 '*filter-missing-values* selmer.util/*filter-missing-values*
                 'set-missing-value-formatter! selmer.util/set-missing-value-formatter!
                 'fix-accessor selmer.util/fix-accessor
                 'parse-accessor selmer.util/parse-accessor

                 ;; macro
                 ;; 'exception (with-meta @#'selmer/exception {:sci/macro true})
                 'with-escaping (with-meta @#'selmer/with-escaping {:sci/macro true})
                 'without-escaping (with-meta @#'selmer/without-escaping {:sci/macro true})
                 '->buf (with-meta @#'selmer/->buf {:sci/macro true})
                 }

   'selmer.validator {
                      'error-template selmer.validator/error-template
                      'validate? selmer.validator/validate?
                      'validate-on! selmer.validator/validate-on!
                      'validate-off! selmer.validator/validate-off!
                      'format-tag selmer.validator/format-tag
                      'validation-error selmer.validator/validation-error
                      'validate-filters selmer.validator/validate-filters
                      'close-tags selmer.validator/close-tags
                      'valide-tag selmer.validator/valide-tag
                      'skip-verbatim-tags selmer.validator/skip-verbatim-tags
                      'read-tag selmer.validator/read-tag
                      'validate-tags selmer.validator/validate-tags
                      'validate selmer.validator/validate
                      }

   'clojure.walk {
                  'walk clojure.walk/walk
                  'postwalk clojure.walk/postwalk
                  'prewalk clojure.walk/prewalk
                  'postwalk-demo clojure.walk/postwalk-demo
                  'prewalk-demo clojure.walk/prewalk-demo
                  'keywordize-keys clojure.walk/keywordize-keys
                  'stringify-keys clojure.walk/stringify-keys
                  'prewalk-replace clojure.walk/prewalk-replace
                  'postwalk-replace clojure.walk/postwalk-replace
                  }

   'clojure.tools.cli {
                       'cli clojure.tools.cli/cli
                       'make-summary-part clojure.tools.cli/make-summary-part
                       'format-lines clojure.tools.cli/format-lines
                       'summarize clojure.tools.cli/summarize
                       'get-default-options clojure.tools.cli/get-default-options
                       'parse-opts clojure.tools.cli/parse-opts
                       }

   ;; https://github.com/borkdude/sci/issues/124
   'clojure.core {
                  'find-ns (fn [_] 'user)
                  'parse-string parse-string
                  'parse-int #(Integer/parseInt %)
                  'println println
                  'pprint utils/pprint
                  'pprint-str utils/pprint-str
                  'slurp utils/slurp-relative
                  'spit utils/spit-relative
                  }
   })
