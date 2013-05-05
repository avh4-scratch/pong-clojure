(ns pong-clojure.core)
(use 'clojure.math.numeric-tower)
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

(defn adjust-velocity [sprite h]
  (assoc sprite
    :vy (if (>= (:y sprite) h) (- (abs (:vy sprite))) (:vy sprite)) ))

(defn update-ball [state]
  (assoc state :ball
    (-> (state :ball) (adjust-velocity (state :height)) adjust-position)))

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

(defn component [width height draw-commands state-agent]
  (proxy [JComponent] []
    (getPreferredSize[] (new Dimension width height))
    (paintComponent [g]
      (let [commands (draw-commands @state-agent)]
        (doseq [command commands]
          (draw command g))
          )
      )))

; Pong game

(defn draw-pong-game [state]
  [{:shape :rect, :bounds (game-bounds state), :color (grassColor)}
   {:shape :oval, :bounds (ball-bounds state), :color (ballColor)}
   ])

(defn pongCanvas [width height game-state-agent]
  (component width height draw-pong-game game-state-agent))

(defn update-game [state-agent]
  (send state-agent update-ball))

(defn pongGame [w h]
  (let [game-state {:width w, :height h, :ball (new-ball w h 5)}
        game-state-agent (agent game-state)
        canvas (pongCanvas w h game-state-agent)
        timer (new Timer 30 nil)]

    (with-action timer
      (update-game game-state-agent)
      (SwingUtilities/invokeLater #(.repaint canvas))
      )

    (doto (new JFrame)
      (.add canvas)
      (.pack)
      (.setLocationRelativeTo nil)
      (.setDefaultCloseOperation WindowConstants/EXIT_ON_CLOSE)
      (.setVisible true)
      )

    (.start timer)
    ))
