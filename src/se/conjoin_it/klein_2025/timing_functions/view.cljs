(ns se.conjoin-it.klein-2025.timing-functions.view
  (:require [se.conjoin-it.klein-2025.timing-functions.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.timing-functions.core :refer []]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))


(def x-size 400)
(def y-size 600)
(def padding-size 100)

(defn format-number [n dec]
  (.toFixed n dec))

(defn get-cubic-bezier [p1 p2]
  (str "cubic-bezier(" (format-number (/ (:x p1) x-size) 3)
       ", " (format-number (- 1 (/ (- (:y p1) padding-size) x-size)) 3)
       ", " (format-number (/ (:x p2) x-size) 3)
       ", " (format-number (- 1 (/ (- (:y p2) padding-size) x-size)) 3) ")"))

(defn get-path [p1 p2]
  (str "M 0 " (- y-size padding-size) " C " (:x p1) " " (:y p1) ", " (:x p2) " " (:y p2) ", " x-size " " padding-size))

(defn timing-function-svg
  [{local-state-atom   :local-state-atom
    box-id             :box-id
    p1                 :p1
    p2                 :p2
    animation-duration :animation-duration}]
  (let [local-state (deref local-state-atom)
        mouse-event-listener-fn (fn [mouse-event]
                                  (swap! local-state-atom
                                         assoc
                                         :mouse-position
                                         {:x (.-offsetX mouse-event)
                                          :y (.-offsetY mouse-event)}))
        fix-point-position! (fn [point-id]
                              (let [local-state (deref local-state-atom)]
                                (-> (js/document.getElementById box-id)
                                    (.removeEventListener "mousemove" mouse-event-listener-fn))
                                (when (:mouse-position local-state)
                                  (handle-event {:name :animations-point-changed
                                                  :data {:point point-id
                                                         :value {:x (/ (:x (:mouse-position local-state))
                                                                       x-size)
                                                                 :y (- 1
                                                                       (/ (- (:y (:mouse-position local-state))
                                                                             padding-size)
                                                                          x-size))}}}))
                                ; 250 -> 0, 50 -> 1, 0 -> 1.25
                                (swap! local-state-atom
                                       assoc
                                       :mouse-listener-active nil
                                       :mouse-position nil)))
        move-point-position! (fn [point-id]
                               (-> (js/document.getElementById box-id)
                                   (.addEventListener "mousemove" mouse-event-listener-fn))
                               (swap! local-state-atom assoc :mouse-listener-active point-id))]
    [:svg {:height (str y-size "px")
           :width  (str x-size "px")
           :style  {:background-color "rgb(245,245,245)"}}
     [:rect {:height x-size
             :width  x-size
             :y      padding-size
             :stroke "lightgray"
             :fill   "transparent"}]
     [:path {:d            (get-path p1 p2)
             :stroke       style/clojure-light-blue
             :fill         "transparent"
             :stroke-width "2px"}]
     [:path {:d            (get-path p1 p2)
             :stroke       style/clojure-light-blue
             :style        {:opacity          0.3
                            :transform        "rotate(180deg)"
                            :transform-origin (str (/ x-size 2) "px " (/ y-size 2) "px")}
             :fill         "transparent"
             :stroke-width "2px"}]
     [:line {:x1    0 :y1 (+ padding-size x-size)
             :x2    (:x p1) :y2 (:y p1)
             :style {:stroke       style/clojure-darker-blue
                     :stroke-width 2}}]
     [:circle {:r             5 :cx (:x p1) :cy (:y p1)
               :style         {:cursor (if (:mouse-listener-active local-state)
                                         "grabbing"
                                         "grab")}
               :fill          style/clojure-darker-blue
               :stroke        style/clojure-darker-blue
               :stroke-width  2
               :on-mouse-down (fn [] (move-point-position! :graph-point1))
               :on-mouse-up   (fn [] (fix-point-position! :graph-point1))}]
     [:line {:x1    x-size :y1 padding-size
             :x2    (:x p2) :y2 (:y p2)
             :style {:stroke       style/clojure-darker-blue
                     :stroke-width 2}}]
     [:circle {:r             5 :cx (:x p2) :cy (:y p2)
               :style         {:cursor (if (:mouse-listener-active local-state)
                                         "grabbing"
                                         "grab")}
               :fill          style/clojure-darker-blue
               :stroke        style/clojure-darker-blue
               :stroke-width  2
               :on-mouse-down (fn [] (move-point-position! :graph-point2))
               :on-mouse-up   (fn [] (fix-point-position! :graph-point2))}]
     [:circle {:r     5
               :fill  style/clojure-dark-green
               :cx    (if (= (:animation local-state) :start) 0 x-size)
               :cy    (if (= (:animation local-state) :start)
                        (+ padding-size x-size)
                        padding-size)
               :style {:transition (str "cx " animation-duration "ms linear, "
                                        "cy " animation-duration "ms "
                                        (get-cubic-bezier p1 p2))}}]]))

(defn timing-functions-component
  [db-timing-functions width]
  (let [box-id "timing-function-graph"
        local-state-atom (reagent/atom {:animation             :start
                                        :box-bounding-rect     nil
                                        :mouse-position        nil
                                        :mouse-listener-active false})
        toggle-animation! (fn []
                            (swap! local-state-atom update :animation
                                   (fn [v] (if (= v :start) :end :start))))]
    (reagent/create-class
      {:component-did-mount
       (fn []
         (swap! local-state-atom
                assoc
                :box-bounding-rect
                (let [rect (-> (js/document.getElementById box-id)
                               (.getBoundingClientRect))]
                  {:x (.-x rect) :y (.-y rect)})))

       :reagent-render
       (fn [db-timing-functions width]
         (let [local-state (deref local-state-atom)
               box-bounding-rect (:box-bounding-rect local-state)
               mouse-position (:mouse-position local-state)
               p1 (if (and (= (:mouse-listener-active local-state) :graph-point1)
                           mouse-position)
                    mouse-position
                    {:x (* (get-in db-timing-functions [:graph-point1 :x]) x-size)
                     :y (+ padding-size
                           (* x-size (- 1 (get-in db-timing-functions [:graph-point1 :y]))))})
               p2 (if (and (= (:mouse-listener-active local-state) :graph-point2)
                           mouse-position)
                    mouse-position
                    {:x (* (get-in db-timing-functions [:graph-point2 :x]) x-size)
                     :y (+ padding-size
                           (* x-size (- 1 (get-in db-timing-functions [:graph-point2 :y]))))})]
           [:section {:style {:margin-top "50px"}}

            [:div {:style {:display       "flex"
                           :margin-bottom "20px"}}
             [:div {:id    box-id
                    :style {:height        (str y-size "px")
                            :width         (str x-size "px")
                            :margin-left   (if (> width 400) "20px" "0px")
                            :margin-bottom "1rem"}}
              (when box-bounding-rect
                [timing-function-svg {:local-state-atom   local-state-atom
                                      :box-id             box-id
                                      :p1                 p1
                                      :p2                 p2
                                      :animation-duration (:animation-duration db-timing-functions)}])]

             [:div {:style {:margin-left (if (< width 400) "10px" "20px")
                            :width       "170px"}}
              [style/input {:label     "Duration"
                            :info-text "Given in ms"
                            :value     (:animation-duration-input db-timing-functions)
                            :on-change (fn [value]
                                         (handle-event {:name :animation-duration-input-changed
                                                        :data value}))
                            :on-blur (fn [value]
                                       (handle-event {:name :animation-duration-changed
                                                      :data value}))}]

              [style/dropdown {:label       "Fördefinierade funktioner"
                               :options     [{:label "ease" :value :ease}
                                             {:label "linear" :value :linear}
                                             {:label "ease-in" :value :ease-in}
                                             {:label "ease-out" :value :ease-out}
                                             {:label "ease-in-out" :value :ease-in-out}]
                               :placeholder "Choose"
                               :style       {:margin-bottom "16px"}
                               :value       (:predefined db-timing-functions)
                               :on-change   (fn [value]
                                              (handle-event {:name :predefined-changed
                                                             :data value}))}]

              [style/dropdown {:label     "Animation element"
                               :options   [{:label "color" :value :background-color}
                                           {:label "margin-left" :value :margin-left}
                                           {:label "opacity" :value :opacity}
                                           {:label "transform" :value :transform}
                                           {:label "width" :value :width}]
                               :style     {:margin-bottom "80px"}
                               :value     (:animation-element db-timing-functions)
                               :on-change (fn [value]
                                            (handle-event {:name :element-changed
                                                           :data value}))}]

              [style/button {:text    "Animera"
                             :style    {:margin-top "10px"}
                             :on-click (fn [] (toggle-animation!))}]]]

            [:div {:style {:margin-bottom "2rem"
                           :margin-left   "20px"
                           :font-weight   "700"}}
             (str "transition: \"all " (:animation-duration db-timing-functions) "ms "
                  (if (:predefined db-timing-functions)
                    (name (:predefined db-timing-functions))
                    (get-cubic-bezier p1 p2)) "\"")]


            (let [transition-style (str "all " (:animation-duration db-timing-functions) "ms " (get-cubic-bezier p1 p2))
                  animation-margin 50
                  animation-width (- width (* 2 animation-margin))]
              [:div {:style {:width        (str animation-width "px")
                             :margin       (str "0 " animation-margin "px")
                             :border-right "3px solid lightgray"
                             :border-left  "3px solid lightgray"}}
               (condp = (:animation-element db-timing-functions)
                 :background-color
                 [:div {:style {:margin-bottom    "1rem"
                                :transition       transition-style
                                :background-color (if (= (:animation local-state) :start)
                                                    style/clojure-light-blue
                                                    style/clojure-dark-green)
                                :height           "50px"
                                :width            (str animation-width "px")}}]

                 :margin-left
                 [:div {:style {:background-color style/clojure-dark-green
                                :margin-bottom    "1rem"
                                :transition       transition-style
                                :border-radius    "50%"
                                :margin-left      (if (= (:animation local-state) :start)
                                                    0 (str (- animation-width 50) "px"))
                                :height           "50px"
                                :width            "50px"}}]

                 :width
                 [:div {:style {:background-color style/clojure-light-blue
                                :margin-bottom    "1rem"
                                :transition       transition-style
                                :width            (if (= (:animation local-state) :start)
                                                    "50px" (str animation-width "px"))
                                :height           "50px"}}]

                 :opacity
                 [:div {:style {:background-color style/clojure-light-green
                                :margin-bottom    "1rem"
                                :transition       transition-style
                                :opacity          (if (= (:animation local-state) :start)
                                                    0
                                                    1)
                                :height           "50px"
                                :width            (str animation-width "px")}}]

                 :transform
                 [:div {:style {:perspective "800px"}}
                  [:div {:style {:background-color style/clojure-dark-blue
                                 :margin-bottom    "1rem"
                                 :transition       transition-style
                                 :transform        (if (= (:animation local-state) :start)
                                                     "rotateY(0deg)"
                                                     "rotateY(180deg)")
                                 :height           "50px"
                                 :width            (str animation-width "px")}}]])])]
           ))})))

(defn main-component
  "The main component."
  [db-timing-functions width]
  (when db-timing-functions
    [:div {:id "timing-functions"}
     [timing-functions-component db-timing-functions width]]))

