(ns runtime
  (:require [clojure.string :as string]
            [lumo.core]))

(.log js/console "#Started")

(defn _t [] (/ (.getTime (js/Date.)) 1000))
(def t (_t))
(js/setInterval #(def t (_t)) 1)

(def node-osc (js/require "node-osc"))
(def Client (.-Client node-osc))
(def Message (.-Message node-osc))
(def client (Client. "127.0.0.1" 4200))

(defn send [msgs]
  (when (seq msgs)
    (.log js/console (clj->js (first msgs)))
    (defn callback [] (js/setTimeout #(send (rest msgs)) 0.05))
    (.send client (clj->js (first msgs)) callback)))

(defn done [] (js/setTimeout #(.close client) 2))

(defn osc [endpoint & data] {:address endpoint, :args (flatten data)})

(defn shader->osc [data]
  [(map hash->osc (:inputs data))
   (osc "/program/watch" (:program data) (:vert data) (:geom data) (:frag data))
   (osc "/source.shader/create" (:name data))
   (osc "/source.shader/set/program" (:name data) (:program data))
   (osc "/source/set/magfilter" (:name data) (:mag data))
   (map-indexed #(osc "/source.shader/set/input" (:name data) %1 (:name %2)) (:inputs data))
   (map #(uniform->osc (:name data) %1 (get (:uniforms data) %1)) (keys (:uniforms data)))])

(defn ffvideo->osc [data & timescale]
  [(osc "/source.ffvideo/create" (:name data) (:path data))
   (osc "/source.ffvideo/set/timescale" (:name data) (if (nil? timescale) 1 timescale))])

(defn fft->osc [data] [(osc "/source.fft/create" (:name data))
                       (osc "/source.fft/scale" (:name data) (:scale data))])

(defn hash->osc [data]
  (flatten
   (case (:source data)
     :shader  (shader->osc data)
     :ffvideo (ffvideo->osc data)
     :fft     (fft->osc data)
     [])))

(defn uniform->osc [shader name value]
  (if (fn? value)
    (uniformFn->osc shader name value)
    (uniformVal->osc shader name value)))

(defn uniformVal->osc [shader name value]
  (osc "/source.shader/set/uniform1f" shader name value))

(defn uniformFn->osc [shader name value]
  (let [exp #(uniformVal->osc shader name (value))]
    (js/setInterval #(send [(exp)]) 17)
    (exp)))

(def nameUniq 0)

(defn shaderGen [n uniforms]
  (fn  [& [args]]
    (def nameUniq (+ 1 nameUniq))
    (let [thisName n]
      {:source :shader
       :program n
       :vert "shaders/vert/default.glsl"
       :geom "shaders/geom/default.glsl"
       :frag (str "shaders/frag/" n ".glsl")
       :mag "NEAREST"
       :name (str thisName "[" nameUniq "]")
       :inputs []
       :uniforms (merge uniforms args)})))

(defn shader [n uniforms]
  (fn [s & [args]]
    (let [inputs     (if (nil? s) [] (flatten [s]))
          inputNames (map #(:name %1) inputs)
          thisName   (case (count inputs)
                       0 n
                       1 (str (first inputNames) "->" n)
                       (str "(" (clojure.string/join "," inputNames) ")->" n))
          nameUniq   (+ 1 nameUniq)]
      {:source :shader
       :program n
       :vert "shaders/vert/default.glsl"
       :geom "shaders/geom/default.glsl"
       :frag (str "shaders/frag/" n ".glsl")
       :mag "NEAREST"
       :name (str thisName "[" nameUniq "]")
       :inputs inputs
       :uniforms (merge uniforms args)})))

(defn mag-linear [input]
  (merge input {:mag "LINEAR"}))

(defn out [data]
  (let [messages (hash->osc data)
        suffix   (osc "/source.shader/set/input" "window" 0 (:name data))]
    (send (flatten [messages suffix]))))
