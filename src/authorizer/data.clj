(ns authorizer.data
  (:require [schema.core :as s]
            [authorizer.schema :refer [Account]]))

(s/defn initialize! :- (s/atom Account)
  []
  (atom {:active-card false
         :available-limit 0
         :initialized false
         :history []}))

(s/defn update!
  [a :- (s/atom Account)
   account :- Account]
  (reset! a account))
