(ns authorizer.controllers
  (:require [schema.core :as s]
            [authorizer
             [data :as data]
             [schema :refer [Account PublicAccount PublicTransaction OperationResult OperationResult]]]
            [authorizer.logic.operations :as operations]))

(s/defn hide-internal-info
  [result :- OperationResult])

(s/defn create-account! :- OperationResult
  [account-atom :- (s/atom Account)
   {{:keys [active-card available-limit]} :account} :- PublicAccount]
  (let [result (operations/create-account @account-atom active-card available-limit)]
    (when (not (contains? result :violations))
      (data/update! account-atom (:account result)))
    result))

;; TODO: replace OperationResult for OperationResultPublic
;; get the result through a mapper function which should remove the fields which
;; should not return to the public 

(s/defn authorize-transaction! :- OperationResult
  [account-atom :- (s/atom Account)
   transaction :- PublicTransaction]
  (let [result (operations/authorize-transaction @account-atom (:transaction transaction))]
    (when (not (contains? result :violations))
      (data/update! account-atom (:account result)))
    result))
