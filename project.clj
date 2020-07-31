(defproject conduit "0.1.0-SNAPSHOT"
            :description "Conduit API"
            :url "http://example.com/FIXME"
            :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
                      :url "https://www.eclipse.org/legal/epl-2.0/"}
            :dependencies [[org.clojure/clojure "1.10.0"]
                           [io.replikativ/datahike "0.3.1"]
                           [io.replikativ/datahike-postgres "0.1.0"]]
            :main ^:skip-aot conduit.core
            :target-path "target/%s"
            :profiles {:uberjar {:aot :all}})
