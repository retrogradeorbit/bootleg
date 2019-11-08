(-> [:div (markdown "simple.md")]
    (convert-to :hickory)
    (net.cgrand.enlive-html/at [:p] (net.cgrand.enlive-html/set-attr :style "color:green;"))

    )
