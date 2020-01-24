(ns authorizer.logic.operations
  (:require [schema.core :as s]
            [authorizer.schema :refer [Account Transaction OperationResult Validations]]
            [authorizer.logic
             [helpers :refer [validate op-res op-res-with-violation]]
             [rules :as rules]]))

(s/def validations :- Validations
  [{:type :account-and-transaction
    :rule rules/validate-doubled-transaction}
   {:type :account-and-transaction
    :rule rules/validate-high-frequency-small-interval}
   {:type :account-and-transaction
    :rule rules/validate-insufficient-limit}
   {:type :account
    :rule rules/validate-card-not-active}
   {:type :account
    :rule rules/validate-account-not-initialized}])

(s/defn create-account :- OperationResult
  "Operation for the creation of an account."
  [account :- Account
   active-card :- s/Bool
   available-limit :- s/Int]
  (if-let [violation (rules/validate-account-already-initialized account)]
    (op-res-with-violation account violation)
    (-> account
        (assoc :active-card active-card)
        (assoc :available-limit available-limit)
        (assoc :initialized true)
        (op-res))))

(s/defn authorize-transaction :- OperationResult
  "Operation for the authorization of a transaction on an account."
  [account :- Account
   transaction :- Transaction]
  (if-let [violation (validate account transaction validations)]
    (op-res-with-violation account violation)
    (do
      (let [new-available-limit (- (:available-limit account) (:amount transaction))
            new-history (conj (:history account) transaction)]
        (-> account
            (assoc :available-limit new-available-limit)
            (assoc :history new-history)
            (op-res))))))

