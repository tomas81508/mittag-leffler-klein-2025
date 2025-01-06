(ns se.conjoin-it.klein-2025.timing-functions.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.timing-functions.core :refer [predefined-timing-functions]]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]))

(def section-name :timing-functions)

(defmethod create-initial-state section-name
  []
  {:graph-point1             {:x 0.5 :y 0.75}
   :graph-point2             {:x 0.75 :y 0.5}
   :animation-duration       1000
   :animation-duration-input 1000
   :animation-element        :transform
   :predefined               :ease})

(defmethod start-page-processes! section-name [_])

(defmethod stop-page-processes! section-name [_])

(defn handle-event
  [{name :name
    data :data}]
  (println "[TIMING FUNCTIONS] Event:" name ", data:" data)
  (condp = name
    :animation-duration-input-changed
    (swap! db/db-atom assoc-in [section-name :animation-duration-input] data)

    :animation-duration-changed
    (swap! db/db-atom assoc-in [section-name :animation-duration] data)

    :predefined-changed
    (swap! db/db-atom update section-name
           (fn [db-timing-functions]
             (let [timing-function (predefined-timing-functions data)]
               (assoc db-timing-functions
                 :predefined data
                 :graph-point1 (:p1 timing-function)
                 :graph-point2 (:p2 timing-function)))))

    :element-changed
    (swap! db/db-atom assoc-in [section-name :animation-element] (keyword data))

    :animations-point-changed
    (swap! db/db-atom
           update section-name
           (fn [db-timing-functions]
             (assoc db-timing-functions
               (:point data) (:value data)
               :predefined nil)))

    "Nothing"
    ))