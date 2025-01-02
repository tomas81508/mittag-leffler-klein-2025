(ns se.conjoin-it.klein-2025.bernstein-polynomials.view
  (:require [se.conjoin-it.klein-2025.bernstein-polynomials.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.bernstein-polynomials.core :refer [delta n]]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))

(def start-x 100)
(def width 900)
(def start-y 700)
(def height 500)

(defn transform-to-screen
  [x y]
  [(+ start-x (* x width)) (- start-y (* y height))])

(defn plot
  [ys]
  [:g {:key (first ys)}
   (->> ys
        (partition 2 1)
        (map-indexed (fn [x [y1 y2]]
                       (let [x (/ x n)
                             [x1 y1] (transform-to-screen x y1)
                             [x2 y2] (transform-to-screen (+ x delta) y2)]
                         [:line {:key          x
                                 :x1           x1 :y1 y1 :x2 x2 :y2 y2
                                 :stroke-width 2
                                 :stroke       style/kth-blue}]))))])


(defn plot-all-curves
  [degree values]
  [:g
   [:text {:x     60 :y 210
           :style {:font-size "200%"}}
    "1"]
   [:text {:x     990 :y 750
           :style {:font-size "200%"}}
    "1"]
   [:path {:d            (str "M " start-x " " start-y " v -600 l -10 10 M 100 100 l 10 10 "
                              "M " start-x " " start-y " h 1000 l -10 10 M 1100 700 l -10 -10 "
                              "M 90 200 h 20 "
                              "M 1000 690 v 20")
           :stroke-width 2
           :fill         "transparent"
           :stroke       style/kth-blue}]

   (->> values
        (map plot))
   ])


(defn main-component
  "The main component."
  [db-bernstein-polynomials width height]
  (when db-bernstein-polynomials
    [:div {:id "db-bernstein-polynomials"}
     [:div
      [:svg {:width  width
             :height 800
             :style  {:background-color "rgb(240, 240, 240)"}}
       [plot-all-curves
        (:degree db-bernstein-polynomials)
        (:values db-bernstein-polynomials)]]]
     [:div {:style {:display     "flex"
                    :align-items "center"}}
      [style/button {:style    {}
                     :on-click (fn [] (handle-event {:name :decrease-degree}))
                     :text     "Minska gradtal"}]
      [:div {:style {:padding   "5px"
                     :margin    "0px 10px"
                     :font-size "200%"}}
       (:degree db-bernstein-polynomials)]
      [style/button {:style    {:margin-left "5px"}
                     :on-click (fn [] (handle-event {:name :increase-degree}))
                     :text     "Öka gradtal"}]]]))

