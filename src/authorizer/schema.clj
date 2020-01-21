(ns authorizer.schema
  (:require [schema.core :as s])
  (:import [java.util Date]))

(def Map
  "Schema for a map"
  (s/pred map?))

(s/defschema Account
  "An account as in the form persisted in the atom"
  {:account {:active-card s/Bool
             :available-limit s/Int}})

(def OperationResult
  "The result of an operation"
  (assoc Account (s/optional-key :violations) [s/Keyword]))

(s/defschema Transaction
  "A transaction is a type of operation that occurs on an Account"
  {:transaction {:merchant s/Str
                 :amount s/Int
                 ;; TODO: use a coercer
                 ;; :time Date
                 :time s/Str}})
