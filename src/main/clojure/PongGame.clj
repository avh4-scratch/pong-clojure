(import '(javax.swing JFrame JComponent SwingUtilities WindowConstants Timer))
(import '(java.awt Dimension Graphics Color))

; Support

(defmacro with-action [component & body]
  `(.addActionListener ~component
     (proxy [java.awt.event.ActionListener] []
       (actionPerformed [~'event] ~@body))))

; Background

(defn grassColor []
  (new Color 87 153 88))

(defn drawBackground [g w h]
  (doto g
    (.setColor (grassColor))
    (.fillRect 0 0 w h)
    ))

; Ball

(defn ballColor []
  (new Color 13 75 145))

(defn new-ball [w h]
  (agent {:x (/ w 2), :y (/ h 2)})
  )

(defn move-ball [state]
  (assoc state :x (+ (:x state) 1)))

(defn centeredCircle [g x y diameter]
  (.fillOval g (- x (/ diameter 2)) (- y (/ diameter 2)) diameter diameter)
  )

(defn drawBall [g ballState]
  (doto g
    (.setColor (ballColor))
    (centeredCircle (:x ballState) (:y ballState) 25)
    ))

; Pong game

(defn pongCanvas [w h ball]
  (proxy [JComponent] []
    (getPreferredSize [] (new Dimension w h))
    (paintComponent [g]
      (doto g
        (drawBackground w h)
        (drawBall @ball)
        ))
    ))

(defn pongGame [w h]
  (let [ball (new-ball w h)
        canvas (pongCanvas w h ball)
        timer (new Timer 100 nil)]

    (with-action timer
      (send ball move-ball)
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

(SwingUtilities/invokeLater #(pongGame 800 600))
