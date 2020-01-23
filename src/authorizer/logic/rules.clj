(ns authorizer.logic.rules
  (:require [schema.core :as s]
            [authorizer.schema :refer [Account Transaction]]
            [java-time :as jtime])
  (:import [java.time OffsetDateTime]))

(s/defn validate-account-not-initialized :- (s/maybe s/Keyword)
  "No transaction should be accepted without a properly initialized account"
  [account :- Account]
  (if (not (:initialized account))
    :account-not-initialized))

(s/defn validate-account-already-initialized :- (s/maybe s/Keyword)
  "There can't be more than one account initialization operation"
  [account :- Account]
  (if (not (validate-account-not-initialized account))
    :account-already-initialized))

(s/defn validate-card-not-active :- (s/maybe s/Keyword)
  "No transaction should be accepted when the card is not active"
  [{:keys [active-card]} :- Account]
  (if (not active-card)
    :card-not-active))

(s/defn validate-insufficient-limit :- (s/maybe s/Keyword)
  "The transaction amount should not exceed available limit"
  [{:keys [available-limit]} :- Account
   {:keys [amount]} :- Transaction]
  (if (> amount available-limit)
    :insufficient-limit))

(s/defn ^:private to-minutes-from-epoch :- s/Int
  [date-time :- OffsetDateTime]
  (int (/ (jtime/to-millis-from-epoch date-time) 60000)))

(s/defn ^:private to-minutes-list :- [s/Int]
  [account :- Account
   transaction :- Transaction]
  (-> (:history account)
      (conj transaction)
      (as-> transactions (map #(to-minutes-from-epoch (:time %)) transactions))
      (sort)))

(s/defn validate-high-frequency-small-interval :- (s/maybe s/Keyword)
  "There should not be more than 3 transactions on a 2 minute interval"
  [account :- Account
   transaction :- Transaction]
  (let [minutes-list (to-minutes-list account transaction)]
    (if (>= (count minutes-list) 3)
      (let [at-3th-idx-reverse (nth minutes-list (- (count minutes-list) 3))
            at-last-idx (last minutes-list)]
        (if (<= (- at-last-idx at-3th-idx-reverse) 2)
          :high-frequency-small-interval)))))

(s/defn validate-doubled-transaction :- (s/maybe s/Keyword)
  "There should not be more than 1 similar transactions (same amount and merchant) in a 2 minutes interval"
  [account :- Account
   transaction :- Transaction]
  (let [range (filter #(and (= (:merchant transaction) (:merchant %1))
                            (= (:amount transaction) (:amount %1))
                            (<= (- (to-minutes-from-epoch (:time transaction))
                                   (to-minutes-from-epoch (:time %1)))
                                2))
                      (:history account))]
    (when (> (count range) 0)
      :doubled-transaction)))

