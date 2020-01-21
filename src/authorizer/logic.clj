(ns authorizer.logic
  (:require [schema.core :as s]
            [authorizer.schema :refer [Map Account OperationResult
                                       Transaction]]))

(s/defn create-account :- OperationResult
  [account :- {(s/optional-key :account) Map}
   active-card :- s/Bool
   available-limit :- s/Int]
  (if (not (s/check Account account))
    (assoc account :violations [:account-already-initialized])
    (assoc account :account {:active-card active-card
                             :available-limit available-limit})))

(s/defn authorize-transaction :- OperationResult
  [account :- Account
   transaction :- Transaction]
  account)
