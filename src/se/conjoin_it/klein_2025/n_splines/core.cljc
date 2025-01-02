(ns se.conjoin-it.klein-2025.n-splines.core
  (:require [clojure.test :refer [is]]))

(defn get-point-in-between
  {:test (fn []
           (is (= (get-point-in-between [[10 60] [30 40]] 0.5)
                  [20.0 50.0])))}
  [[p1 p2] t]
  (map (fn [x y] (+ (* (- 1 t) x) (* t y))) p1 p2))


(defn get-points
  {:test (fn []
           (is (= (get-points 0.5 [[0 0] [100 50] [100 100]])
                  [[[0 0] [100 50] [100 100]]
                   [[50.0 25.0] [100.0 75.0]]
                   [[75.0 50.0]]])))}
  [t control-points]
  (loop [result [control-points]
         points control-points]
    (let [nps (->> points
                   (partition 2 1))]
      (if (empty? nps)
        result
        (let [next-points (->> nps
                               (map (fn [pairs] (get-point-in-between pairs t))))]
          (recur (conj result next-points)
                 next-points))))))

(defn add-degree
  {:test (fn []
           (is (= (add-degree [[100 100] [200 200]])
                  [[100 100] [200 200] [250 250]])))}
  [control-points]
  (let [last-control-point (last control-points)]
    (conj control-points (map + last-control-point [50 50]))))

(defn remove-degree
  [control-points]
  (->> control-points
       (drop-last)
       (into [])))
