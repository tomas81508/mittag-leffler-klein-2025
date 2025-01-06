(ns se.conjoin-it.klein-2025.harmonic-oscillator.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]
            [se.conjoin-it.klein-2025.harmonic-oscillator.core :refer [section-name
                                                                       time-tick]]))

(defn now [] (.getTime (js/Date.)))

(def initial-state
  {:y               300
   :vy              0
   :stiffness-input 0.7
   :stiffness       0.7
   :damping-input   0
   :damping         0
   :time            nil
   :interval-id     nil})

(defmethod create-initial-state section-name
  []
  initial-state)

(defmethod start-page-processes! section-name [_]
  (let [interval-id (js/setInterval (fn []
                                      (if (get (deref db/db-atom) section-name)
                                        (swap! db/db-atom time-tick (now))
                                        (println "NOT HERE")))
                                    10)]
    (swap! db/db-atom assoc-in [section-name :interval-id] interval-id)))

(defmethod stop-page-processes! section-name [_]
  (let [interval-id (get-in (deref db/db-atom) [section-name :interval-id])]
    (js/clearInterval interval-id)))

(defn handle-event
  [{name :name
    data :data}]
  (println "[HARMONIC OSCILLATOR] Event:" name ", data:" data)
  (condp = name

    :stiffness-input-changed
    (swap! db/db-atom assoc-in [section-name :stiffness-input] data)

    :stiffness-changed
    (swap! db/db-atom assoc-in [section-name :stiffness] data)

    :damping-input-changed
    (swap! db/db-atom assoc-in [section-name :damping-input] data)

    :damping-changed
    (swap! db/db-atom assoc-in [section-name :damping] data)

    :restart
    (swap! db/db-atom update section-name
           (fn [db-harmonic-oscillator]
             (-> initial-state
                 (assoc :interval-id (:interval-id db-harmonic-oscillator)))))

    "Nothing"
    ))