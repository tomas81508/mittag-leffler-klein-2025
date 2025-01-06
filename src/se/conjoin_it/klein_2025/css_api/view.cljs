(ns se.conjoin-it.klein-2025.css_api.view
  (:require [se.conjoin-it.klein-2025.css_api.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))

(defn main-component
  "The main component."
  [db-css-api width]
  (when db-css-api
    [:div {:id "css-api"}
     [:div
      [:div {:style {:margin-bottom "20px"}}
       "Taget från "
       [:a {:href "https://developer.mozilla.org/en-US/docs/Web/CSS/animation-timing-function"}
        "https://developer.mozilla.org/en-US/docs/Web/CSS/animation-timing-function"]]
      [:img {:style {:width "900px"}
             :src "asset/css-animate-timing-functions.png"}]]]))

