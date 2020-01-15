(ns showtime
  (:use [runtime :only (done shader shaderGen t mag-linear out)]))

(def modulate (shader "modulate" {}))
(def blend-normal (shader "blend.normal" {}))
(def blend-add (shader "blend.add" {}))
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
(def genBlocks (shaderGen "gen.blocks" {}))
(def lightGraffiti (shader "lightGraffiti"
                           {:highlightColor [0 1 1]
                            :decay 0.003
                            :threshold 1.2}))
(def galtan (shader "galtan" {}))
(def unsharp (shader "unsharp" {}))
(def default (shader "default" {}))
(def invert (shader "invert" {}))
(def multiply (shader "multiply" {}))
(def blend-multiply (shader "blend.multiply" {}))
(def shiftHue (shader "shift.hue" {:amount 0.5}))
(def posterize (shader "posterize" {:bins 5, :gamma 1.0}))
(def distort (shader "distort.vhs" {:bandSize 250}))
(def shimmer (shader "shimmer" {:bandSize 250}))
(def slats (shader "slats" {:slats 20, :minSize 0.05}))
(def rgb->cmyk (shader "convert.rgb2cmy"))
(def cmyk->rgb (shader "convert.cmyk2rgb"))

(defn video [path] {:source :ffvideo, :path path, :name path})
(defn fft [[args]]
  (def props (merge {:scale 128} args))
  {:source :fft, :name "fft", :scale (:scale props)})

(->
 [ (genBlocks {:color [1 0 0] :rseed 1}) 
   (genBlocks {:color [0 1 0] :rseed 2}) 
   (genBlocks {:color [0 0 1] :rseed 3}) ]
  (blend-add)
  sobel
  (cmyk->rgb)
  distort
  kaleid
 out)



