(let [page (markdown "simple.md")
      page-green (enlive/at page [:p] (enlive/set-attr :style "color:green;"))]
  (spit "greens.html" (as-html page-green))
  page)
