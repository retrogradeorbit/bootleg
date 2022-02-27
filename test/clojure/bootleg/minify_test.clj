(ns bootleg.minify-test
  (:require [clojure.test :refer :all]
            [bootleg.minify :refer :all]))

(def html
  )

(deftest minify-html-tests
  (testing "minify html"

    (is (= "<div arg=\" foo \"> test </div>"
           (compress-html "<div  arg=\" foo \" >\n\n  test  </div>")))

    (is (= (compress-html
            "<a x=\"aaa 'bbb' ccc\"  b=\"1\">   <b   onclick  =  \"  alert('<a>   <b>')  \"  /> <c/>
   <d />
  <pre    a   =   \"  1  \">    <x   />    <y>   </pre>
<!-- comment -->
  <!--[if lt IE 7 ]>        <body class=\"ie6\">      <![endif]-->           <!--[if lt IE 7 ]>        <body class=\"ie6\">      <![endif]-->
<pre>          <!-- comment -->     </pre>
<textarea>          <!-- comment -->     </textarea>
<script>          <!-- comment -->     </script>
  <script type=\"text/x-jquery-tmpl\">     <a>     <!-- comment -->   <b>   </script>
<style>          <!-- comment -->     </style>"
            {:remove-comments true
             :remove-intertag-spaces true
             :remove-link-attributes true
             :remove-multi-spaces true
             :remove-quotes true
             :remove-script-attributes true})
           "<a x=\"aaa 'bbb' ccc\" b=1><b onclick=\"  alert('<a>   <b>')  \"/><c/><d/><pre a=\" 1 \">    <x   />    <y>   </pre><!--[if lt IE 7 ]><body class=ie6><![endif]--><!--[if lt IE 7 ]><body class=ie6><![endif]--><pre>          <!-- comment -->     </pre><textarea>          <!-- comment -->     </textarea><script>          <!-- comment -->     </script><script type=\"text/x-jquery-tmpl\"><a><b></script><style>          <!-- comment -->     </style>"))



    (is (= (compress-html
            "<a>


        <b>


                <c/>


        </b>
</a>"
            {:preserve-line-breaks true})
           "<a>
<b>
<c/>
</b>
</a>"))

    (is (= (compress-html "<p>a      b</p><p>a      c</p>" {:preserve-patterns [#"a\s+b"]})
           "<p>a      b</p><p>a c</p>"))

    (is (= (compress-html
            "<form method=\"get\"><button type=\"submit\">submit</button></form>"
            {:remove-form-attributes true})
           "<form><button type=\"submit\">submit</button></form>"))

    (is (= (compress-html
            "<a href=\"http://foo\">link</a>"
            {:remove-http-protocol true})
           "<a href=\"//foo\">link</a>"))

    (is (= (compress-html
            "<a href=\"https://foo\">link</a>"
            {:remove-https-protocol true})
           "<a href=\"//foo\">link</a>"))

    (is (= (compress-html
            "<input type=\"text\"></input>"
            {:remove-input-attributes true})
           "<input></input>"))

    (is (= (compress-html
            "<button onlick=\"javascript: alert(\"click\")\">click me</button>"
            {:remove-javascript-protocol true})
           "<button onlick=\"alert(\"click\")\">click me</button>"))

    (is (= (compress-html
            "<link rel=\"stylesheet\" type=\"text/css\">"
            {:remove-link-attributes true})
           "<link rel=\"stylesheet\">"))

    (is (= (compress-html
            "<a b=\"c\" d=\"e, f, g\" e=\"4\">"
            {:remove-quotes true})
           "<a b=c d=\"e, f, g\" e=4>"))

    (is (= (compress-html
            "<script type=\"text/javascript\" language=\"javascript\">"
            {:remove-script-attributes true})
           "<script>"))

    (is (= (compress-html
            "<style type=\"text/style\">"
            {:remove-style-attributes true})
           "<style>"))

    (is (= (compress-html
            "aaa <br> <p style=\"\"> bbbb <br/> ccccccc </p> <b> ddddd </b> eeeeeeee"
            {:remove-intertag-spaces true
             :remove-surrounding-spaces [:p "br"]})
           "aaa<br><p style=\"\">bbbb<br/>ccccccc</p><b> ddddd </b> eeeeeeee"))

    (is (= (compress-html
            "<input readonly=\"readonly\">"
            {:simple-boolean-attributes true})
           "<input readonly>"))

    (is (= (compress-html
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            {:simple-doctype true})
           "<!DOCTYPE html>"))

    (is (= (compress-html
            "<style>
h1, h2:
    font-family: sans-serif;
    font-weight: 900;

p:
    font-size: 12pt;

</style>
"
            {:compress-css true})
           "<style>h1,h2:font-family:sans-serif;font-weight:900;p:font-size:12pt;</style>"))

    #_ (compress-html "<script type=\"text/javascript\">var foo;
var bar=4;
 "
                     {:javascript-compressor :google-closure})


    ;; (is (= (compress-html
    ;;         ""
    ;;         {})
    ;;        ""))

    ;; (is (= (compress-html
    ;;         ""
    ;;         {})
    ;;        ""))





    ))
