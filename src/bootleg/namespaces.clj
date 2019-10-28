(ns bootleg.namespaces
  (:require [bootleg.utils :as utils]
            [bootleg.enlive :as enlive]
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
            [sci.core :as sci]
            [clojure.walk]))

(defn at [node-or-nodes & selector-transform-pairs]
  (let [input-type (utils/markup-type node-or-nodes)
        converted-nodes (utils/convert-to node-or-nodes :hickory-seq)]
    (->
     (reduce
      (fn [node [selector transform]]
        (enlive/at node selector transform))
      converted-nodes
      (partition 2 selector-transform-pairs))

     (utils/convert-to input-type))))

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
   'enlive {'at at
            'content enlive-html/content
            'html-content enlive-html/html-content
            'wrap enlive-html/wrap
            'unwrap enlive-html/unwrap
            'set-attr enlive-html/set-attr
            'remove-attr enlive-html/remove-attr
            'add-class enlive-html/add-class
            'remove-class enlive-html/remove-class
            'do-> enlive-html/do->
            'append enlive-html/append
            'prepend enlive-html/prepend
            'after enlive-html/after
            'before enlive-html/before
            'substitute enlive-html/substitute
            'move enlive-html/move

            ;; macro
            'clone-for (with-meta @#'enlive-html/clone-for {:sci/macro true})

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
                            'content net.cgrand.enlive-html/content
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
                            'at (with-meta enlive/at {:sci/macro true})
                            'transformation (with-meta enlive/transformation {:sci/macro true})
                            'lockstep-transformation (with-meta enlive/lockstep-transformation {:sci/macro true})
                            'snippet* (with-meta enlive/snippet* {:sci/macro true})
                            'snippet (with-meta enlive/snippet {:sci/macro true})
                            'template (with-meta enlive/template {:sci/macro true})
                            'defsnippet (with-meta enlive/defsnippet {:sci/macro true})
                            'deftemplate (with-meta enlive/deftemplate {:sci/macro true})
                            'defsnippets (with-meta enlive/defsnippets {:sci/macro true})
                            'transform-content (with-meta enlive/transform-content {:sci/macro true})
                            'clone-for (with-meta enlive/clone-for {:sci/macro true})
                            'strict-mode (with-meta enlive/strict-mode {:sci/macro true})
                            'let-select (with-meta enlive/let-select {:sci/macro true})
                            'sniptest (with-meta enlive/sniptest {:sci/macro true})
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

   'clojure.walk {
                  'walk clojure.walk/walk
                  'postwalk clojure.walk/postwalk
                  'prewalk clojure.walk/prewalk
                  'postwalk-demo clojure.walk/postwalk-demo
                  'prewalk-demo clojure.walk/prewalk-demo
                  'kewordize-keys clojure.walk/keywordize-keys
                  'stringify-keys clojure.walk/stringify-keys
                  'prewalk-replace clojure.walk/prewalk-replace
                  'postwalk-replace clojure.walk/postwalk-replace
                  }

   ;; https://github.com/borkdude/sci/issues/124
   'extra-core {
                'find-ns (fn [_] 'user)
                'doall doall
                }
   })
