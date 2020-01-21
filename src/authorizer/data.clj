(ns authorizer.data
  (:require [schema.core :as s]
            [authorizer.schema :refer [Map]]))

(s/defn initialize! :- (s/atom Map)
  []
  (atom {}))

(s/defn update!
  [a :- (s/atom Map)
   account :- Map]
  (reset! a account))
