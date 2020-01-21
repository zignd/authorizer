(ns authorizer.dispatcher
  (:require [schema.core :as s]
            [authorizer
             [controllers :as controllers]
             [adapters :as adapters]
             [data :as data]
             [schema :refer [Map]]])
  (:import [java.io BufferedReader Reader]))

(s/defn ^:private resolve-controller :- (s/pred ifn?)
  [operation :- {s/Keyword s/Any}]
  (cond
    (:account operation) controllers/create-account!
    (:transaction operation) controllers/authorize-transaction!
    :else (throw (ex-info "Invalid operation type" {:operation operation}))))

(s/defn dispatch-line!
  [account-atom :- (s/atom Map)
   line :- s/Str]
  (-> line
      (adapters/from-json-to-map)
      (as-> operation
            ((resolve-controller operation) account-atom operation))
      (adapters/from-map-to-json)
      (println)))

(s/defn dispatch!
  [rdr :- Reader]
  (let [account-atom (data/initialize!)]
    (doseq [line (line-seq (BufferedReader. rdr))]
      (dispatch-line! account-atom line))))
