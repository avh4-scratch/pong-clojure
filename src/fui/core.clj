(ns fui.core
  (:use [lamina.core]))
(import '(javax.swing JFrame JComponent SwingUtilities WindowConstants))
(import '(java.awt Dimension Graphics Color))

(defn color [r g b] (new Color r g b))

(defmulti draw :shape)
(defmethod draw :rect [{color :color [x y w h] :bounds} g]
  (.setColor g color)
  (.fillRect g x y w h))
(defmethod draw :oval [{color :color [x y w h] :bounds} g]
  (.setColor g color)
  (.fillOval g x y w h))

(defmacro with-action [component & body]
  `(.addActionListener ~component
     (proxy [java.awt.event.ActionListener] []
       (actionPerformed [~'event] ~@body))))

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

(defn window [component]
  (doto (new JFrame)
    (.add component)
    (.setDefaultCloseOperation WindowConstants/EXIT_ON_CLOSE)))

(defn show [window]
  (SwingUtilities/invokeLater
    #(doto window
      (.pack)
      (.setLocationRelativeTo nil)
      (.setVisible true))))
