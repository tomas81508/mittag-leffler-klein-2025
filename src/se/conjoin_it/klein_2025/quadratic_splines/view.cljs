(ns se.conjoin-it.klein-2025.quadratic-splines.view
  (:require [se.conjoin-it.klein-2025.quadratic-splines.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.quadratic-splines.core :refer [get-points-between-control-points
                                                                     zip-collections]]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))

(defn maybe-modify-moving-point
  [local-state points]
  (if (and (:mouse-position-listener local-state)
           (:mouse-position local-state))
    (assoc points (:mouse-position-listener local-state) (:mouse-position local-state))
    points))

(def fill-color
  {0 "rgb(101, 67, 33)"
   1 "rgb(111, 90, 40)"
   2 "rgb(124, 105, 50)"
   3 "rgb(135, 120, 60)"
   4 "rgb(145, 133, 70)"
   5 "rgb(155, 140, 80)"
   6 "rgb(169, 150, 100)"
   7 "rgb(180, 160, 120)"
   8 "rgb(188, 170, 130)"
   9 "rgb(210, 180, 140)"}
  )

(defn spline
  [_]
  (let [local-state-atom (reagent/atom {:mouse-position-listener nil
                                        :mouse-position          nil})
        mouse-event-listener-fn (fn [mouse-event]
                                  (swap! local-state-atom
                                         assoc
                                         :mouse-position
                                         [(.-offsetX mouse-event) (.-offsetY mouse-event)]))
        fix-point-position! (fn [index]
                              (let [local-state (deref local-state-atom)]
                                (-> (js/document.getElementById (str "quadratic-spline"))
                                    (.removeEventListener "mousemove" mouse-event-listener-fn))
                                (when (:mouse-position local-state)
                                  (handle-event {:name :control-point-change
                                                 :data {:index index
                                                        :value (:mouse-position local-state)}}))
                                (swap! local-state-atom
                                       assoc
                                       :mouse-position-listener nil
                                       :mouse-position nil)))
        move-point-position! (fn [index]
                               (-> (js/document.getElementById (str "quadratic-spline"))
                                   (.addEventListener "mousemove" mouse-event-listener-fn))
                               (swap! local-state-atom assoc :mouse-position-listener index))]
    (fn [db-quadratic-splines]
      (let [local-state (deref local-state-atom)
            point-radius 7
            paint-level (:paint-level db-quadratic-splines)
            control-points (->> (:control-points db-quadratic-splines)
                                (maybe-modify-moving-point local-state))
            paired-points (->> (get-points-between-control-points control-points)
                               (apply zip-collections))]
        [:g

         [:g {:style {:opacity    (if (> paint-level 2) 1 0)
                      :transition "all 1000ms"}}
          (->> paired-points
               (drop 2)
               (map (fn [[[x1 y1] [x2 y2]]]
                      [:line {:key          (str "the-rest" x1 y1 x2 y2)
                              :x1           x1 :y1 y1 :x2 x2 :y2 y2
                              :stroke       "rgb(140,140,140)"
                              :stroke-width 2}])))]

         [:g {:style {:opacity    (if (> paint-level 1) 1 0)
                      :transition "all 1000ms"}}
          (->> paired-points
               (take 2)
               (map (fn [[[x1 y1] [x2 y2]]]
                      [:line {:key          (str "first-two" x1 y1 x2 y2)
                              :x1           x1 :y1 y1 :x2 x2 :y2 y2
                              :stroke       "rgb(200,200,200)"
                              :stroke-width 2}])))]

         [:g {:style {:opacity    (if (> paint-level 0) 1 0)
                      :transition "all 1000ms"}}
          (->> paired-points
               (apply concat)
               (map-indexed (fn [index [x y]]
                              [:circle {:key  (str "circles" index x y)
                                        :cx   x :cy y :r (* 0.3 point-radius)
                                        :fill "gray"}])))]

         [:g {:style {:opacity    (if (:show-curve db-quadratic-splines) 1 0)
                      :transition "all 1000ms"}}
          [:path {:stroke       "green"
                  :stroke-width "2px"
                  :fill         "transparent"
                  :d            (let [[[x1 y1] [x2 y2] [x3 y3]] control-points]
                                  (str "M " x1 " " y1 " "
                                       "Q " x2 " " y2 " " x3 " " y3))}]
          ]

         [:g (->> control-points
                  (map-indexed (fn [index [x y]]
                                 [:circle {:key           index
                                           :cx            x
                                           :cy            y
                                           :r             point-radius
                                           :fill          "orange"
                                           :style         {:cursor (if (:mouse-position-listener local-state)
                                                                     "grabbing"
                                                                     "grab")}
                                           :on-mouse-down (fn [] (move-point-position! index))
                                           :on-mouse-up   (fn [] (fix-point-position! index))
                                           }])))]]))))



(defn main-component
  "The main component."
  [db-quadratic-splines width height]
  (when db-quadratic-splines
    [:div {:id    "quadratic-spline"}
     [:div
      [:svg {:width width
             :height (- height 150)
             :style {:background-color "rgb(240, 240, 240)"}}
       [spline db-quadratic-splines]]]
     [:div {:style {:margin-top "5px"
                    :display         "flex"
                    :align-items     "center"
                    :justify-content "space-between"}}
      [:div
       [style/button {:style    {}
                      :on-click (fn [] (handle-event {:name :paint-less}))
                      :text     "Färre detaljer"}]
       [style/button {:style    {:margin-left "5px"}
                      :on-click (fn [] (handle-event {:name :paint-more}))
                      :text     "Mer detaljer"}]]
      [style/button {:style    {}
                     :on-click (fn [] (handle-event {:name :show-curve}))
                     :text     "Visa kurvan"}]]]))

