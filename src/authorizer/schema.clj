(ns authorizer.schema
  (:require [schema.core :as s])
  (:import [java.util Date]))

(def Map
  "Schema for a map"
  (s/pred map?))

(s/defschema Transaction
  "A transaction is a type of operation that occurs on an Account"
  {:merchant s/Str
   :amount s/Int
   :time s/Str})

(s/defschema Account
  "An account as in the form persisted in the atom"
  {:active-card s/Bool
   :available-limit s/Int
   :initialized s/Bool
   :history [Transaction]})

(s/defschema PublicTransaction
  {:transaction Transaction})

(s/defschema PublicAccount
  {:account {:active-card s/Bool
             :available-limit s/Int}})

(def OperationResult
  {:account Account
   (s/optional-key :violations) [s/Keyword]})

(def PublicOperationResult
  (assoc PublicAccount (s/optional-key :violations) [s/Keyword]))

(def ValidationFn (s/make-fn-schema (s/maybe s/Keyword) [[Account Transaction]]))

(s/defschema Validation {:type (s/enum :account :account-and-transaction)
                         :rule ValidationFn})

(s/defschema Validations [Validation])
