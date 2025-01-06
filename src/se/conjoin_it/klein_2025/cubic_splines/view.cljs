(ns se.conjoin-it.klein-2025.cubic-splines.view
  (:require [se.conjoin-it.klein-2025.cubic-splines.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.cubic-splines.core :refer [get-points
                                                                 replace-points]]
            [reagent.core :as reagent]
            [se.conjoin-it.klein-2025.style :as style]))

(defn maybe-modify-moving-point
  [local-state control-points]
  (if (and (:mouse-position local-state)
           (:original-point local-state))
    (replace-points control-points (:original-point local-state) (:mouse-position local-state))
    control-points))

(defn show-control-lines
  [points]
  [:g
   (map-indexed (fn [deep-index ps]
                  [:g {:key deep-index}
                   (->> ps
                        (partition 2 1)
                        (map-indexed (fn [index [[x1 y1] [x2 y2]]]
                                       [:line {:key          index
                                               :x1           x1 :y1 y1 :x2 x2 :y2 y2
                                               :stroke       "rgb(135, 135, 135)"
                                               :stroke-width 2
                                               :style        {:opacity 0.3}}])))])
                points)])

(defn cubic-spline
  [{control-points       :control-points
    t                    :t
    n                    :n
    level                :level
    show-path            :show-path
    show-curve           :show-curve
    show-construction    :show-construction
    moving-control-point :moving-control-point
    move-point-position  :move-point-position
    fix-point-position   :fix-point-position}]
  (let [point-radius 7]
    [:g

     (when show-path
       (let [[[x1 y1] [x2 y2] [x3 y3] [x4 y4]] control-points]
         [:path {:d            (str "M " x1 " " y1 " "
                                    "C " x2 " " y2 " " x3 " " y3 " " x4 " " y4)
                 :stroke       "green"
                 :stroke-width "2px"
                 :fill         "transparent"}]))

     (when show-curve
       (let [ss (range n (min t (inc n)) 0.005)]
         [:g
          (->> ss
               (map-indexed (fn [index s]
                              (let [[x y] (->> control-points
                                               (get-points s)
                                               (last)
                                               (first))]
                                [:circle {:key  index
                                          :cx   x :cy y :r 3
                                          :fill style/kth-blue}]))))]))

     (when show-construction
       (let [all-points (->> control-points
                             (get-points t))]
         [:g
          [:g (->> all-points
                   (take (int (+ level 0.5)))
                   (drop 1)
                   (show-control-lines))]

          [:g (let [points (->> (drop 1 all-points)
                                (take (int level)))
                    number-of-points (count points)]
                (->> points
                     (map-indexed (fn [deep-index ps]
                                    (let [leaf (= deep-index (dec number-of-points))]
                                      [:g {:key   deep-index
                                           :style {:opacity (if leaf 1 0.3)}}
                                       (->> ps
                                            (map-indexed (fn [index [x y]]
                                                           [:g {:key index}
                                                            [:circle {:key  index
                                                                      :cx   x :cy y :r point-radius
                                                                      :fill style/kth-blue}]])))])))))]]))

     [:g (show-control-lines [control-points])]

     [:g (->> control-points
              (map-indexed (fn [index [x y]]
                             [:circle {:key           index
                                       :cx            x
                                       :cy            y
                                       :r             point-radius
                                       :fill          "orange"
                                       :style         {:cursor (if moving-control-point "grabbing" "grab")}
                                       :on-mouse-down (fn [] (move-point-position [x y]))
                                       :on-mouse-up   (fn [] (fix-point-position))
                                       }])))]]))


(defn main-component
  "The main component."
  [db-cubic-splines width height]
  (let [local-state-atom (reagent/atom {:original-point nil
                                        :mouse-position nil})
        mouse-event-listener-fn (fn [mouse-event]
                                  (swap! local-state-atom
                                         assoc
                                         :mouse-position
                                         [(.-offsetX mouse-event) (.-offsetY mouse-event)]))
        fix-point-position! (fn []
                              (let [local-state (deref local-state-atom)]
                                (-> (js/document.getElementById (str "spline"))
                                    (.removeEventListener "mousemove" mouse-event-listener-fn))
                                (when (:mouse-position local-state)
                                  (handle-event {:name :control-point-change
                                                 :data {:original-point (:original-point local-state)
                                                        :value          (:mouse-position local-state)}}))
                                (swap! local-state-atom
                                       assoc
                                       ;:mouse-position-listener nil
                                       :mouse-position nil)))
        move-point-position! (fn [original-point]
                               (-> (js/document.getElementById (str "spline"))
                                   (.addEventListener "mousemove" mouse-event-listener-fn))
                               (swap! local-state-atom assoc :original-point original-point))]
    (fn [db-cubic-splines width height]
      (let [local-state (deref local-state-atom)]
        (when db-cubic-splines
          [:div {:id "spline"}
           [:div
            [:svg {:width  width
                   :height (- height 150)
                   :style  {:background-color "rgb(240, 240, 240)"}}
             (->> (:control-points db-cubic-splines)
                  (maybe-modify-moving-point local-state)
                  (map-indexed (fn [index control-points]
                                 [:g {:key index}
                                  [cubic-spline {:control-points       control-points
                                                 :t                    (:time db-cubic-splines)
                                                 :n                    index
                                                 :show-construction    (<= index (:time db-cubic-splines) (inc index))
                                                 :level                (:level db-cubic-splines)
                                                 :show-path            (:show-path db-cubic-splines)
                                                 :show-curve           (:show-curve db-cubic-splines)
                                                 :moving-control-point (:mouse-position local-state)
                                                 :move-point-position  move-point-position!
                                                 :fix-point-position   fix-point-position!}]])))]]
           [:div {:style {:display         "flex"
                          :align-items     "center"
                          :justify-content "space-between"}}
            [:div {:style {:display     "flex"
                           :align-items "center"}}
             [style/button {:style    {:width "160px"}
                            :on-click (fn [] (handle-event {:name :dec-level}))
                            :text     "Färre detaljer"}]
             [:div {:style {:padding   "5px"
                            :margin    "0px 5px"
                            :font-size "200%"}}
              (:level db-cubic-splines)]
             [style/button {:style    {:width "160px"}
                            :on-click (fn [] (handle-event {:name :inc-level}))
                            :text     "Mer detaljer"}]

             [style/button {:style    {:margin-left "30px"
                                       :width       "160px"}
                            :on-click (fn [] (handle-event {:name :remove-curve}))
                            :text     "Ta bort kurva"}]
             [style/button {:style    {:margin-left "10px"
                                       :width       "160px"}
                            :on-click (fn [] (handle-event {:name :add-curve}))
                            :text     "Lägg till kurva"}]]

            [:div
             [style/button {:style    {:width "160px"}
                            :on-click (fn [] (handle-event {:name :toggle-path}))
                            :text     "Visa <path d=\"...\">"}]
             [style/button {:style    {:margin-left "10px"
                                       :width       "160px"}
                            :on-click (fn [] (handle-event {:name :toggle-curve}))
                            :text     "Visa kurvan"}]]]])))))

