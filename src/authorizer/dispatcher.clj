(ns authorizer.dispatcher
  (:require [schema.core :as s]
            [authorizer.controllers :as controllers]
            [authorizer.adapters :as adapters])
  (:import [java.io BufferedReader Reader]))

(s/defn ^:private resolve-controller
  [operation]
  (cond
    (:account operation) controllers/create-account!
    (:transaction operation) controllers/authorize-transaction!
    :else (throw (ex-info "Invalid operation type" {:operation operation}))))

(s/defn dispatch-line!
  [line :- s/Str]
  (-> line
      (adapters/from-json-to-map)
      (as-> op ((resolve-controller op) op))
      (adapters/from-map-to-json)
      (println)))

(s/defn dispatch!
  [rdr :- Reader]
  (doseq [line (line-seq (BufferedReader. rdr))]
    (dispatch-line! line)))
