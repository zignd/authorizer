(ns authorizer.adapters
  (:require [schema.core :as s]
            [cheshire.core :as json]))

(s/defn from-json-to-map :- {s/Keyword s/Any}
  [data :- s/Str]
  (json/decode data true))

(s/defn from-map-to-json :- s/Str
  [data :- {s/Keyword s/Any}]
  (json/encode data))


