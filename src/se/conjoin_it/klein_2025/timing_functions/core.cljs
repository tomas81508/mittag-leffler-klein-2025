(ns se.conjoin-it.klein-2025.timing-functions.core
  (:require [clojure.test :refer [is]]))

(def predefined-timing-functions
  {:ease        {:p1 {:x 0.25 :y 0.1} :p2 {:x 0.25 :y 1.0}}
   :linear      {:p1 {:x 0.0 :y 0.0} :p2 {:x 1.0 :y 1.0}}
   :ease-in     {:p1 {:x 0.42 :y 0} :p2 {:x 1.0 :y 1.0}}
   :ease-out    {:p1 {:x 0.0 :y 0.0} :p2 {:x 0.58 :y 1.0}}
   :ease-in-out {:p1 {:x 0.42 :y 0.0} :p2 {:x 0.58 :y 1.0}}})
