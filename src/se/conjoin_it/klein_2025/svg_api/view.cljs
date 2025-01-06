(ns se.conjoin-it.klein-2025.svg_api.view
  (:require [se.conjoin-it.klein-2025.svg_api.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))

(defn main-component
  "The main component."
  [db-svg-api width]
  (when db-svg-api
    [:div {:id "svg-api"}
     [:div
      [:div {:style {:margin-bottom "20px"}}
       "Taget från "
       [:a {:href "https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/d"}
        "https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/d"]]
      [:img {:src "asset/path-commands.png"}]]]))

