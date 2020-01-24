(ns authorizer.schema
  (:require [schema.core :as s])
  (:import [java.time OffsetDateTime]))

(s/defschema Transaction
  "A transaction is a type of operation that occurs in an account."
  {:merchant s/Str
   :amount s/Int
   :time OffsetDateTime})

(s/defschema Account
  "An account as in the form persisted in the atom."
  {:active-card s/Bool
   :available-limit s/Int
   :initialized s/Bool
   :history [Transaction]})

(s/defschema PublicTransaction
  "The transaction form accepted from the outside."
  {:transaction {:merchant s/Str
                 :amount s/Int
                 :time s/Str}})

(s/defschema PublicAccount
  "The account form accepted from the outside. It's also used return results to the outside."
  {:account {:active-card s/Bool
             :available-limit s/Int}})

(def OperationResult
  "Every operation generates an operation result, which may or may not have violations."
  {:account Account
   (s/optional-key :violations) [s/Keyword]})

(def PublicOperationResult
  "Just like OperationResult, but the form exposed to the outside."
  (assoc PublicAccount (s/optional-key :violations) [s/Keyword]))

(s/defn OperationResult->PublicOperationResult :- PublicOperationResult
  "Converts from OperationResult to PublicOperationResult."
  [result :- OperationResult]
  (-> result
      (update-in [:account] dissoc :initialized)
      (update-in [:account] dissoc :history)))

(s/defn PublicTransaction->Transaction :- Transaction
  "Converts from PublicTransaction to Transaction."
  [{:keys [transaction]} :- PublicTransaction]
  (let [time (:time transaction)]
    (assoc transaction :time (OffsetDateTime/parse time))))

(def ValidationFn (s/make-fn-schema (s/maybe s/Keyword) [[Account Transaction]]))

(s/defschema Validation {:type (s/enum :account :account-and-transaction)
                         :rule ValidationFn})

(s/defschema Validations [Validation])
