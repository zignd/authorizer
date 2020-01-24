(ns authorizer.data
  (:require [schema.core :as s]
            [authorizer.schema :refer [Account]]))

(s/defn initialize! :- (s/atom Account)
  "Initializes an atom according to the schema for 
   accounts used throughout the application."
  []
  (atom {:active-card false
         :available-limit 0
         :initialized false
         :history []}))

(s/defn update!
  "Persists in the atom the altered atom representing an account."
  [a :- (s/atom Account)
   account :- Account]
  (reset! a account))
