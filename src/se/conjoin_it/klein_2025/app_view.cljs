(ns se.conjoin-it.klein-2025.app-view
  (:require [cljs.pprint :as pprint]
            [se.conjoin-it.klein-2025.events :refer [handle-main-events]]
            [se.conjoin-it.klein-2025.style :as style]
            [se.conjoin-it.klein-2025.cubic-splines.view :as cubic-splines-view]
            [se.conjoin-it.klein-2025.n-splines.view :as n-splines-view]
            [se.conjoin-it.klein-2025.quadratic-splines.view :as quadratic-splines-view]
            [se.conjoin-it.klein-2025.bernstein-polynomials.view :as bernstein-polynomials-view]
            [se.conjoin-it.klein-2025.super-mario.view :as super-mario-view]
            [se.conjoin-it.klein-2025.tangram.view :as tangram-view]
            [se.conjoin-it.klein-2025.timing-functions.view :as timing-functions-view]
            [se.conjoin-it.klein-2025.svg_api.view :as svg-api-view]
            [se.conjoin-it.klein-2025.css_api.view :as css-api-view]
            [se.conjoin-it.klein-2025.harmonic-oscillator.view :as harmonic-oscillator-view]
            [se.conjoin-it.klein-2025.monte_carlo_volume.view :as monte-carlo-volume-view]
            [se.conjoin-it.klein-2025.links.view :as links-view]))

(defn header-component
  [db]
  (let [at-home-page (= (:page db) :home)]
    [:div (merge {:style (merge {:padding          "10px"
                                 :border-radius    "5px"
                                 :display          "flex"
                                 :align-items      "center"
                                 :justify-content  "space-between"
                                 :color            style/kth-white
                                 :background-color style/kth-blue})})
     [:div {:style {:display     "flex"
                    :align-items "center"}}
      [:div (cond-> {:style (cond-> {:display "flex"}
                                    (not at-home-page) (assoc :cursor "pointer"))}
                    (not at-home-page)
                    (-> (assoc-in [:style :cursor] "pointer")
                        (assoc :on-click (fn []
                                           (println "Click on header")
                                           (handle-main-events {:name :page-changed :data :home})))))
       [style/education-icon {:color style/kth-white
                              :style {:margin-right "10px"}}]]
      [:div {:style {:font-size "140%"}}
       ;(let [from-path "M 10 50 C 60 20 120 10 180 30 C 240 50 300 30 360 10"
       ;      to-path   "M 10 10 C 60 40 120 50 180 30 C 240 10 300 30 360 50"]
       ;  [:svg {:view-box "0 0 500 60"
       ;         :height   "50px" :width "500px"}
       ;   [:path {:id     "title"
       ;           :d      from-path
       ;           :stroke "transparent" :stroke-width 0 :fill "none"}]
       ;   [:animate {:href "#title"
       ;              :attributeName "d"
       ;              :values (str from-path ";" to-path ";" from-path)
       ;              :dur "20s"
       ;              :repeat-count "indefinite"}]
       ;   [:text {:style {:stroke "white" :fill "white" :font-size "20px"}}
       ;    [:textPath {:href "#title" :start-offset 5}
       ;     "Matematik inom Webprogrammering"]]])
       "Matematik inom Webprogrammering - Kleindagarna 2025"]]
     [:div {:style    {:cursor "pointer"}
            :on-click (fn [] (js/alert (with-out-str (pprint/write db))))}
      [style/heart-icon {:color style/kth-white
                         :style {:height "32px"
                                 :width  "32px"}}]]]))

(defn section
  [db n display-name name]
  [:div {:style    {:cursor           "pointer"
                    :margin-bottom    "10px"
                    :padding          "10px"
                    :font-size        "120%"
                    :border-radius    "5px"
                    :color            ({1 "rgb(60,60,60)"
                                        2 "rgb(60,60,60)"
                                        3 "rgb(250, 250, 250)"
                                        4 "rgb(250, 250, 250)"
                                        5 "rgb(250, 250, 250)"} n)
                    :background-color ({1 "orange"
                                        2 "darkorange"
                                        3 "rgb(180,60,30)"
                                        4 "darkred"
                                        5 "rgb(100,100,100)"} n)}
         :on-click (fn [] (handle-main-events {:name :page-changed :data name}))}
   display-name])

(defn home-component
  [db]
  [:div {:style {:padding          "20px"
                 :height           "100%"
                 :background-color "rgb(245,245,245)"}}
   [section db 1 "Kvadratiska Bézierkurvor" :quadratic-splines]

   [section db 1 "Högre ordningens Bézierkurvor" :n-splines]

   [section db 1 "Bernstein polynomials" :bernstein-polynomials]

   [section db 1 "Kubiska Bézierkurvor" :cubic-splines]

   [section db 1 "SVG APIet" :svg-api]

   [:br]

   [section db 2 "Tidsstyrning av animeringar" :timing-functions]

   [section db 2 "CSS APIet" :css-api]

   [:br]

   [section db 3 "Harmonisk oscillator" :harmonic-oscillator]

   [section db 3 "Tangram" :tangram]

   [section db 3 "Super Mario - fritt fall" :super-mario]

   [:br]

   [section db 4 "Volym och dimension" :monte-carlo-volume]

   [:br]

   [section db 5 "Länkar" :links]
   ])

(defn app-component
  [db]
  [:div {:style {:font-family "Lato, sans-serif"
                 :height      (:screen-height db)}}
   [header-component db]
   [:div {:style {:margin-top "10px"}}
    (condp = (:page db)
      :home
      [home-component db]

      :quadratic-splines
      [quadratic-splines-view/main-component (:quadratic-splines db) (:screen-width db) (:screen-height db)]

      :n-splines
      [n-splines-view/main-component (:n-splines db) (:screen-width db) (:screen-height db)]

      :bernstein-polynomials
      [bernstein-polynomials-view/main-component (:bernstein-polynomials db) (:screen-width db) (:screen-height db)]

      :cubic-splines
      [cubic-splines-view/main-component (:cubic-splines db) (:screen-width db) (:screen-height db)]

      :super-mario
      [super-mario-view/main-component (:super-mario db) (:screen-width db)]

      :tangram
      [tangram-view/main-component (:tangram db) (:screen-width db) (:screen-height db)]

      :timing-functions
      [timing-functions-view/main-component (:timing-functions db) (:screen-width db)]

      :svg-api
      [svg-api-view/main-component (:svg-api db) (:screen-width db)]

      :css-api
      [css-api-view/main-component (:css-api db) (:screen-width db)]

      :harmonic-oscillator
      [harmonic-oscillator-view/main-component (:harmonic-oscillator db) (:screen-width db)]

      :monte-carlo-volume
      [monte-carlo-volume-view/main-component (:monte-carlo-volume db)]

      :links
      [links-view/main-component (:links db)]

      (println "No match for" (:page db))

      )]

   ]

  )

