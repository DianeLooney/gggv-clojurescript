(ns runtime
  (:require [clojure.string :as string]
            [lumo.core]))

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
   (map-indexed #(osc "/source.shader/set/input" (:name data) %1 (:name %2)) (:inputs data))
   (map #(osc "/source.shader/set/uniform1f" (:name data) %1 (get (:uniforms data) %1)) (keys (:uniforms data)))])
(defn ffvideo->osc [data] [(osc "/source.ffvideo/create" (:name data) (:path data))])
(defn fft->osc [data] [
  (osc "/source.fft/create" (:name data))
  (osc "/source.fft/scale" (:name data) (:scale data))])

(defn hash->osc [data]
  (flatten
   (case (:source data)
     :shader  (shader->osc data)
     :ffvideo (ffvideo->osc data)
     :fft     (fft->osc data)
     [])))

(defn shader [n uniforms]
  (fn [s & [args]]
    (def inputs (if (nil? s) [] (flatten [s])))
    (def inputNames (map #(:name %1) inputs))
    (def thisName
      (case (count inputs)
        0 n
        1 (str (first inputNames) "->" n)
        (str "(" (clojure.string/join "," inputNames) ")->" n)))
    {:source :shader
     :program n
     :vert "shaders/vert/default.glsl"
     :geom "shaders/geom/default.glsl"
     :frag (str "shaders/frag/" n ".glsl")
     :name thisName
     :inputs inputs
     :uniforms (merge uniforms args)}))

(defn out [data]
  (def messages (hash->osc data))
  (def suffix (osc "/source.shader/set/input" "window" 0 (:name data)))
  (send (flatten [messages suffix])))
