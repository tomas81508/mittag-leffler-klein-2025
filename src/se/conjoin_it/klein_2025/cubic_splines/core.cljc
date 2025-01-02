(ns se.conjoin-it.klein-2025.cubic-splines.core
  (:require [clojure.test :refer [is]]))

(defn get-point-in-between
  {:test (fn []
           (is (= (get-point-in-between [[10 60] [30 40]] 0.5)
                  [20.0 50.0])))}
  [[p1 p2] t]
  (map (fn [x y] (+ (* (- 1 t) x) (* t y))) p1 p2))

(defn replace-points
  {:test (fn []
           (is (= (replace-points [[[100 50]] [[40 30]]] [40 30] [90 90])
                  [[[100 50]] [[90 90]]])))}
  [control-points original-point modified-point]
  (mapv (fn [ps] (mapv (fn [p] (if (= p original-point) modified-point p))
                       ps))
        control-points))


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
                               (map (fn [pairs] (get-point-in-between pairs (- t (int t))))))]
          (recur (conj result next-points)
                 next-points))))))

(defn add-curve
  {:test (fn []
           (is (= (add-curve [[[100 500] [150 150] [400 200] [450 400]]
                               [[450 400] [500 600] [700 600] [800 300]]])
                  [[[100 500] [150 150] [400 200] [450 400]]
                   [[450 400] [500 600] [700 600] [800 300]]
                   [[800 300] [820 320] [840 340] [860 360]]])))}
  [control-points]
  (let [last-control-point (last (last control-points))]
    (conj control-points (->> (range 4)
                              (mapv (fn [s] (map + last-control-point [(* s 20) (* s 20)])))))))

(defn remove-curve
  [control-points]
  (->> control-points
       (drop-last)
       (into [])))
