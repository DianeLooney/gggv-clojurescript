(ns showtime
  (:use [runtime :only (done shader out)]))

(def blend (shader "blend.normal" {}))
(def bits (shader "3bit" {}))
(def c64 (shader "8bit" {
  :color0 [0 0 0]
  :color1 [0 0 0]
  :color2 [0 0 0]
  :color3 [0 0 0]
  :color4 [0 0 0]
  :color5 [0 0 0]
  :color6 [0 0 0]
  :color7 [0 0 0]
  :color8 [0 0 0]
  :color9 [0 0 0]
  :colorA [0 0 0]
  :colorB [0 0 0]
  :colorC [0 0 0]
  :colorD [0 0 0]
  :colorE [0 0 0]
  :colorF [0 0 0]
}))
(def haze (shader "haze" {}))
(def threshold (shader "threshold" {:threshold 0.05}))
(def grayscale (shader "grayscale" {}))
(def edges (shader "detectEdges" {:threshold 0.2}))
(def ideal (shader "ideal" {}))
(def sobel (shader "sobel" {}))
(def unsharp (shader "unsharp" {}))
(def default (shader "default" {}))
(def invert (shader "invert" {}))
(def multiply (shader "multiply" {}))
(def blendNormal (shader "blend.normal" {}))
(def shiftHue (shader "shift.hue" {:amount 0.5}))
(def posterize (shader "posterize" {:bins 5, :gamma 1.0}))
(def distort (shader "distort.vhs" {:bandSize 250}))

(defn video [path] {:source :ffvideo, :path path, :name path})
(defn fft [[args]] 
  (def props (merge {:scale 128} args))
  {:source :fft, :name "fft", :scale (:scale props)})

(def vid (-> (video "sample1.mp4") shiftHue))

(->
  [(video "sample1.mp4") (-> (default) invert)]
  blendNormal
  out
)
