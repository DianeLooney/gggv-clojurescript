(def runtime (js/require "./runtime.js"))

;; Generating gggv things
(def send (.-send runtime))
(defn shader [name & {:keys [p s] :or {:p nil :s []}}]
  (do
    (send "/source.shader/create" name)
    (send "/source.shader/set/program" name p)
    (doall (map-indexed (fn [idx source] (send "/source.shader/set/input" name idx source)) s))
    name))
(defn window [name]
  (do (send "/source.shader/set/input" "window" 0 name)))
(defn video [path]
  (do (send "/source.ffvideo/create" path path) path))

(defn program [name vShaderPath gShaderPath fShaderPath]
  (do (send "/program/watch" name vShaderPath gShaderPath fShaderPath) name))

(defn timescale [name speed] (send "/source.ffvideo/set/timescale" name speed))

(def _v "shaders/vert/default.glsl")
(def _g "shaders/geom/default.glsl")
(def _f "shaders/frag/default.glsl")

(defn mk-shader [path]
  (fn [name] (shader (str name "->" path)
                     :p (program path _v _g (str "shaders/frag/" path ".glsl"))
                     :s [name])))

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

(defn shatter [name1 name2 name3]
  (shader (str name ".shatter")
          :p (program "shatter" "shaders/vert/fx.shatter.glsl"  "shaders/geom/fx.shatter.glsl" "shaders/frag/fx.shatter.glsl")
          :s [name1, name2, name3]))
(defn mask [maskName v1Name v2Name]
  (shader (str maskName ".mask")
          :p (program "mask" _v _g, "shaders/frag/filt.mask.glsl")
          :s [maskName v1Name v2Name]))
(defn julia []
  (shader "julia"
          :p (program "julia" _v _g, "shaders/frag/gen.julia.glsl")
          :s []))

(def v1 (video "sample1.mp4"))

(window (mask
          (edges v1)
          (edges v1)
          (pride v1)))

