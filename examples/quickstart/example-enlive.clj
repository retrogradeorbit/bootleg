(-> (markdown "examples/quickstart/simple.md")
    (enlive/at [:p] (enlive/set-attr :style "color:green;")))
