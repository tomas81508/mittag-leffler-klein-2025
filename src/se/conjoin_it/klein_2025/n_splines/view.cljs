(ns se.conjoin-it.klein-2025.n-splines.view
  (:require [se.conjoin-it.klein-2025.n-splines.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.n-splines.core :refer [get-points]]
            [reagent.core :as reagent]
            [se.conjoin-it.klein-2025.style :as style]))

(defn maybe-modify-moving-point
  [local-state points]
  (if (and (:mouse-position-listener local-state)
           (:mouse-position local-state))
    (assoc points (:mouse-position-listener local-state) (:mouse-position local-state))
    points))

(def fill-color
  {0 "rgb(80, 120, 60)"
   1 "rgb(100, 120, 60)"
   2 "rgb(120, 120, 60)"
   3 "rgb(135, 120, 60)"
   4 "rgb(145, 133, 70)"
   5 "rgb(155, 140, 80)"
   6 "rgb(169, 150, 100)"
   7 "rgb(180, 160, 120)"
   8 "rgb(188, 170, 130)"
   9 "rgb(210, 180, 140)"})

(def line-color
  {0 "rgb(80, 120, 60)"
   1 "rgb(100, 120, 60)"
   2 "rgb(120, 120, 60)"
   3 "rgb(135, 120, 60)"
   4 "rgb(145, 133, 70)"
   5 "rgb(155, 140, 80)"
   6 "rgb(169, 150, 100)"
   7 "rgb(180, 160, 120)"
   8 "rgb(188, 170, 130)"
   9 "rgb(210, 180, 140)"})

(defn spline
  [_ _ _ _]
  (let [local-state-atom (reagent/atom {:mouse-position-listener nil
                                        :mouse-position          nil})
        mouse-event-listener-fn (fn [mouse-event]
                                  (swap! local-state-atom
                                         assoc
                                         :mouse-position
                                         [(.-offsetX mouse-event) (.-offsetY mouse-event)]))
        fix-point-position! (fn [index]
                              (let [local-state (deref local-state-atom)]
                                (-> (js/document.getElementById (str "spline"))
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
                               (-> (js/document.getElementById (str "spline"))
                                   (.addEventListener "mousemove" mouse-event-listener-fn))
                               (swap! local-state-atom assoc :mouse-position-listener index))]
    (fn [points t level show-curve]
      (let [local-state (deref local-state-atom)
            point-radius 7
            all-points (->> points
                            (maybe-modify-moving-point local-state)
                            (get-points t))]
        [:g
         ;[:text {:x 20 :y 20} (str false)]
         (when (and show-curve (<= 0 t 1))
           (let [ss (range 0 t 0.005)]
             [:g
              (->> ss
                   (map-indexed (fn [index s]
                                  (let [[x y] (->> points
                                                   (maybe-modify-moving-point local-state)
                                                   (get-points s)
                                                   (last)
                                                   (first))]
                                    [:circle {:key  index
                                              :cx   x :cy y :r 3
                                              :fill style/kth-blue}]))))]))

         [:g (->> all-points
                  (take (int (+ level 0.5)))
                  (map-indexed (fn [deep-index ps]
                                 [:g {:key deep-index}
                                  (->> ps
                                       (partition 2 1)
                                       (map-indexed (fn [index [[x1 y1] [x2 y2]]]
                                                      [:line {:key          index
                                                              :x1           x1 :y1 y1 :x2 x2 :y2 y2
                                                              :stroke       (line-color deep-index)
                                                              :stroke-width 2
                                                              :style        {:opacity 0.3}}])))])))]

         [:g (let [points (->> (drop 1 all-points)
                               (take (int level)))
                   number-of-points (count points)]
               (->> points
                    (map-indexed (fn [deep-index ps]
                                   [:g {:key   deep-index
                                        :style {:opacity (if (= deep-index (dec number-of-points)) 1 0.3)}}
                                    (->> ps
                                         (map-indexed (fn [index [x y]]
                                                        [:g {:key index}
                                                         [:circle {:key  index
                                                                   :cx   x :cy y :r point-radius
                                                                   :fill (fill-color deep-index)}]])))]))))]

         [:g (->> (first all-points)
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
  [db-splines width height]
  (when db-splines
    [:div {:id "spline"}
     [:div
      [:svg {:width  width
             :height (- height 150)
             :style {:background-color "rgb(240, 240, 240)"}}
       [spline (:control-points db-splines)
        (:time db-splines)
        (:level db-splines)
        (:show-curve db-splines)]]]
     [:div {:style {:display         "flex"
                    :align-items     "center"
                    :justify-content "space-between"}}
      [:div {:style {:display     "flex"
                     :align-items "center"}}
       [style/button {:style    {}
                      :on-click (fn [] (handle-event {:name :dec-level}))
                      :text     "Färre detaljer"}]
       [:div {:style {:padding   "5px"
                      :margin    "0px 5px"
                      :font-size "200%"}}
        (:level db-splines)]
       [style/button {:style    {}
                      :on-click (fn [] (handle-event {:name :inc-level}))
                      :text     "Mer detaljer"}]

       [style/button {:style    {:margin-left "40px"}
                      :on-click (fn [] (handle-event {:name :remove-degree}))
                      :text     "Minska gradtal"}]
       [:div {:style {:padding   "5px"
                      :margin    "0px 10px"
                      :font-size "200%"}}
        (str (dec (count (:control-points db-splines))))]
       [style/button {:style    {}
                      :on-click (fn [] (handle-event {:name :add-degree}))
                      :text     "Öka gradtal"}]]


      [style/button {:style    {:margin-left "40px"}
                     :on-click (fn [] (handle-event {:name :show-curve}))
                     :text     "Visa kurvan"}]]]))

