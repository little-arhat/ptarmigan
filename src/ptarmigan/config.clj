(ns ptarmigan.config
  ;; (:require)
  )

(defn deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]
    (if (some identity vs)
      (reduce #(rec-merge %1 %2) v vs)
      v)))

(def default-config-path "config/default.edn")

(defn load-config
  ([]
   (load-file default-config-path))
  ([config-path]
   (deep-merge
    (load-file default-config-path)
    (load-file config-path))))
