(ns se.conjoin-it.klein-2025.tangram.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.tangram.core :refer [get-model
                                                           get-next-model-name]]
            [se.conjoin-it.klein-2025.tangram.physics :as physics]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]))

(def section-name :tangram)

(defmethod create-initial-state section-name
  []
  (let [current-model :logo]
    {:stiffness-input 220
     :stiffness       220
     :damping-input   10
     :damping         10
     :model           (get-model current-model)
     :model-name      current-model
     :time            0})
  )

(defmethod start-page-processes! section-name
  [_]
  )

(defmethod stop-page-processes! section-name [_])

(defn animation-callback [timestamp]
  (let [db (deref db/db-atom)
        model-animating (physics/moving-model? (get-in db [section-name :model]))]
    (if-not model-animating
      (println "Stop animation")
      (do
        (swap! db/db-atom update section-name
               (fn [db-tangram]
                 (let [time-delta (- timestamp (:time db-tangram))]
                   (if (pos? time-delta)
                     (as-> db-tangram $
                           (if-not model-animating
                             $
                             (update $ :model (fn [model] (physics/step model time-delta (:stiffness db-tangram) (:damping db-tangram)))))
                           (assoc $ :time timestamp))
                     db-tangram))))
        (js/window.requestAnimationFrame animation-callback)))))


(defn handle-event
  [{name :name
    data :data}]
  (println "[TANGRAM] Event:" name ", data:" data)
  (condp = name
    :animate
    (let [static (physics/static-model? (get-in (deref db/db-atom) [section-name :model]))]
      (swap! db/db-atom update section-name
             (fn [{model-name :model-name model :model :as db-tangram}]
               (let [next-model-name (get-next-model-name model-name)]
                 (-> db-tangram
                     (assoc :model-name next-model-name)
                     (assoc :model (physics/set-target-model model (get-model next-model-name)))
                     (assoc :time (js/window.performance.now))))))
      (when static
        (js/window.requestAnimationFrame animation-callback)))

    :stiffness-input-changed
    (swap! db/db-atom assoc-in [section-name :stiffness-input] data)

    :stiffness-changed
    (swap! db/db-atom assoc-in [section-name :stiffness] data)

    :damping-input-changed
    (swap! db/db-atom assoc-in [section-name :damping-input] data)

    :damping-changed
    (swap! db/db-atom assoc-in [section-name :damping] data)

    "Nothing"
    ))