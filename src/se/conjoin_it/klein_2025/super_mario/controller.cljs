(ns se.conjoin-it.klein-2025.super-mario.controller
  (:require [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.super-mario.core :refer [clojure-boxes
                                                               section-name
                                                               update-time]]
            [se.conjoin-it.klein-2025.events :refer [create-initial-state
                                                     start-page-processes!
                                                     stop-page-processes!]]))


(def arrow-keys #{"ArrowLeft" "ArrowRight" "ArrowUp" "ArrowDown"})

(defmethod create-initial-state section-name
  []
  {:frames     false
   :boxes      clojure-boxes
   :interval   nil
   :directions #{}
   :time       nil
   :mario      {:x         120
                :vx        0
                :y         -300
                :vy        0
                :direction :right}})

(defn now [] (.getTime (js/Date.)))

(defn handle-event
  [{name :name
    data :data}]
  (println "[SUPER MARIO] Event:" name ", data:" data)
  (condp = name

    :key-up
    (swap! db/db-atom update-in [section-name :directions] disj data)

    :key-down
    (swap! db/db-atom update-in [section-name :directions] conj data)

    :box-clicked
    (swap! db/db-atom update-in [section-name :boxes]
           (fn [boxes]
             (let [operation (if (contains? boxes data) disj conj)]
               (operation boxes data))))

    nil))

(defn handleKeyUpEvent
  [e]
  (let [key (.-key e)]
    (when (contains? arrow-keys key)
      (handle-event {:name :key-up :data key}))))

(defn handleKeyDownEvent
  [e]
  (let [key (.-key e)]
    (when (contains? arrow-keys key)
      (handle-event {:name :key-down :data key}))))

(defmethod start-page-processes! section-name
  [_]
  (.addEventListener (.-body js/document) "keyup" handleKeyUpEvent)
  (.addEventListener (.-body js/document) "keydown" handleKeyDownEvent)
  (let [interval-id (js/setInterval (fn [] (swap! db/db-atom update-time (now))) 20)]
    (swap! db/db-atom assoc-in [section-name :interval-id] interval-id)))

(defmethod stop-page-processes! section-name [_]
  (js/clearInterval (get-in (deref db/db-atom) [section-name :interval-id]))
  (.removeEventListener (.-body js/document) "keyup" handleKeyUpEvent)
  (.removeEventListener (.-body js/document) "keydown" handleKeyDownEvent))
