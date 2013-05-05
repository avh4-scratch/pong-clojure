(ns pong-clojure.core
  (:use [fui.core])
  (:use [clojure.math.numeric-tower])
  (:use [lamina.core])
  (:use [clj-time.core]) )

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

(defn centered-circle-bounds [x y diameter]
  [(- x (/ diameter 2)) (- y (/ diameter 2)) diameter diameter])

(defn ball-bounds [game-state]
  (let [ball-state (game-state :ball)]
    (centered-circle-bounds (ball-state :x) (ball-state :y) 25)))

; Pong game

(defn inset [d bounds]
  [(+ (bounds 0) d) (+ (bounds 1) d) (- (bounds 2) d d) (- (bounds 3) d d)])

(defmacro fill-rect [bounds color]
  {:shape :rect, :bounds bounds, :color color})

(defmacro fill-oval [bounds color]
  {:shape :oval, :bounds bounds, :color color})

(defn draw-pong-game [state]
  (let [grass-color (color 87 153 88)
        ball-color (color 13 75 145)
        lines-color (color 255 255 245)
        game-bounds [0 0 (state :width) (state :height)]
        ]
    [(fill-rect game-bounds grass-color)
     (fill-rect (inset 20 game-bounds) lines-color)
     (fill-rect (inset 25 game-bounds) grass-color)
     (fill-oval (ball-bounds state) ball-color)
     ]))

(defn pong-game [w h]
  (let [game-state {:width w, :height h, :ball (new-ball w h 5)}
        time-signal (periodically 30 now)
        game-state-signal (reductions* update-ball game-state time-signal)
        graphics-signal (map* draw-pong-game game-state-signal)
        ]
    (window (component w h graphics-signal))))
