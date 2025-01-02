(ns se.conjoin-it.klein-2025.cubic-splines.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.cubic-splines.core :refer [add-curve
                                                                 remove-curve
                                                                 replace-points]]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]))

(def section-name :cubic-splines)

(defmethod create-initial-state section-name
  []
  {:interval-id    nil
   :time           0
   :level          3
   :show-curve     false
   :show-path      false
   :control-points [[[100 500] [150 150] [400 200] [450 400]]
                    [[450 400] [500 600] [700 600] [800 300]]]})

(defmethod start-page-processes! section-name
  [_]
  (let [interval-id (js/setInterval (fn []
                                      (let [control-points (get-in (deref db/db-atom)
                                                                   [section-name :control-points])]
                                        (swap! db/db-atom update-in [section-name :time]
                                               (fn [t]
                                                 (let [t (+ t 0.005)]
                                                   (if (> t (count control-points)) 0 t))))))
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
    (let [original-point (:original-point data)
          value (:value data)]
      (swap! db/db-atom update-in [section-name :control-points] replace-points original-point value))

    :inc-level
    (swap! db/db-atom update-in [section-name :level] + 0.5)

    :dec-level
    (swap! db/db-atom update-in [section-name :level] - 0.5)

    :toggle-curve
    (swap! db/db-atom update-in [section-name :show-curve] not)

    :toggle-path
    (swap! db/db-atom update-in [section-name :show-path] not)

    :remove-curve
    (swap! db/db-atom update-in [section-name :control-points] remove-curve)

    :add-curve
    (swap! db/db-atom update-in [section-name :control-points] add-curve)



    (println "No event defined!")
    ))