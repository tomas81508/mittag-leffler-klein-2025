(ns ^:figwheel-hooks se.conjoin-it.klein-2025.main
  (:require [reagent.core]
            [reagent.dom :as reagent-dom]
            [se.conjoin-it.klein-2025.db :as db]
            [se.conjoin-it.klein-2025.app-view :refer [app-component]]
            [se.conjoin-it.klein-2025.quadratic-splines.controller]
            [se.conjoin-it.klein-2025.n-splines.controller]
            [se.conjoin-it.klein-2025.cubic-splines.controller]
            [se.conjoin-it.klein-2025.bernstein-polynomials.controller]
            [se.conjoin-it.klein-2025.tangram.controller]
            [se.conjoin-it.klein-2025.timing-functions.controller]))

(enable-console-print!)

(defn render!
  {:export true}
  []
  (reagent-dom/render [app-component (deref db/db-atom)]
                      (js/document.getElementById "app")))


; A function that will run only once even under dev hot reload
(defonce init (do (add-watch db/db-atom
                             :render-watcher
                             (fn [_ _ old-db new-db]
                               (when-not (= old-db new-db)
                                 (render!))))
                  (reset! db/db-atom (db/create-initial-state
                                       {:screen-height (.-innerHeight js/window)
                                        :screen-width (.-innerWidth js/window)}))
                  (.addEventListener js/window
                                     "resize"
                                     (fn [] (swap! db/db-atom assoc
                                                   :screen-height (.-innerHeight js/window)
                                                   :screen-width (.-innerWidth js/window))))
                  (render!)))

(defn on-js-reload
  {:after-load true}
  []
  (println "Reloading")
  (render!))
