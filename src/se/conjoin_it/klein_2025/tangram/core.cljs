(ns se.conjoin-it.klein-2025.tangram.core
  (:require [se.conjoin-it.klein-2025.style :as style]))

(def triangle-big "-280,-90 0,190 280,-90")
(def triangle-medium "-198,-66 0,132 198,-66")
(def triangle-small "-130,-44 0,86  130,-44")
(def square "-130,0 0,-130 130,0 0,130")
(def parallelogram "-191,61 69,61 191,-61 -69,-61")

(defn get-shape-definition
  [shape-key]
  (get {:tb1 {:coordinates triangle-big
              :color       style/clojure-dark-green}
        :tb2 {:coordinates triangle-big
              :color       style/clojure-dark-blue}
        :tm  {:coordinates triangle-medium
              :color       style/clojure-dark-blue}
        :sqr {:coordinates square
              :color       style/clojure-light-blue}
        :par {:coordinates parallelogram
              :color       style/clojure-dark-green}
        :ts1 {:coordinates triangle-small
              :color       style/clojure-light-green}
        :ts2 {:coordinates triangle-small
              :color       style/clojure-light-green}}
       shape-key))

(def logo-model {:tb1 [0 -210 0]
                 :tb2 [-210 0 90]
                 :tm  [207 207 45]
                 :sqr [150 0 0]
                 :par [-89 239 0]
                 :ts1 [0 106 180]
                 :ts2 [256 -150 270]})

(def heart-model {:tb1 [-160 120 0]
                  :tb2 [150 -90 180]
                  :tm  [-270 -93 45]
                  :sqr [0 -300 0]
                  :par [231 91 0]
                  :ts1 [150 224 0]
                  :ts2 [-106 -150 90]})

(def bird-model {:tb1 [-296 166 45]
                 :tb2 [0 40 225]
                 :tm  [200 136 270]
                 :sqr [-42 -212 45]
                 :par [-138 -424 135]
                 :ts1 [139 -181 315]
                 :ts2 [352 214 225]})

(def child-model {:tb1 [-88 -46 135]
                  :tb2 [208 86 -45]
                  :tm  [120 -300 0]
                  :sqr [104 352 36]
                  :par [-140 -300 315]
                  :ts1 [-404 -380 315]
                  :ts2 [328 -434 180]})

(def house-model {:tb1 [0 -250 0]
                  :tb2 [96 54 0]
                  :tm  [-218 -152 315]
                  :sqr [-106 266 45]
                  :par [-212 56 315]
                  :ts1 [162 -104 180]
                  :ts2 [264 -206 270]})

(def cat-model {:tb1 [-40 -120 90]
                :tb2 [20 -420 135]
                :tm  [-226 -38 270]
                :sqr [-220 276 0]
                :par [350 -462 315]
                :ts1 [-320 428 90]
                :ts2 [-120 428 270]})

(def camel-model {:tb1 [-250 -256 315]
                  :tb2 [100 -260 270]
                  :tm  [-190 -30 0]
                  :sqr [40 40 0]
                  :par [278 40 90]
                  :ts1 [262 276 90]
                  :ts2 [366 380 180]})

(def model-name-cycle [:logo :heart :bird :child :house :cat :camel :logo])

(defn get-model [model-name]
  (get {:logo  logo-model
        :heart heart-model
        :bird  bird-model
        :child child-model
        :house house-model
        :cat   cat-model
        :camel camel-model}
       model-name))

(defn get-next-model-name [model-name]
  (->> model-name-cycle
       (map-indexed (fn [index item] [index item]))
       (some (fn [[index item]] (when (= item model-name) index)))
       (inc)
       (get model-name-cycle)))



