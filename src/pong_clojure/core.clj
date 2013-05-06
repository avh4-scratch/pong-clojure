(ns pong-clojure.core
  (:use [fui.core])
  (:use [fui.drawing])
  (:use [fui.geometry])
  (:use [clojure.math.numeric-tower])
  (:use [lamina.core])
  (:use [clj-time.core]) )

(def grass-color (color 87 153 88))
(def ball-color (color 13 75 145))
(def lines-color (color 255 255 245))

(defn new-ball [w h speed]
  {:x (/ w 2), :y (/ h 2),
          :vx speed, :vy speed})

(defn adjust-position [sprite]
  (assoc sprite
    :x (+ (:x sprite) (:vx sprite))
    :y (+ (:y sprite) (:vy sprite)) ))

(defn adjust-velocity [sprite w h]
  (assoc sprite
    :vy (if (>= (:y sprite) h) (- (abs (:vy sprite))) (:vy sprite))
    :vx (if (>= (:x sprite) w) (- (abs (:vx sprite))) (:vx sprite)) ))

(defn update-ball [state time-signal]
  (assoc state :ball
    (-> (state :ball)
      (adjust-velocity (state :width) (state :height))
      adjust-position) ))

(defn draw-background [{w :width h :height}]
  (let [bounds [0 0 w h]]
    [(fill-rect bounds grass-color)
     (fill-rect (inset 20 bounds) lines-color)
     (fill-rect (inset 25 bounds) grass-color) ]))

(defn draw-ball [{x :x y :y}]
  [(fill-oval (from-center x y 25) ball-color)])

(defn draw-pong-game [state]
  (concat
    (draw-background state)
    (draw-ball (:ball state))))

(defn pong-game [w h]
  (let [game-state {:width w, :height h, :ball (new-ball w h 5)}
        time-signal (periodically 30 now)
        game-state-signal (reductions* update-ball game-state time-signal)
        graphics-signal (map* draw-pong-game game-state-signal)
        ]
    (window (component w h graphics-signal))))
