(ns authorizer.logic.helpers
  (:require [schema.core :as s]
            [authorizer.schema :refer [Account Transaction OperationResult Validation Validations]]))

(s/defn invoke-validation :- (s/maybe s/Keyword)
  "Responsible properly invoking the validation based on its type for the given account and transaction."
  [account :- Account
   transaction :- Transaction
   validation :- Validation]
  (case (:type validation)
    :account ((:rule validation) account)
    :account-and-transaction ((:rule validation) account transaction)))

(s/defn validate :- (s/maybe s/Keyword)
  "Invokes all the provided validations on the given account and transaction."
  [account :- Account
   transaction :- Transaction
   validations :- Validations]
  (if-let [validation (and (> (count validations) 0) (peek validations))]
    (if-let [violation (invoke-validation account transaction validation)]
      violation
      (recur account transaction (pop validations)))))

(s/defn op-res :- OperationResult
  "The execution of an operation results in a OperationResult."
  [account :- Account]
  {:account account})

(s/defn op-res-with-violation :- OperationResult
  "The execution of an operation results in an OperationResult which might contain violations."
  [account :- Account
   violation :- s/Keyword]
  (-> account
      (op-res)
      (assoc :violations [violation])))
