(ns se.conjoin-it.klein-2025.harmonic-oscillator.view
  (:require [se.conjoin-it.klein-2025.harmonic-oscillator.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.harmonic-oscillator.core :as core]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))

(def height 800)
(def baseline (/ height 2))
(def width 300)

(defn main-component
  "The main component."
  [db-harmonic-oscillator screen-width]
  (when db-harmonic-oscillator
    (let [{y :y vy :vy stiffness :stiffness damping :damping} db-harmonic-oscillator
          hookes-force (core/hookes-force stiffness y)
          damping-force (core/damping-force damping vy)
          svg-y (+ y baseline)]
      [:div {:id "harmonic-oscillator"}
       [:div {:style {:display "flex"}}
        [:svg {:view-box (str "0 0 " width " " height)
               :width    "300px"}
         [:line {:x1           10 :x2 (- width 10) :y1 baseline :y2 baseline
                 :stroke-width 2
                 :stroke       "gray"}]

         [:circle {:cx     (/ width 2)
                   :cy     svg-y
                   :r      40
                   :stroke style/kth-blue
                   :fill   "orange"}]
         [:path {:d              (str "M 150 " svg-y " v " hookes-force
                                      (if (pos? y)
                                        "l -3 6 h 6 l -3 -6"
                                        "l -3 -6 h 6 l -3 6"))
                 :stroke-linecap "round"
                 :stroke         style/kth-blue
                 :stroke-width   5
                 :fill           style/kth-blue}]
         (when-not (zero? damping-force)
           [:path {:d              (str "M 160 " svg-y " v " damping-force
                                        (if (pos? vy)
                                          "l -3 6 h 6 l -3 -6"
                                          "l -3 -6 h 6 l -3 6"))
                   :stroke-linecap "round"
                   :stroke         "red"
                   :stroke-width   5
                   :fill           "red"}])]
        [:div {:style {:margin-left "20px"}}
         [:div {:style {:width "170px"}}
          [style/input {:label     "Styvhet"
                        :value     (:stiffness-input db-harmonic-oscillator)
                        :on-blur   (fn [value]
                                     (handle-event {:name :stiffness-changed
                                                    :data value}))
                        :on-change (fn [value]
                                     (handle-event {:name :stiffness-input-changed
                                                    :data value}))}]]
         [:div {:style {:width       "170px"}}
          [style/input {:label     "Dämpning"
                        :value     (:damping-input db-harmonic-oscillator)
                        :on-blur   (fn [value]
                                     (handle-event {:name :damping-changed
                                                    :data value}))
                        :on-change (fn [value]
                                     (handle-event {:name :damping-input-changed
                                                    :data value}))}]]
         [style/button {:text "Starta om"
                        :style {:margin-top "20px"
                                :width "175px"}
                        :on-click (fn [] (handle-event {:name :restart}))}]]
        ]])))

