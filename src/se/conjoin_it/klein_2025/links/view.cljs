(ns se.conjoin-it.klein-2025.links.view
  (:require [se.conjoin-it.klein-2025.links.controller :refer [handle-event]]
            [se.conjoin-it.klein-2025.style :as style]
            [reagent.core :as reagent]))

(defn link-component
  [link title authors]
  [:div {:style {:padding       "10px"
                 :margin-bottom "10px"
                 :background-color "rgb(240,240,240)"}}
   [:a {:href link}
    title]
   (when authors
     [:div {:style {:color "gray"}} authors])])

(defn main-component
  "The main component."
  [db-links width]
  (when db-links
    [:div {:id "links"}
     [:div
      [:div {:style {:margin-bottom "20px"}}
       [link-component
        "https://www.youtube.com/watch?v=jvPPXbo87ds"
        "Continuity of splines"
        "Freja Holmér"]
       [link-component
        "https://www.youtube.com/watch?v=aVwxzDHniEw"
        "The Beauty of Bézier Curves"
        "Freja Holmér"]
       [link-component
        "https://www.youtube.com/watch?v=3t9FSfM1fIQ"
        "Crafting Artisanal Vector Graphics"
        "Timothy Pratley and Chris Houser"]
       [link-component
        "https://en.wikipedia.org/wiki/B%C3%A9zier_curve"
        "Bézier curve"
        "Wikipedia"]
       [link-component
        "https://en.wikipedia.org/wiki/Bernstein_polynomial"
        "Bernstein polynomials"
        "Wikipedia"]
       [link-component
        "https://en.wikipedia.org/wiki/SVG"
        "SVG"
        "Wikipedia"]
       [link-component
        "https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/d"
        "(SVG - Path) - d attribute"
        "Mozilla developer docs"]
       [link-component
        "https://developer.mozilla.org/en-US/docs/Web/CSS/animation-timing-function"
        "animation-timing-function"
        "Mozilla developer docs"]
       [link-component
        "https://en.wikipedia.org/wiki/Harmonic_oscillator"
        "Harmonisk oscillator"
        "Wikipedia"]
       [link-component
        "https://en.wikipedia.org/wiki/Damping"
        "Damping"
        "Wikipedia"]
       [link-component
        "https://en.wikipedia.org/wiki/Volume_of_an_n-ball"
        "Volume of an n-ball"
        "Wikipedia"]
       [link-component
        "https://adventofcode.com/2024"
        "Advent of code"
        "Eric Wastl"]
       ]
      ]]))

