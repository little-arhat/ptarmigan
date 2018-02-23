(defproject ptarmigan "0.1.0-SNAPSHOT"
  :description "delay"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [defcomponent "0.2.2"
                  :exclusions [com.stuartsierra/component]]
                 [org.influxdb/influxdb-java "2.8"]
                 [com.squareup.retrofit2/retrofit "2.3.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [honeysql "0.9.1"]
                 [http-kit "2.2.0"]
                 [twarc "0.1.10"]
                 ]
  :main ^:skip-aot ptarmigan.core
  :target-path "target/%s"
  :source-paths ["src"]
  :profiles {
             :uberjar {:aot :all}
             :repl {:repl-options {:init-ns user}
                    :injections [(require 'clojure.tools.namespace.repl)
                                 (require 'user)]
                    }
             :dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :repl-options {:init-ns user}
                   }
             })
