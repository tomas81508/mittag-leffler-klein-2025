(ns se.conjoin-it.klein-2025.super-mario.core
  (:require [clojure.test :refer [is]]))

(def section-name :super-mario)

(def clojure-boxes
  #{[1000 250] [900 150] [250 350] [1200 250] [1450 400] [450 250] [1400 400] [550 150] [250 300] [1000 350] [1350 250] [1250 150] [1250 350] [850 350] [600 350] [700 150] [1000 300] [1000 200] [1100 150] [1100 200] [600 300] [450 300] [1350 150] [1250 300] [1400 300] [750 250] [850 400] [750 400] [850 250] [600 200] [1450 150] [500 150] [100 400] [1400 150] [850 300] [450 200] [750 350] [250 200] [1100 400] [1350 200] [1200 400] [1350 400] [1200 200] [300 150] [50 350] [1100 350] [1100 300] [1150 400] [250 150] [950 150] [1150 250] [1450 300] [550 400] [150 400] [500 400] [250 400] [1100 250] [450 350] [1350 350] [350 150] [50 300] [600 250] [50 250] [150 150] [1000 400] [1350 300] [750 200] [850 200] [50 200] [250 250] [100 150] [750 300]})

(defn maybe-move-mario-x
  [db delta]
  (let [vx (get-in db [section-name :mario :vx])
        velocity 0.2
        directions (get-in db [section-name :directions])]
    (cond (contains? directions "ArrowLeft")
          (update-in db [section-name :mario]
                     (fn [mario]
                       (-> mario
                           (update :x + (* delta vx))
                           (assoc :vx (- velocity)
                                  :direction :left))))

          (contains? directions "ArrowRight")
          (update-in db [section-name :mario]
                     (fn [mario]
                       (-> mario
                           (update :x + (* delta vx))
                           (assoc :vx velocity
                                  :direction :right))))

          (zero? vx) db

          :else
          (update-in db [section-name :mario]
                     (fn [mario]
                       (-> mario
                           (update :vx (fn [vx] (if (neg? vx)
                                                  (min 0 (+ vx (* delta 0.001)))
                                                  (max 0 (- vx (* delta 0.001))))))
                           (update :x + (* delta vx))))))))

(defn maybe-move-mario-y
  [db delta]
  (let [mario (get-in db [section-name :mario])
        vy (:vy mario)
        y (:y mario)
        x (:x mario)
        boxes (get-in db [section-name :boxes])]
    (cond (and (contains? (get-in db [section-name :directions]) "ArrowUp")
               (zero? vy))
          (-> db
              (assoc-in [section-name :mario :vy] 0.6)
              (update-in [section-name :mario :y] - 0.6))

          (or (neg? y)
              (not (zero? vy)))
          (let [gravity 0.0018
                new-y (min 0 (- y (* vy delta)))
                new-vy (if (zero? new-y) 0 (- vy (* gravity delta)))
                colliding-boxes (->> boxes
                                     (filter (fn [[bx by]]
                                               (and
                                                 (<= (- bx 26) x (+ bx 50))
                                                 (<= (- new-y)
                                                     by
                                                     (- y))))))]

            (if (empty? colliding-boxes)
              (-> db
                  (assoc-in [section-name :mario :y] new-y)
                  (assoc-in [section-name :mario :vy] new-vy))
              (-> db
                  (assoc-in [section-name :mario :y] (-> colliding-boxes
                                                         (first)
                                                         (second)
                                                         (-)
                                                         ))
                  (assoc-in [section-name :mario :vy] 0))))

          (contains? (:directions db) "ArrowUp")
          (-> db
              (assoc-in [section-name :mario :vy] 0.6)
              (update-in [section-name :mario :y] - 0.6))

          (and (zero? y)
               (not (zero? vy)))
          (assoc-in db [section-name :mario :vy] 0)

          :else
          db)))

(defn maybe-move-mario
  [db delta]
  (-> db
      (maybe-move-mario-x delta)
      (maybe-move-mario-y delta)))

(defn update-time
  [db new-time]
  (let [time (get-in db [section-name :time])
        delta (if-not time 0 (- new-time time))]
    (-> db
        (assoc-in [section-name :time] new-time)
        (maybe-move-mario delta))))


