(import '(javax.swing JFrame SwingUtilities WindowConstants))

(defn gameWindow []
  (doto (JFrame.)
    (.setSize 800 600)
    (.setLocationRelativeTo nil)
    (.setDefaultCloseOperation WindowConstants/EXIT_ON_CLOSE)
    (.setVisible true)
    ))

(gameWindow)