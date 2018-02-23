(ns ptarmigan.retrieval
  (:require
   [clojure.tools.logging :as log]
   [clojure.string :as str]
   [defcomponent :refer [defcomponent]]
   [ptarmigan.influxdb :as influxdb]
   [honeysql.core :as honey]
   [honeysql.helpers :refer :all :as helpers]
   ))


(defn sql-group-by [m args]
  (conj m (some->> (not-empty args) (assoc {} :group-by))))

(defn q [s]
  (str \" s \"))

(defn build-delay-query [{:keys [lookback measurement tags fields conditions]}]
  (let [time-condition [:> :time (str "now() - " lookback)]
        fields (map q fields)]
    (-> (apply select (-> fields not-empty (or [:*])))
        (from (keyword measurement))
        (where (concat [:and time-condition] conditions))
        (sql-group-by (map (comp keyword q) tags))
        (order-by [:time :desc])
        (limit 1))))

(defn format-query [sqlmap]
  (let [query (honey/format sqlmap)
        parts (str/split (first query) #"\?")
        args (rest query)
        merged (interleave parts args)]
    (apply str merged)))

(defcomponent retrieval [influxdb/influxdb]
  [config]
  (start [this]
         (->> (:retrieval config)
              build-delay-query
              format-query
              (assoc this :delays-query)))
  (stop [this]))

(defn delays [retrieval]
  (let [db (:influxdb retrieval)
        query (:delays-query retrieval)]
    (influxdb/query db query :precision :ms)))
