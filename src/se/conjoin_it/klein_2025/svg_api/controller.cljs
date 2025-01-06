(ns se.conjoin-it.klein-2025.svg_api.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]))

(def section-name :svg-api)

(defmethod create-initial-state section-name
  []
  {})

(defmethod start-page-processes! section-name [_])

(defmethod stop-page-processes! section-name [_])

(defn handle-event
  [{name :name
    data :data}]
  (println "[SVG API] Event:" name ", data:" data)
  (condp = name

    "Nothing"
    ))