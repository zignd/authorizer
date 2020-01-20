(ns authorizer.core
  (:gen-class)
  (:require [authorizer.dispatcher :refer [dispatch!]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (dispatch! *in*))
