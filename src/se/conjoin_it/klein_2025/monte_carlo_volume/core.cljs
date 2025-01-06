(ns se.conjoin-it.klein-2025.monte_carlo_volume.core
  (:require [clojure.test :refer [is]]))

(def section-name :monte-carlo-volume)

(defn inside?
  [xs]
  (< (->> xs
          (reduce (fn [a x]
                    (if (> a 1)
                      (reduced 2)
                      (+ a (* x x))))
                  0))
     1))

(defn update-with-sample
  [db]
  (let [dimension (get-in db [section-name :dimension])
        xs (->> (range dimension)
                (map (fn [_] (rand))))
        [x1 x2] (->> (split-at (/ dimension 2) xs)
                     (map (fn [ns]
                            (-> (reduce (fn [a n]
                                          (+ a (* n n)))
                                        0
                                        ns)
                                (js/Math.sqrt)))))]
    (update db section-name (fn [db-monte-carlo]
                              (-> (if (inside? xs)
                                    (update db-monte-carlo :inside-count inc)
                                    db-monte-carlo)
                                  (update :samples inc)
                                  (update :points conj [x1 x2]))))))
