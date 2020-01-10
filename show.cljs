(ns showtime
  (:use [runtime :only (done shader t mag-linear out)]))

(def modulate (shader "modulate" {}))
(def blend-normal (shader "blend.normal" {}))
(def bits (shader "3bit" {}))
(def bit8 (shader "8bit"
                  {:color0 [0 0 0]
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
                   :colorF [0 0 0]}))
(def band (shader "band" {}))
(def haze (shader "haze" {}))
(def threshold (shader "threshold" {:threshold 0.05}))
(def bThreshold (shader "blend.threshold" {:threshold 0.05}))
(def grayscale (shader "grayscale" {}))
(def dots (shader "dots" {}))
(def edges (shader "detectEdges" {:threshold 0.2}))
(def expandSrc (shader "gen.expand" {:frequency 3, :lineThickness 0.01, :spread 0.05}))
(defn expand [src]
  (-> [src (expandSrc)] modulate))
(def edgeFracking (shader "edgeFracking" {}))
(def sobel (shader "filt.sobel" {}))
(def kaleid (shader "kaleidoscope" {}))
(def julia (shader "gen.julia" {}))
(def lightGraffiti (shader "lightGraffiti"
                           {:highlightColor [0 1 1]
                            :decay 0.003
                            :threshold 1.2}))
(def galtan (shader "galtan" {}))
(def unsharp (shader "unsharp" {}))
(def default (shader "default" {}))
(def invert (shader "invert" {}))
(def multiply (shader "multiply" {}))
(def blendNormal (shader "blend.normal" {}))
(def shiftHue (shader "shift.hue" {:amount 0.5}))
(def posterize (shader "posterize" {:bins 5, :gamma 1.0}))
(def distort (shader "distort.vhs" {:bandSize 250}))
(def shimmer (shader "shimmer" {:bandSize 250}))
(def slats (shader "slats" {:slats 20, :minSize 0.05}))

(defn video [path] {:source :ffvideo, :path path, :name path})
(defn fft [[args]]
  (def props (merge {:scale 128} args))
  {:source :fft, :name "fft", :scale (:scale props)})

(->
 (video "sample1.mp4")
 (slats {})
 (expand {:decay [0 0 0]})
 sobel
 out)

