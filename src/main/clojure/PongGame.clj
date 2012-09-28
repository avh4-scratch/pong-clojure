(import '(javax.swing JFrame JComponent SwingUtilities WindowConstants))
(import '(java.awt Dimension Graphics Color))

(defn grassColor []
  (new Color 87 153 88))

(defn pongCanvas [w h]
  (proxy [JComponent] []
    (getPreferredSize [] (new Dimension w h))
    (paintComponent [g]
      (doto g
        (.setColor (grassColor))
        (.fillRect 0 0 w h)
        ))
    ))

(defn pongGame [w h]
  (doto (new JFrame)
    (.add (pongCanvas w h))
    (.pack)
    (.setLocationRelativeTo nil)
    (.setDefaultCloseOperation WindowConstants/EXIT_ON_CLOSE)
    (.setVisible true)
    ))

(SwingUtilities/invokeLater #(pongGame 800 600))
