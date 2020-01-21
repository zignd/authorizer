(defproject authorizer "0.1.0-SNAPSHOT"
  :description "Code Challenge: Authorizer"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [prismatic/schema "1.1.12"]
                 [cheshire "5.9.0"]]
  :main ^:skip-aot authorizer.core
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[midje "1.9.9"]]}
             :uberjar {:aot :all}}
  :plugins [[lein-cljfmt "0.6.6"]
            [lein-midje "3.2.2"]])
