(ns se.conjoin-it.klein-2025.monte_carlo_volume.view
  (:require [se.conjoin-it.klein-2025.monte_carlo_volume.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))

(defn main-component
  "The main component."
  [db-monte-carlo-volume width]
  (when db-monte-carlo-volume
    [:div {:id    "monte-carlo-volume"
           :style {:display "flex"}}
     [:div
      [:svg {:view-box "0 0 1200 1200"
             :width    600
             :height   600}
       [:g {:stroke-width 3
            :stroke       "black"}
        [:line {:x1 100 :y1 1150 :x2 100 :y2 50}]
        [:line {:x1 50 :y1 1100 :x2 1150 :y2 1100}]
        [:path {:d "M 95 60 l 5 -10 l 5 10 z"}]
        [:path {:d "M 1140 1095 l 10 5 l -10 5 z"}]
        [:path {:d    "M 100 100 A 1000 1000 0 0 1 1100 1100"
                :fill "transparent"}]
        [:path {:d    "M 100 100 h 1000 v 1000"
                :fill "transparent"}]]
       [:g {:stroke "orange"
            :fill   "orange"}
        (->> (:points db-monte-carlo-volume)
             (map-indexed (fn [index [x y]]
                            (let [x (min x 1.03)
                                  y (min y 1.03)]
                              [:circle {:key index
                                        :cx  (+ 100 (* x 1000))
                                        :cy  (+ 100 (* (- 1 y) 1000))
                                        :r   4}]))))]]]
     (let [inside-in-percent (when-not (zero? (:samples db-monte-carlo-volume))
                               (/ (:inside-count db-monte-carlo-volume) (:samples db-monte-carlo-volume)))
           volume-of-cube (apply * (repeat (:dimension db-monte-carlo-volume) 2))]
       [:div {:style {:margin-left "20px"}}
        [:div {:style {:width "170px"}}
         [style/input {:label     "Dimension"
                       :value     (:dimension-input db-monte-carlo-volume)
                       :on-blur   (fn [value]
                                    (handle-event {:name :dimension-changed
                                                   :data value}))
                       :on-change (fn [value]
                                    (handle-event {:name :dimension-input-changed
                                                   :data value}))}]]
        [:div {:style {:width "170px"}}
         [style/input {:label     "Antal samplingar"
                       :value     (:max-samples-input db-monte-carlo-volume)
                       :on-blur   (fn [value]
                                    (handle-event {:name :max-samples-changed
                                                   :data value}))
                       :on-change (fn [value]
                                    (handle-event {:name :max-samples-input-changed
                                                   :data value}))}]]

        [style/display {:label "Volymen av kuben"
                        :value volume-of-cube}]

        [style/display {:label "Andel innanför"
                        :value (or inside-in-percent "-")}]

        [style/display {:label "Volymen av enhetsklotet"
                        :value (or (and inside-in-percent
                                        (* inside-in-percent volume-of-cube))
                                   "-")}]

        [style/button {:text     "Kör"
                       :style    {:margin-top "20px"}
                       :on-click (fn [] (handle-event {:name :run}))}]

        ])]))

