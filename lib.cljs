(def runtime (js/require "./runtime.js"))

;; Generating gggv things
(def send (.-send runtime))
(defn create-shader [name]
  (send "/source.shader/create" name))
(defn set-program [name prog]
  (send "/source.shader/set/program" name prog))
(defn set-uniform [shader name value]
  (send "/source.shader/set/uniform1f" shader name value))
(defn set-input [name index source]
  (send "/source.shader/set/input" name index source))
(defn set-window [name]
  (send "/source.shader/set/input" "window" 0 name))
(defn create-video [path]
  (send "/source.ffvideo/create" path path) path)

(defn watch-program [name vShaderPath gShaderPath fShaderPath]
  (send "/program/watch" name vShaderPath gShaderPath fShaderPath) name)

(defn set-timescale [name speed] (send "/source.ffvideo/set/timescale" name speed))

(def _v "shaders/vert/default.glsl")
(def _g "shaders/geom/default.glsl")
(def _f "shaders/frag/default.glsl")
(def w "window")

(defn shader [name & {:keys [p s u] :or {:p nil :s [] :u []}}]
  (do
    (create-shader name)
    (set-program name p)
    (doall (map-indexed (fn [idx source] (set-input name idx source)) s))
    (doall (map (fn [x] (set-uniform name (first x) (second x))) u))
    name))
(defn mk-shader [path]
  (do
    (watch-program path _v _g (str "shaders/frag/" path ".glsl"))
    (fn 
      ([{:keys [u] :or {:u []}}]
        (shader
          (str path)
          :p path
          :u u))
      ([name & {:keys [u] :or {:u []}}]
        (shader
          (str name "->" path)
          :p path
          :s [name]
          :u u)))))
(def rdither (mk-shader "filt.randomDither"))
(def grayscale (mk-shader "filt.grayscale"))
(def brightness (mk-shader "filt.brightness"))
(def edges (mk-shader "filt.edges"))
(def pride (mk-shader "filt.pride"))
(def rorschach (mk-shader "filt.rorschach"))
(def default (mk-shader "default"))
(def kaleidoscope (mk-shader "filt.kaleidoscope"))
(def highlights (mk-shader "filt.highlights"))
(def dots (mk-shader "filt.dots"))
(def haze (mk-shader "filt.haze"))

(defn shatter [name1 name2 name3]
  (shader (str name ".shatter")
          :p (watch-program
               "shatter"
               "shaders/vert/fx.shatter.glsl" 
               "shaders/geom/fx.shatter.glsl"
               "shaders/frag/fx.shatter.glsl")
          :s [name1, name2, name3]))
(defn mask [maskName v1Name v2Name]
  (shader (str maskName ".mask")
          :p (watch-program "mask" _v _g, "shaders/frag/filt.mask.glsl")
          :s [maskName v1Name v2Name]))
(def julia
  (do
    (watch-program "julia" _v _g "shaders/frag/gen.julia.glsl")
    (fn [] (shader "julia" :p "julia"))))

(defn << [base & rest]
  (do
    (reduce (fn [x y] (do (set-input x 0 y) y)) base rest)
    base))

(def j (julia))
(def v1 (create-video "sample1.mp4"))
(def v2 (create-video "muxed.mp4"))
(def h (highlights ""))
(def s1 (haze ""))
(def k (kaleidoscope ""))
(def e (edges ""))
(def m (mask s1 "" v2))
(<< w s1 v1)
(set-uniform k "radius" 540)
(set-uniform h "threshold" 0.5)
(set-uniform h "fadeRate" 0.025)

;; (<< w k s1 h e v1)
