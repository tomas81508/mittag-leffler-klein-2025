(ns se.conjoin-it.klein-2025.monte_carlo_volume.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.monte_carlo_volume.core :refer [section-name
                                                                      update-with-sample]]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]))



(defmethod create-initial-state section-name
  []
  {:max-samples       1
   :max-samples-input 1
   :dimension         2
   :dimension-input   2
   :inside-count      0
   :samples           0
   :points            []})

(defn update-with-sample!
  []
  (let [{samples :samples max-samples :max-samples} (get (deref db/db-atom) :monte-carlo-volume)]
    (when (< samples max-samples)
      (swap! db/db-atom update-with-sample)
      (recur))))

(defmethod start-page-processes! section-name [_])

(defmethod stop-page-processes! section-name [_])

(defn handle-event
  [{name :name
    data :data}]
  (println "[MONTE CARLO VOLUME] Event:" name ", data:" data)
  (condp = name

    :dimension-changed
    (swap! db/db-atom assoc-in [section-name :dimension] data)

    :dimension-input-changed
    (swap! db/db-atom assoc-in [section-name :dimension-input] data)

    :max-samples-changed
    (swap! db/db-atom assoc-in [section-name :max-samples] data)

    :max-samples-input-changed
    (swap! db/db-atom assoc-in [section-name :max-samples-input] data)

    :run
    (do (swap! db/db-atom update section-name (fn [db-monte-carl-volume]
                                                (-> db-monte-carl-volume
                                                    (assoc :inside-count 0
                                                           :samples 0
                                                           :points []))))
        (update-with-sample!))

    "Nothing"
    ))