(ns se.conjoin-it.klein-2025.super-mario.view
  (:require [se.conjoin-it.klein-2025.super-mario.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))

(def sky-height 500)

(defn get-mario-image-src
  [mario]
  (str "asset/mario/"
       (cond (not (zero? (:vy mario)))
             (if (= (:direction mario) :left) "left-jump" "right-jump")

             (zero? (:vx mario))
             (if (= (:direction mario) :left) "left" "right")

             :else
             (if (= (:direction mario) :left) "left-walk" "right-walk"))))

(defn mario-component
  [mario frames]
  (let [mario-x (:x mario)
        mario-y (:y mario)
        mario-image-height 70
        mario-image-padding-bottom 8
        mario-image-padding-left 25]
    [:img {:src   (str (get-mario-image-src mario) ".gif")
           :style (merge {:position         "absolute"
                          :top              (+ sky-height
                                               mario-image-padding-bottom
                                               (- mario-y
                                                  mario-image-height))
                          :left             (- mario-x
                                               mario-image-padding-left)
                          :transform-origin "0 0"
                          :transform        (str "scale(2)")}
                         (when frames
                           {:border "1px solid red"}))}]))


(defn main-component
  "The main component."
  [db-super-mario screen-width]
  (when db-super-mario
    (let [width (* (quot screen-width 50) 50)]
      [:div {:id    "db-super-mario"
             :style {:position "relative"}}
       [:div {:style {:position "absolute"
                      :z-index  1}}
        (->> (for [x (range 0 width 50)
                   y (range 0 500 50)]
               [x y])
             (map-indexed (fn [index [x y]]
                            (let [box [x (- sky-height y)]]
                              [:div {:key      index
                                     :style    (merge {:position "absolute"
                                                       :width    50
                                                       :height   50
                                                       :top      y
                                                       :left     x}
                                                      (when (:frames db-super-mario)
                                                        {:border "1px solid gray"})
                                                      (when (contains? (:boxes db-super-mario) box)
                                                        {:background-color "brown"}))
                                     :on-click (fn []
                                                 (handle-event {:name :box-clicked :data box}))}]))))]
       [:div {:style {:position "relative"}}
        [mario-component (:mario db-super-mario) (:frames db-super-mario)]
        [:div {:id    "the-sky"
               :style {:background-color "rgb(174, 238, 238)"
                       :height           "500px"
                       :width            "100%"}}]
        [:div {:style {:background-color "rgb(74, 163, 41)"
                       :height           "100px"
                       :width            "100%"}}]]])))
