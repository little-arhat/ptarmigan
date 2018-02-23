(ns ptarmigan.influxdb
  (:require
   [defcomponent :refer [defcomponent]])
  (:import
   (org.influxdb InfluxDB InfluxDBFactory)
   (org.influxdb.dto Query QueryResult QueryResult$Result QueryResult$Series)
   (okhttp3 OkHttpClient OkHttpClient$Builder)
   (java.util.concurrent TimeUnit)))

(defn ^:private make-client [{:keys [connect-timeout read-timeout]}]
  (-> (OkHttpClient$Builder. )
      (.connectTimeout connect-timeout (TimeUnit/MILLISECONDS))
      (.readTimeout read-timeout (TimeUnit/MILLISECONDS))))

(defn ^:private connect [{:keys [uri user pass connection-config]}]
  (->> (make-client connection-config)
       (InfluxDBFactory/connect uri
                                user
                                pass)))

(def time-units
  {:ns TimeUnit/NANOSECONDS
   :ms TimeUnit/MILLISECONDS
   :s TimeUnit/SECONDS})

(defn ^:private convert-series [^QueryResult$Series series]
  {:name (.getName series)
   :tags (.getTags series)
   :values (into [] (map #(into [] %) (.getValues series)))})

(defn ^:privat convert-result [^QueryResult$Result result]
  (if (.hasError result)
    {:error (.getError result)}
    {:series (into [] (map convert-series (.getSeries result)))}))

(defn send-q [conn precision q]
  (.query conn q precision))
(defn mk-q [db qs]
  (Query. qs db))

(defcomponent influxdb []
  [config]

  (start [this]
         (let [cfg (:influxdb config)]
           (assoc this
                  :conn (connect cfg)
                  :db (:db cfg))))

  (stop [this]))

(defn query [influxdb qs & {:keys [precision] :or {:precision :ns}}]
  (->> qs
       (mk-q (:db influxdb))
       (send-q (:conn influxdb) (get time-units precision))
       (.getResults)
       (map convert-result)
       (assoc {} :results)))

(defn ping [influxdb]
  (let [pong (.ping (:conn influxdb))]
    {:version (.getVersion pong)
     :response-time (.getResponseTime pong)}))
