(ns se.conjoin-it.klein-2025.tangram.physics)

;-- MODEL

(defn static? [shape] (= (count shape) 3))
(defn moving? [shape] (not (static? shape)))

(defn static-model? [model]
  (static? (:tb1 model)))

(defn moving-model? [model]
  (not (static-model? model)))

;-- SET PATTERN

(defn set-shape
  [shape target-static-shape]
  (if (= (count shape) 3)
    (into [] (concat [0 0 0] shape target-static-shape))
    (into [] (concat (subvec shape 0 6) target-static-shape))))

(defn set-target-model
  [model target-model]
  (reduce-kv (fn [a k v]
               (assoc a k (set-shape v (get target-model k))))
             {}
             model))


;-- STEP

(def time-constant 1000)

(defn to-nearest-angle
  [current target]
  (let [distance (- target current)]
    (cond (< distance -180)
          (+ target 360)

          (> distance 180)
          (- target 360)

          :else
          target)))

(defn normalize
  [angle]
  (cond (< angle 0)
        (+ angle 360)

        (< 360 angle)
        (- angle 360)

        :else
        angle))

(defn step-shape
  [shape dt stiffness damping]
  {:pre [stiffness damping]}
  (let [p-stiffness stiffness
        a-stiffness stiffness
        p-damping damping
        a-damping damping]
    (if-not (moving? shape)
      shape
      (let [[vx vy va x y a tx ty ta] shape
            tt (to-nearest-angle a ta)
            ax (- (* p-stiffness (- tx x)) (* p-damping vx))
            ay (- (* p-stiffness (- ty y)) (* p-damping vy))
            at (- (* a-stiffness (- tt a)) (* a-damping va))
            nvx (+ vx (* ax dt))
            nvy (+ vy (* ay dt))
            nva (+ va (* at dt))
            nx (+ x (* nvx dt))
            ny (+ y (* nvy dt))
            na (+ a (* nva dt))
            dx (abs (- tx nx))
            dy (abs (- ty ny))
            da (abs (- tt na))]
        (if (and (< dx 1)
                 (< dy 1)
                 (< da 1)
                 (< (abs nvx) 0.6)
                 (< (abs nvy) 0.6)
                 (< (abs nva) 0.6))
          [tx ty (normalize ta)]
          [nvx nvy nva nx ny na tx ty ta])))))

(defn step
  [model time-delta stiffness damping]
  (let [dt (/ time-delta time-constant)]
    (reduce-kv (fn [a k v]
                 (assoc a k (step-shape v dt stiffness damping)))
               {}
               model)))




