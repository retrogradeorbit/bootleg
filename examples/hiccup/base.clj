[:html
 [:body
  [:h1 "My resume"]

  [:div#intro (markdown "intro.md")]

  (for [project (json "projects.json")]
    [:div [:p (:title project)]
     [:p (:description project)]])

  [:div#experience (load-file "experience.clj")]]]
