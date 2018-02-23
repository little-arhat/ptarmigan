(ns ptarmigan.core
  (:require
   [com.stuartsierra.component :as component]
   [clojure.tools.logging :as log]
   defcomponent
   [ptarmigan.config :as cfg]
   [ptarmigan.influxdb :as influxdb]))


;; (defn init-query []
;;   (->
;;    (->> (query all-strats-query)
;;         :results
;;         first
;;         :series
;;         first
;;         :values
;;         (map second))
;;    (zipmap (repeatedly (constantly nil)))))

(defn iso2timestamp [s]
  (-> s
      java.time.ZonedDateTime/parse
      .toInstant
      .toEpochMilli))

(defn mk-convert []
  (let [now (.toEpochMilli (java.time.Instant/now))]
    (fn [[time strategy]]
      [strategy (- now (iso2timestamp time))])))

;; (defn get-delays-millis []
;;   (let [convert (mk-convert)]
;;     (->> (query delays-query)
;;          :results
;;          first
;;          :series
;;          (map :values)
;;          (map first)
;;          (map #(take 2 %))
;;          (map convert)
;;          (into {}))))

;; (def state (atom (init-strategies)))

(defn at-shutdown
  [f]
  (-> (Runtime/getRuntime)
      (.addShutdownHook (Thread. (bound-fn []
                                   (log/info "Shutdown!")
                                   (f))))))


(defn -main
  [config-path]
  (let [system (defcomponent/system []
                 {:start true
                  :params [(cfg/load-config config-path)]})]
    (log/info "System started")
    (at-shutdown #(component/stop system))
    (while true
      (Thread/sleep 100))))
