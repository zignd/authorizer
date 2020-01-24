(ns authorizer.adapters
  (:require [schema.core :as s]
            [cheshire.core :as json]))

(s/defn from-json-to-map :- {s/Keyword s/Any}
  "Decodes a JSON string into a map with keys."
  [data :- s/Str]
  (json/decode data true))

(s/defn from-map-to-json :- s/Str
  "Encodes a map into a JSON string."
  [data :- {s/Keyword s/Any}]
  (json/encode data))


