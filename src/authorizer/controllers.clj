(ns authorizer.controllers
  (:require [schema.core :as s]
            [authorizer
             [data :as data]
             [schema :refer [Account PublicAccount PublicTransaction PublicOperationResult
                             OperationResult->PublicOperationResult
                             PublicTransaction->Transaction]]]
            [authorizer.logic.operations :as operations]))

(s/defn create-account! :- PublicOperationResult
  [account-atom :- (s/atom Account)
   {{:keys [active-card available-limit]} :account} :- PublicAccount]
  (let [result (operations/create-account @account-atom active-card available-limit)]
    (when (not (contains? result :violations))
      (data/update! account-atom (:account result)))
    (OperationResult->PublicOperationResult result)))

(s/defn authorize-transaction! :- PublicOperationResult
  [account-atom :- (s/atom Account)
   transaction :- PublicTransaction]
  (let [result (->> transaction
                    (PublicTransaction->Transaction)
                    (operations/authorize-transaction @account-atom))]
    (when (not (contains? result :violations))
      (data/update! account-atom (:account result)))
    (OperationResult->PublicOperationResult result)))
