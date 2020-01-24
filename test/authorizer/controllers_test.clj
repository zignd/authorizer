(ns authorizer.controllers-test
  (:require [midje.sweet :refer :all]
            [authorizer
             [fixtures :as fxt]
             [data :as data]
             [controllers :as controllers]]))

(facts "Account creation through the controller"
       (fact "Should successfully create an account when no account was previously created"
             (let [account-atom (data/initialize!)]
               (controllers/create-account! account-atom fxt/public-account) => {:account {:active-card true
                                                                                           :available-limit 100}}
               @account-atom => {:active-card true
                                 :available-limit 100
                                 :initialized true
                                 :history []}))

       (fact "Should fail to create an account when an account was already created"
             (let [account-atom (data/initialize!)]
               (controllers/create-account! account-atom fxt/public-account) => {:account {:active-card true
                                                                                           :available-limit 100}}
               (controllers/create-account! account-atom fxt/public-account) => {:account {:active-card true
                                                                                           :available-limit 100}
                                                                                 :violations [:account-already-initialized]}
               @account-atom => {:active-card true
                                 :available-limit 100
                                 :initialized true
                                 :history []})))

(facts "Transactions"
       (fact "Should successfully authorize a transaction"
             (let [account-atom (data/initialize!)]
               (controllers/create-account! account-atom fxt/public-account)
               (controllers/authorize-transaction!
                account-atom fxt/public-transaction-uber) => {:account {:active-card true
                                                                        :available-limit 80}}
               (controllers/authorize-transaction!
                account-atom fxt/public-transaction-99-taxi) => {:account {:active-card true
                                                                           :available-limit 65}}
               @account-atom => {:active-card true
                                 :available-limit 65
                                 :initialized true
                                 :history [fxt/transaction-uber
                                           fxt/transaction-99-taxi]}))

       (fact "Should fail returning a violation and the account should not be affected"
             (let [account-atom (data/initialize!)]
               (controllers/create-account! account-atom fxt/public-account)
               (controllers/authorize-transaction!
                account-atom fxt/public-transaction-vivara) => {:account {:active-card true
                                                                          :available-limit 100}
                                                                :violations [:insufficient-limit]}
               @account-atom => {:active-card true
                                 :available-limit 100
                                 :initialized true
                                 :history []})))
