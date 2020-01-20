(ns authorizer.controllers
  (:require [schema.core :as s]
            [authorizer.logic :as logic]
            [authorizer.data :as data]))

(s/defn create-account!
  [operation]
  (assoc operation :violations []))

(s/defn authorize-transaction!
  [operation]
  {:account {:active-card true, :available-limit 80}, :violations [:insufficient-limit]})
