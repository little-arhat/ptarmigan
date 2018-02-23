(defproject ptarmigan "0.1.0-SNAPSHOT"
  :description "delay"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot ptarmigan.core
  :target-path "target/%s"
  :profiles {
             :uberjar {:aot :all}
             :dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :repl-options {:init-ns user}
                   }
             })
