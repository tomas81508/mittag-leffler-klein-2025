(ns se.conjoin-it.klein-2025.harmonic-oscillator.core
  (:require [clojure.test :refer [is]]))

(def section-name :harmonic-oscillator)

(def time-constant 0.002)

(def baseline 0)

(defn hookes-force
  [stiffness y]
  (- (* stiffness (- y baseline))))

(defn damping-force
  [damping vy]
  (- (* damping vy)))

(defn move
  [db delta]
  (let [stiffness (get-in db [section-name :stiffness])
        damping (get-in db [section-name :damping])
        y (get-in db [section-name :y])
        vy (get-in db [section-name :vy])
        new-vy (+ vy (* delta (+ (hookes-force stiffness y)
                                 (damping-force damping vy))))
        new-y (+ y (* delta new-vy))]
    (-> db
        (assoc-in [section-name :y] new-y)
        (assoc-in [section-name :vy] new-vy))))

(defn time-tick
  [db new-time]
  (let [time (get-in db [section-name :time])
        delta (if-not time 0 (- new-time time))]
    (-> db
        (assoc-in [section-name :time] new-time)
        (move (* delta time-constant)))))