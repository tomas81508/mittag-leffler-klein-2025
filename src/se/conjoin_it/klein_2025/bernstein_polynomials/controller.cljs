(ns se.conjoin-it.klein-2025.bernstein-polynomials.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.bernstein-polynomials.core :refer [compute-plot-values]]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]))

(def section-name :bernstein-polynomials)

(defmethod create-initial-state section-name
  []
  (let [degree 1]
    {:degree degree
     :values (compute-plot-values degree)}))

(defmethod start-page-processes! section-name [_])

(defmethod stop-page-processes! section-name [_])

(defn handle-event
  [{name :name
    data :data}]
  (println "[BERNSTEIN POLYNOMIALS] Event:" name ", data:" data)
  (condp = name

    :decrease-degree
    (swap! db/db-atom (fn [db]
                        (let [new-degree (dec (get-in db [section-name :degree]))]
                          (if (neg? new-degree)
                            db
                            (-> db
                                (assoc-in [section-name :degree] new-degree)
                                (assoc-in [section-name :values] (compute-plot-values new-degree)))))))

    :increase-degree
    (swap! db/db-atom (fn [db]
                        (let [new-degree (inc (get-in db [section-name :degree]))]
                          (-> db
                              (assoc-in [section-name :degree] new-degree)
                              (assoc-in [section-name :values] (compute-plot-values new-degree))))))

    "Nothing"
    ))