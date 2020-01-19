(defproject authorizer "0.1.0-SNAPSHOT"
  :description "Code Challenge: Authorizer"
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :main ^:skip-aot authorizer.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
