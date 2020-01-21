(ns authorizer.logic
  (:require [schema.core :as s]
            [authorizer.schema :refer [Map Account OperationResult
                                       Transaction]]))

;; rules for operations

(s/defn validate-account-not-initialized :- (s/maybe s/Keyword)
  "No transaction should be accepted without a properly initialized account"
  [account :- {(s/optional-key :account) Map}]
  (if (s/check Account account)
    :account-not-initialized))

(s/defn validate-account-already-initialized :- (s/maybe s/Keyword)
  ""
  [account :- {(s/optional-key :account) Map}]
  (if (not (validate-account-not-initialized account))
    :account-already-initialized))

(s/defn validate-card-not-active :- (s/maybe s/Keyword)
  "No transaction should be accepted when the card is not active"
  [{{:keys [active-card]} :account} :- Account]
  (if (not active-card)
    :card-not-active))

(s/defn validate-insufficient-limit :- (s/maybe s/Keyword)
  "The transaction amount should not exceed available limit"
  [{{:keys [available-limit]} :account} :- Account
   {{:keys [amount]} :transaction } :- Transaction]
  (if (> amount available-limit)
    :insufficient-limit))

(s/defn validate-high-frequency-small-interval
  "There should not be more than 3 transactions on a 2 minute interval"
  [account :- Account
   transaction :- Transaction]
  ;; TODO: add transaction history to the account atom
  ;; TODO: :transaction :time needs to be java.util.Date
  (if false
    :high-frequency-small-interval))

(s/defn validate-doubled-transaction
  "There should not be more than 1 similar transactions (same amount and merchant) in a 2 minutes interval"
  [account :- Account
   transaction :- Transaction]
  ;; TODO: add transaction history to the account atom
  ;; TODO: :transaction :time needs to be java.util.Date
  (if false
    :doubled-transaction))

;; end of rules rules for operations


(s/defn create-account :- OperationResult
  [account :- {(s/optional-key :account) Map}
   active-card :- s/Bool
   available-limit :- s/Int]
  (if-let [error (validate-account-not-initialized)]
    (assoc account :violations [error])
    (assoc account :account {:active-card active-card
                             :available-limit available-limit})))

(s/defn authorize-transaction :- OperationResult
  [account :- Account
   transaction :- Transaction]
  account)
