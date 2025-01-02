(ns se.conjoin-it.klein-2025.quadratic-splines.core
  (:require [clojure.test :refer [is]]))

(defn get-points-between-control-points
  [control-points]
  (->> control-points
       (partition 2 1)
       (map (fn [[[x1 y1] [x2 y2]]]
              (for [t (range 0 1.01 0.1)]
                [(+ (* (- 1 t) x1) (* t x2))
                 (+ (* (- 1 t) y1) (* t y2))])))))

(defn zip-collections
  {:test (fn []
           (is (= (zip-collections [:a :b :c] [:x :y :z])
                  [[:a :x] [:b :y] [:c :z]])))}
  [coll1 coll2]
  (map vector coll1 coll2))
