(ns authorizer.logic.operations
  (:require [schema.core :as s]
            [authorizer.schema :refer [Map Account Transaction OperationResult Validation Validations]]
            [authorizer.logic.rules :as rules]))

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

(s/defn invoke-validation :- (s/maybe s/Keyword)
  [account :- Map
   transaction :- Transaction
   validation :- Validation]
  (case (:type validation)
    :account ((:rule validation) account)
    :account-and-transaction ((:rule validation) account transaction)))

;; (def acc {:account {:active-card true :available-limit 100}})
;; (def tra {:transaction {:merchant "Blah blah" :amount 20 :time "2019-02-13T10:00:00.000Z"}})

(s/defn validate :- (s/maybe s/Keyword)
  [account :- Map
   transaction :- Transaction
   validations :- Validations]
  (if-let [validation (and (> (count validations) 0) (peek validations))]
    (if-let [error (invoke-validation account transaction validation)]
      error
      (recur account transaction (pop validations)))))

(s/defn create-account :- OperationResult
  [account :- Map
   active-card :- s/Bool
   available-limit :- s/Int]
  (if-let [error (rules/validate-account-already-initialized account)]
    (assoc account :violations [error])
    (assoc account :account {:active-card active-card
                             :available-limit available-limit})))

(s/defn authorize-transaction
  [account :- Map
   transaction :- Transaction]
  (if-let [error (validate account transaction validations)]
    (assoc account :violations [error])
    account))

