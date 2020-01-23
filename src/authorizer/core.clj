(ns authorizer.core
  (:gen-class)
  (:require [authorizer.dispatcher :refer [dispatch!]]))

(defn -main
  [& args]
  (dispatch! *in*))
