(ns fui.geometry)

(defn from-center
  ([x y w h] [(- x (/ w 2)) (- y (/ h 2)) w h])
  ([x y size] (from-center x y size size)) )

(defn inset [inset [x y w h]]
  [(+ x inset) (+ y inset) (- w inset inset) (- h inset inset)])
