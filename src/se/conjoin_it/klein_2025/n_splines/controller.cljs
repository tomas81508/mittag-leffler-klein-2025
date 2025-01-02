(ns se.conjoin-it.klein-2025.n-splines.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.n-splines.core :refer [add-degree
                                                             remove-degree]]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]))

(def section-name :n-splines)

(defmethod create-initial-state section-name
  []
  {:interval-id    nil
   :time           0.3
   :level          0
   :show-curve     false
   :control-points [[100 700] [400 150] [700 700]]})

(defmethod start-page-processes! section-name
  [_]
  (let [interval-id (js/setInterval (fn []
                                      (swap! db/db-atom update-in [section-name :time] (fn [t]
                                                                                         (let [t (+ t 0.005)]
                                                                                           (if (> t 1) 0 t)))))
                                    50)]
    (swap! db/db-atom assoc-in [section-name :interval-id] interval-id)))

(defmethod stop-page-processes! section-name
  [_]
  (let [id (get-in (deref db/db-atom) [section-name :interval-id])]
    (js/clearInterval id)))

(defn handle-event
  [{name :name
    data :data}]
  (println "[N-SPLINES] Event:" name ", data:" data)
  (condp = name
    :control-point-change
    (let [index (:index data)
          value (:value data)]
      (swap! db/db-atom assoc-in [section-name :control-points index] value))

    :inc-level
    (swap! db/db-atom update-in [section-name :level] + 0.5)

    :dec-level
    (swap! db/db-atom update-in [section-name :level] - 0.5)

    :show-curve
    (swap! db/db-atom update-in [section-name :show-curve] not)

    :remove-degree
    (swap! db/db-atom update-in [section-name :control-points] remove-degree)

    :add-degree
    (swap! db/db-atom update-in [section-name :control-points] add-degree)

    (println "No event defined!")
    ))