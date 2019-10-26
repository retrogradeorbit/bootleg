(require '[net.cgrand.enlive-html :as html])


(html/deftemplate main-template "templates/application.html"
  []
  [:head :title] (html/content "Enlive starter kit"))

[:div]
