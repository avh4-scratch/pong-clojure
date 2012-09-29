(ns pong-clojure.test.core
  (:use [pong-clojure.core])
  (:use [clojure.test]))

(deftest test-adjust-velocity-should-keep-vy-when-in-bounds
  (is (= 5 (:vy (adjust-velocity {:vy 5, :y 50} 100)))))

(deftest test-adjust-velocity-should-stay-negative-vy-when-out-of-y-bounds
  (is (= -5 (:vy (adjust-velocity {:vy -5, :y 100} 100)))))

(deftest test-adjust-velocity-should-become-negative-vy-when-out-of-y-bounds
  (is (= -5 (:vy (adjust-velocity {:vy 5, :y 100} 100)))))

(deftest test-adjust-velocity-should-keep-magnitude
  (is (= 3 (:vy (adjust-velocity {:vy 3, :y 50} 100)))))

(deftest test-adjust-velocity-should-keep-magnitude-when-changing-sign
  (is (= -3 (:vy (adjust-velocity {:vy 3, :y 100} 100)))))