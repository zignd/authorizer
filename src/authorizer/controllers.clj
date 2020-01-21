(ns authorizer.controllers
  (:require [schema.core :as s]
            [authorizer
             [logic :as logic]
             [data :as data]
             [schema :refer [Map Account Transaction]]]))

(s/defn create-account!
  [account-atom :- (s/atom Map)
   {{:keys [active-card available-limit]} :account} :- Account]
  (let [account (logic/create-account @account-atom active-card available-limit)]
    (if (not (contains? account :violations))
      (data/update! account-atom account))
    account))

(s/defn authorize-transaction!
  [account-atom :- (s/atom Map)
   transaction :- Transaction]
  (let [account (logic/authorize-transaction @account-atom transaction)]
    (if (not (contains? account :violations))
      (data/update! account-atom account))
    account))
