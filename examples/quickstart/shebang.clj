#!/usr/bin/env bootleg

[:div
 [:h1 "Command Line Args"]
 [:ul
  (for [arg *command-line-args*]
    [:li arg])]]
