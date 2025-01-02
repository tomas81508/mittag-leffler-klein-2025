(ns se.conjoin-it.klein-2025.events
  (:require [se.conjoin-it.klein-2025.db :as db]))

(defmulti create-initial-state (fn [page] page))
(defmethod create-initial-state :home [_] (println "No initial-state defined"))

(defmulti start-page-processes! (fn [page] page))
(defmethod start-page-processes! :default [_] (println "No start-process defined"))

(defmulti stop-page-processes! (fn [page] page))
(defmethod stop-page-processes! :default [_] (println "No stop-process defined"))

(defn handle-main-events
  [{name :name
    data :data}]
  (println "MAIN Event:" name ", data:" data)
  (condp = name
    :page-changed
    (let [previous-page (get (deref db/db-atom) :page)
          next-page data]
      (stop-page-processes! previous-page)
      (swap! db/db-atom
             (fn [db]
               (let [initial-state (create-initial-state next-page)]
                 (as-> db $
                       (assoc $ :page next-page)
                       (dissoc $ previous-page)
                       (if initial-state
                         (assoc $ next-page initial-state)
                         $)))))
      (start-page-processes! next-page))))