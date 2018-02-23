(ns user
  (:require
   [clojure.string :as str]
   [clojure.pprint :refer (pprint)]
   [clojure.tools.namespace.repl :refer [refresh]]
   [com.stuartsierra.component :as component]
   [defcomponent]
   [honeysql.core :as honey]
   [ptarmigan.config :as cfg]
   [ptarmigan.retrieval :as r]
   [ptarmigan.influxdb :as influxdb]))

(def system nil)

(defn init []
  (alter-var-root #'system (constantly (defcomponent/system
                                         [influxdb/influxdb r/retrieval]
                                         {:params [(cfg/load-config "config/dev.edn")]}))))

(defn start []
  (alter-var-root #'system component/start)
  :started)

(defn stop []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s) nil))))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go)
  :ok)
