(ns authorizer.logic.operations-test
  (:require [midje.sweet :refer :all]
            [authorizer.fixtures :as fxt]
            [authorizer.logic.operations :as operations]
            [java-time :as jtime]))

(facts "Account creation"
       (fact "Should successfully create an account when no account was previously created"
             (operations/create-account fxt/account-not-initialized true 1000)
             => {:account {:active-card true
                           :available-limit 1000
                           :initialized true
                           :history []}})
       (fact "Should fail to create an account when an account was already created"
             (-> fxt/account-initialized
                 (assoc :initialized true)
                 (operations/create-account true 1000)) => {:account {:active-card true
                                                                      :available-limit 100
                                                                      :initialized true
                                                                      :history []}
                                                            :violations [:account-already-initialized]}))

(facts "Transactions"
       (fact "Should successfully authorize a transaction"
             (as-> (operations/authorize-transaction
                    fxt/account-initialized fxt/transaction-uber) result
               (operations/authorize-transaction
                (:account result) fxt/transaction-99-taxi)) => {:account {:active-card true
                                                                          :available-limit 65
                                                                          :initialized true
                                                                          :history [fxt/transaction-uber
                                                                                    fxt/transaction-99-taxi]}})

       (fact "Should fail because no transaction should be accepted without a properly initialized account"
             (operations/authorize-transaction fxt/account-not-initialized fxt/transaction-99-taxi)
             => {:account fxt/account-not-initialized
                 :violations [:account-not-initialized]})

       (fact "Should fail because no transaction should be accepted when the card is not active"
             (operations/authorize-transaction fxt/account-card-not-active fxt/transaction-99-taxi)
             => {:account fxt/account-card-not-active
                 :violations [:card-not-active]})

       (fact "Should fail because the transaction amount should not exceed available limit"
             (operations/authorize-transaction fxt/account-initialized fxt/transaction-vivara)
             => {:account fxt/account-initialized
                 :violations [:insufficient-limit]})

       (fact "Should fail because there should not be more than 3 transactions on a 2 minute interval"
             (let [t1 {:merchant "Merchant 1"
                       :amount 10
                       :time (jtime/offset-date-time "2020-01-20T14:00:00.000Z")}
                   t2 {:merchant "Merchant 2"
                       :amount 5
                       :time (jtime/offset-date-time "2020-01-20T14:00:30.000Z")}
                   t3 {:merchant "Merchant 3"
                       :amount 15
                       :time (jtime/offset-date-time "2020-01-20T14:01:00.000Z")}]
               (as-> fxt/account-initialized x
                 (operations/authorize-transaction x t1)
                 (operations/authorize-transaction (:account x) t2)
                 (operations/authorize-transaction (:account x) t3))
               => {:account {:active-card true
                             :available-limit 85
                             :initialized true
                             :history [t1 t2]}
                   :violations [:high-frequency-small-interval]}))

       (fact "Should fail because there should not be more than 1 similar transactions (same amount and merchant) in a 2 minutes interval"
             (as-> fxt/account-initialized x
               (operations/authorize-transaction x fxt/transaction-uber)
               (operations/authorize-transaction (:account x) fxt/transaction-99-taxi)
               (operations/authorize-transaction (:account x) fxt/transaction-99-taxi))
             => {:account {:active-card true
                           :available-limit 65
                           :history [fxt/transaction-uber
                                     fxt/transaction-99-taxi]
                           :initialized true}
                 :violations [:doubled-transaction]}))
