(ns se.conjoin-it.klein-2025.quadratic-splines.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]))

(def section-name :quadratic-splines)

(defmethod create-initial-state section-name
  []
  {:show-curve     false
   :paint-level    0
   :control-points [[100 100] [100 700] [700 700]]})

(defmethod start-page-processes! section-name [_])

(defmethod stop-page-processes! section-name [_])

(defn handle-event
  [{name :name
    data :data}]
  (println "[QUADRATIC SPLINES] Event:" name ", data:" data)
  (condp = name
    :control-point-change
    (let [index (:index data)
          value (:value data)]
      (swap! db/db-atom assoc-in [section-name :control-points index] value))

    :paint-more
    (swap! db/db-atom update-in [section-name :paint-level] inc)

    :paint-less
    (swap! db/db-atom update-in [section-name :paint-level] (fn [level] (max (dec level) 0)))

    :show-curve
    (swap! db/db-atom update-in [section-name :show-curve] not)

    ))