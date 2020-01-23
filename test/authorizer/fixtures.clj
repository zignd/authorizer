(ns authorizer.fixtures
  (:require [schema.core :as s]
            [authorizer.schema :refer [Account PublicAccount Transaction PublicTransaction]]))

(s/def account-not-initialized :- Account
  {:active-card false
   :available-limit 0
   :initialized false
   :history []})

(s/def account-initialized :- Account
  {:active-card true
   :available-limit 100
   :initialized true
   :history []})

(s/def account-card-not-active :- Account
  {:active-card false
   :available-limit 100
   :initialized true
   :history []})

(s/def public-account :- PublicAccount
  {:account {:active-card true
             :available-limit 100}})

(s/def transaction-uber :- Transaction
  {:merchant "Uber"
   :amount 20
   :time "2019-02-13T10:00:00.000Z"})

(s/def transaction-99-taxi :- Transaction
  {:merchant "99 Taxi"
   :amount 15
   :time "2019-02-13T11:00:00.000Z"})

(s/def transaction-vivara :- Transaction
  {:merchant "Vivara"
   :amount 1500
   :time "2019-02-13T15:00:00.000Z"})

(s/def public-transaction-uber :- PublicTransaction
  {:transaction transaction-uber})

(s/def public-transaction-99-taxi :- PublicTransaction
  {:transaction transaction-99-taxi})

(s/def public-transaction-vivara :- PublicTransaction
  {:transaction transaction-vivara})
