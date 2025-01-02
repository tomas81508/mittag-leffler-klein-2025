(ns se.conjoin-it.klein-2025.tangram.view
  (:require [se.conjoin-it.klein-2025.tangram.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.tangram.core :refer [get-shape-definition]]
            [se.conjoin-it.klein-2025.tangram.physics :refer [moving?]]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))



(defn shape-view
  [shape {coordinates :coordinates color :color} key]
  (let [[x y a] (if (moving? shape) (subvec shape 3 6) shape)]
    [:polygon {:key       key
               :fill      color
               :points    coordinates
               :transform (str "translate(" x " " y ") rotate(" (- a) ")")}]))

(defn model-view
  [model width height]
  (let [modified-height (- height 150)]
    [:svg {:view-box "-600 -600 1200 1200"
           :height   (min modified-height width)
           :width    (min modified-height width)
           :on-click (fn [] (handle-event {:name :animate}))
           :style    {:cursor      "pointer"
                      :user-select "none"
                      :border      "1px solid lightgray"}}
     [:g {:transform "scale(1 -1)"}
      (->> (keys model)
           (map (fn [k]
                  (let [shape (get model k)]
                    (shape-view shape (get-shape-definition k) k)))))]]))


(defn main-component
  "The main component."
  [db-tangram width height]
  (when db-tangram
    [:div {:id "tangram"}
     [model-view (:model db-tangram) width height]

     [:div {:style {:display "flex"}}
      [:div {:style {:width "170px"}}
       [style/input {:label     "Styvhet"
                     :value     (:stiffness-input db-tangram)
                     :on-blur   (fn [value]
                                  (handle-event {:name :stiffness-changed
                                                 :data value}))
                     :on-change (fn [value]
                                  (handle-event {:name :stiffness-input-changed
                                                 :data value}))}]]
      [:div {:style {:width       "170px"
                     :margin-left "30px"}}
       [style/input {:label     "Dämpning"
                     :value     (:damping-input db-tangram)
                     :on-blur   (fn [value]
                                  (handle-event {:name :damping-changed
                                                 :data value}))
                     :on-change (fn [value]
                                  (handle-event {:name :damping-input-changed
                                                 :data value}))}]]]]))

