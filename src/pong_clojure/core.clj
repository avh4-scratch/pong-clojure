(ns pong-clojure.core
  (:use [clojure.math.numeric-tower])
  (:use [lamina.core])
  (:use [clj-time.core]) )
(import '(javax.swing JFrame JComponent SwingUtilities WindowConstants Timer))
(import '(java.awt Dimension Graphics Color))

; Support

(defmacro with-action [component & body]
  `(.addActionListener ~component
     (proxy [java.awt.event.ActionListener] []
       (actionPerformed [~'event] ~@body))))

; Definitions

(defn grassColor [] (new Color 87 153 88))
(defn ballColor [] (new Color 13 75 145))

; Game logic

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

(defn game-bounds [game-state]
  [0 0 (game-state :width) (game-state :height)])

; Swing wrapper

(defn fill-rect [g x y w h]
  (.fillRect g x y w h))
(defn fill-oval [g x y w h]
  (.fillOval g x y w h))

(defmulti draw :shape)
(defmethod draw :rect [command g]
  (.setColor g (command :color))
  (apply (partial fill-rect g) (command :bounds)))
(defmethod draw :oval [command g]
  (.setColor g (command :color))
  (apply (partial fill-oval g) (command :bounds)))

(defn component [width height graphics-signal]
  (let [graphics-ref (ref [])
        self (proxy [JComponent] []
          (getPreferredSize[] (new Dimension width height))
          (paintComponent [g]
            (doseq [command @graphics-ref]
              (draw command g))) ) ]

    (receive-all graphics-signal
      (fn [x] (do
        (dosync (ref-set graphics-ref x))
        (SwingUtilities/invokeLater #(.repaint self)))))

    self))

; Pong game

(defn draw-pong-game [state]
  [{:shape :rect, :bounds (game-bounds state), :color (grassColor)}
   {:shape :oval, :bounds (ball-bounds state), :color (ballColor)}
   ])

(defn pongGame [w h]
  (let [game-state {:width w, :height h, :ball (new-ball w h 5)}
        time-signal (periodically 30 now)
        game-state-signal (reductions* update-ball game-state time-signal)
        graphics-signal (map* draw-pong-game game-state-signal)
        ]

    (doto (new JFrame)
      (.add (component w h graphics-signal))
      (.pack)
      (.setLocationRelativeTo nil)
      (.setDefaultCloseOperation WindowConstants/EXIT_ON_CLOSE)
      (.setVisible true)
      )
    ))
