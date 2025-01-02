(ns se.conjoin-it.klein-2025.bernstein-polynomials.core
  (:require [clojure.test :refer [is]]))

(def n 100)

(def delta (/ 1 n))

(defn !
  [n]
  (loop [result 1
         n n]
    (if (zero? n) result (recur (* n result) (dec n)))))

(declare n-over-k)

(defn- n-over-k-calculation [n k] (/ (! n) (* (! (- n k)) (! k))))

(def n-over-k (memoize n-over-k-calculation))

(defn bernstein-polynomial
  [n k t]
  (* (n-over-k n k)
     (js/Math.pow t k)
     (js/Math.pow (- 1 t) (- n k))))

(defn compute-plot-values
  [degree]
  (reduce (fn [a t]
            (->> a
                 (map-indexed (fn [k values]
                                (conj values (bernstein-polynomial degree k (/ t n)))))))
          (into [] (repeat (inc degree) []))
          (range (inc n))))