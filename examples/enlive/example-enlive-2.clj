(require '[net.cgrand.enlive-html :as html])

(html/deftemplate main-template "templates/application.html" [context]
  [:head :title] (html/content (:heading context)))

(html/defsnippet main-snippet "templates/header.html" [:header] [heading navigation-elements]
  [:h1] (html/content heading)
  [:ul [:li html/first-of-type]] (html/clone-for [[caption url] navigation-elements]
                                                 [:li :a] (html/content caption)
                                                 [:li :a] (html/set-attr :href url)))

#_ (->> (main-template {:heading "a new heading"})
     ;;html/emit*
     (apply str)
    )

(apply str (main-template {:heading (main-snippet "heading" [["caption" "url"] ["caption 2" "url 2"]])}))
