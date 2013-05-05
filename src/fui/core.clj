(ns fui.core
  (:use [lamina.core]))
(import '(javax.swing JFrame JComponent SwingUtilities WindowConstants))
(import '(java.awt Dimension Graphics Color))

(defn color [r g b] (new Color r g b))

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
