(ns authorizer.schema
  (:require [schema.core :as s])
  (:import [java.time OffsetDateTime]))

(s/defschema Transaction
  "A transaction is a type of operation that occurs on an Account"
  {:merchant s/Str
   :amount s/Int
   :time OffsetDateTime})

(s/defschema Account
  "An account as in the form persisted in the atom"
  {:active-card s/Bool
   :available-limit s/Int
   :initialized s/Bool
   :history [Transaction]})

(s/defschema PublicTransaction
  {:transaction {:merchant s/Str
                 :amount s/Int
                 :time s/Str}})

(s/defschema PublicAccount
  {:account {:active-card s/Bool
             :available-limit s/Int}})

(def OperationResult
  {:account Account
   (s/optional-key :violations) [s/Keyword]})

(def PublicOperationResult
  (assoc PublicAccount (s/optional-key :violations) [s/Keyword]))

(s/defn OperationResult->PublicOperationResult :- PublicOperationResult
  [result :- OperationResult]
  (-> result
      (update-in [:account] dissoc :initialized)
      (update-in [:account] dissoc :history)))

(s/defn PublicTransaction->Transaction :- Transaction
  [{:keys [transaction]} :- PublicTransaction]
  (let [time (:time transaction)]
    (assoc transaction :time (OffsetDateTime/parse time))))

(def ValidationFn (s/make-fn-schema (s/maybe s/Keyword) [[Account Transaction]]))

(s/defschema Validation {:type (s/enum :account :account-and-transaction)
                         :rule ValidationFn})

(s/defschema Validations [Validation])
