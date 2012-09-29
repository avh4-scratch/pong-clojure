(ns pong-clojure.main
  (:use [pong-clojure.core]))
(import '(javax.swing SwingUtilities))

(SwingUtilities/invokeLater #(pongGame 800 600))