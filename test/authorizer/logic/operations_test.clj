(ns authorizer.logic.operations-test
  (:require [midje.sweet :refer :all]
            [authorizer.fixtures :as fxt]
            [authorizer.logic.operations :as operations]))

(facts "Account creation"
       (fact "Should successfully create an account when no account was previously created"
             (operations/create-account fxt/account-not-initialized true 1000) => {:account {:active-card true
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
                (:account result) fxt/transaction-99-taxi)
               (operations/authorize-transaction
                (:account result) fxt/transaction-99-taxi)) => {:account {:active-card true
                                                                          :available-limit 50
                                                                          :initialized true
                                                                          :history [fxt/transaction-uber
                                                                                    fxt/transaction-99-taxi
                                                                                    fxt/transaction-99-taxi]}})
       
       (fact "Should fail because no transaction should be accepted without a properly initialized account"
             (operations/authorize-transaction
              fxt/account-not-initialized fxt/transaction-99-taxi) => {:account fxt/account-not-initialized
                                                                       :violations [:account-not-initialized]})

       (fact "Should fail because no transaction should be accepted when the card is not active"
             (operations/authorize-transaction
              fxt/account-card-not-active fxt/transaction-99-taxi) => {:account fxt/account-card-not-active
                                                                       :violations [:card-not-active]})

       (fact "Should fail because the transaction amount should not exceed available limit"
             (operations/authorize-transaction
              fxt/account-initialized fxt/transaction-vivara) => {:account fxt/account-initialized
                                                                  :violations [:insufficient-limit]})

       (fact "Should fail because there should not be more than 3 transactions on a 2 minute interval"
             ;; TODO: implement
             )

       (fact "Should fail because there should not be more than 1 similar transactions (same amount and merchant) in a 2 minutes interval"
             ;; TODO: implement
             ))
