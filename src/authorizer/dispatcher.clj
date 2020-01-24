(ns authorizer.dispatcher
  (:require [schema.core :as s]
            [authorizer
             [controllers :as controllers]
             [adapters :as adapters]
             [data :as data]
             [schema :refer [Account]]])
  (:import [java.io BufferedReader Reader]
           [com.fasterxml.jackson.core JsonParseException]))

(s/defn ^:private resolve-controller :- (s/pred ifn?)
  "Based on the operation type its decided which of the
   available controllers will be in charge of handling it."
  [operation :- {s/Keyword s/Any}]
  (cond
    (:account operation) controllers/create-account!
    (:transaction operation) controllers/authorize-transaction!
    :else (throw (ex-info "Invalid operation type" {:operation operation}))))

(s/defn dispatch-line! :- s/Str
  "The STDIN is parsed in lines, every line is expected to be a valid JSON
   data structure. This function will take this line, parse it, and invoke
   the appropriate controller to handle it. The output is a JSON data structure
   as a string."
  [account-atom :- (s/atom Account)
   line :- s/Str]
  (try (-> line
           (adapters/from-json-to-map)
           (as-> operation
                 ((resolve-controller operation) account-atom operation))
           (adapters/from-map-to-json))
       (catch JsonParseException e
         (adapters/from-map-to-json {:error "Invalid JSON"}))
       (catch Exception e
         (adapters/from-map-to-json {:error (.getMessage e)}))))

(s/defn dispatch!
  "Operates on each line of the STDIN and follows by writing the results to the STDOUT."
  [rdr :- Reader]
  (let [account-atom (data/initialize!)]
    (doseq [line (line-seq (BufferedReader. rdr))]
      (println (dispatch-line! account-atom line)))))
