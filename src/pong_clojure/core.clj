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
  (agent {:x (/ w 2), :y (/ h 2),
          :vx speed, :vy speed})
  )

(defn adjust-position [state]
  (assoc state
    :x (+ (:x state) (:vx state))
    :y (+ (:y state) (:vy state)))
  )

(defn adjust-velocity [state h]
  (assoc state
    :vy
    (if (>= (:y state) h) (- (abs (:vy state))) (:vy state))
    )
  )

(defn update-ball [state h]
  (-> state (adjust-velocity h) adjust-position)
  )

(defn centered-circle-bounds [x y diameter]
  [(- x (/ diameter 2)) (- y (/ diameter 2)) diameter diameter])

(defn ball-bounds [ballState]
  (centered-circle-bounds (ballState :x) (ballState :y) 25))

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

(defn component [width height draw-commands]
  (proxy [JComponent] []
    (getPreferredSize[] (new Dimension width height))
    (paintComponent [g]
      (let [commands (draw-commands)]
        (doseq [command commands]
          (draw command g))
          )
      )))

; Pong game

(defn pongCanvas [width height ball]
  (component width height #(vector
    {:shape :rect, :bounds [0 0 width height], :color (grassColor)}
    {:shape :oval, :bounds (ball-bounds @ball), :color (ballColor)}
    )))

(defn pongGame [w h]
  (let [ball (new-ball w h 5)
        canvas (pongCanvas w h ball)
        timer (new Timer 30 nil)]

    (with-action timer
      (send ball update-ball h)
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
