(mustache "quickstart.html"
          (assoc (yaml "fields.yml")
                 :body (markdown "simple.md" :html)))
