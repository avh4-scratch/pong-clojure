(ns fui.drawing)

(defmacro fill-rect [bounds color]
  {:shape :rect, :bounds bounds, :color color})

(defmacro fill-oval [bounds color]
  {:shape :oval, :bounds bounds, :color color})
