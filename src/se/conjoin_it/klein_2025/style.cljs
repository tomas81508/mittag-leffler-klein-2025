(ns se.conjoin-it.klein-2025.style
  (:require [reagent.core :as reagent]))

(def kth-blue "rgb(0,0,93)")
(def kth-white "rgb(252,252,252)")

(def clojure-light-blue "rgb(145,182,251)")
(def clojure-dark-green "rgb(101,175,59)")
(def clojure-light-green "rgb(147,218,82)")
(def clojure-dark-blue "rgb(90,131,213)")
(def clojure-darker-blue "rgb(38,81,154)")
(def clojure-darkest-blue "rgb(22,49,92)")

(defn education-icon
  [{color :color
    style :style}]
  [:svg {:view-box       "0 0 100 75"
         :height         "32px"
         :width          "32px"
         :style          style
         :fill           "transparent"
         :stroke         color
         :stroke-linecap "round"
         :stroke-width   "4px"}
   [:path {:d "M 50 10 L 10 25 L 50 40 L 90 25 Z"}]
   [:path {:d "M 25 32 v 20"}]
   [:path {:d "M 75 32 v 20"}]
   [:path {:d "M 88 27 v 15"}]
   [:path {:d "M 25 52 Q 50 70 75 52"}]])

(defn heart-icon
  [{color :color
    style :style}]
  [:svg {:viewBox      "0 0 100 100"
         :style        style
         :stroke       color
         :stroke-width "4px"}
   [:path {:fill "none"
           :d    "M 10,30
                  A 20,20 0,0,1 50,30
                  A 20,20 0,0,1 90,30
                  Q 90,60 50,90
                  Q 10,60 10,30
                  z"}]])

(defn button
  [{text     :text
    style    :style
    on-click :on-click}]
  [:button {:style    (merge {:padding          "10px 20px"
                              :border           "none"
                              :border-radius    "5px"
                              :color            kth-white
                              :cursor           "pointer"
                              :background-color kth-blue
                              :width            "180px"}
                             style)
            :on-click on-click}
   text])

(defn e->value
  [e]
  (.-value (.-target e)))

(defn input
  [{value     :value
    label     :label
    on-change :on-change
    on-blur   :on-blur}]
  [:div {:style {:margin-bottom    "10px"
                 :background-color "rgb(240,240,240)"
                 :padding          "5px 10px 10px"
                 :width            "160px"
                 :border-radius    "5px"}}
   [:label {:style {:font-size "80%"}}
    label]
   [:input {:value     value
            :style     {:padding "5px"}
            :on-change (fn [e] (on-change (e->value e)))
            :on-blur   (fn [e] (on-blur (e->value e)))}]])


(defn dropdown
  [{label     :label
    value     :value
    on-change :on-change
    options   :options}]
  [:div {:style {:margin-bottom "10px"
                 :background-color "rgb(240,240,240)"
                 :padding          "5px 10px 10px"
                 :width            "160px"
                 :border-radius    "5px"}}
   [:label {:style {:font-size "80%"}}
    label]
   [:select {:value     (or value "NA")
             :style     {:padding "5px"
                         :width   "150px"}
             :on-change (fn [e] (on-change (keyword (e->value e))))}
    (->> (if value options (cons {:label "NA" :value "NA"} options))
         (map-indexed (fn [index {option-label :label
                                  option-value :value}]
                        [:option {:key   index
                                  :value option-value}
                         option-label])))]])

(defn display
  [{label :label
    value :value}]
  [:div {:style {:margin-bottom    "10px"
                 :background-color "rgb(240,240,240)"
                 :padding          "5px 10px"
                 :width            "160px"
                 :border-radius    "5px"}}
   [:label {:style {:font-size "80%"}}
    label]
   [:div value]])


