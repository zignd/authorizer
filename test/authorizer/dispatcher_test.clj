(ns authorizer.dispatcher-test
  (:require [midje.sweet :refer :all]
            [authorizer.dispatcher :refer [dispatch!]])
  (:import [java.io StringReader]))

(facts "Decoding JSON from the STDIN and encoding to the STDOUT"
       (fact "Should successfully decode valid JSON from the STDIN and return the operation result as JSON to the STDOUT"
             (with-out-str
               (with-in-str "{\"account\": {\"active-card\": true, \"available-limit\": 100}}
{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}
{\"transaction\": {\"merchant\": \"Mc Donald's\", \"amount\": 25, \"time\": \"2019-02-13T10:35:00.000Z\"}}"
                 (dispatch! *in*)))
             => "{\"account\":{\"active-card\":true,\"available-limit\":100}}
{\"account\":{\"active-card\":true,\"available-limit\":80}}
{\"account\":{\"active-card\":true,\"available-limit\":55}}\n")
       
       (fact "Should fail to decode invalid JSON from the STDIN returning something which should not expose the internals of the application"
             (with-out-str
               (with-in-str "totally not JSON"
                 (dispatch! *in*)))
             => "{\"error\":\"Invalid JSON\"}\n"))
