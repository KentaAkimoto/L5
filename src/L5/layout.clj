(ns L5.layout
  (:use [clojure.contrib.string :only [split trim blank?]]
        L5)
  (:require [clojure.contrib.string :as str]
            [L5.slide :as slide])
  (:import [java.io File]
           [javax.imageio ImageIO]))

(defn normalize-strings [& strs]
  (remove blank?
          (flatten
           (map #(split #"[ \t]*\n[ \t]*" (trim %1)) strs))))

(defmacro with [params & body]
  `(binding [L5/*context* (ref (merge (context) ~params))]
     ~@(map (fn [e] `(doelem ~e)) body)))

(defmacro with-size [size & body]
  `(with {:font-size ~size} ~@body))

(defmacro with-padding [padding & body]
  `(with {:padding ~padding} ~@body))

(defn img [filename]
  {:body (ImageIO/read
          (File. (str (.getParent (File. (or @*run-file* *file*))) "/" filename)))})

(defmacro title [& strs]
  `{:attr {:font-size (* 1.3 (:font-size (context)))
           :text-align :center
           :padding {:top 30}}
    :body [~@strs]})

(defmacro lines [& strs]
  (let [line-height (/ (:font-size (context)) 3)]
    `{:attr {:padding {:top (+ (-> (context) :padding :top) ~line-height)
                       :bottom ~line-height}}
      :body [~@strs]}))

(defmacro item [& strs]
  `(lines ~@(map #(str "・" %) strs)))

(defmacro enum [& strs]
  `(lines
    ~@(map #(str %1 ". " %2) (range 1 (+ 1 (count strs))) strs)))

(defmacro t [& strs]
  `{:body [~@strs]
    :attr {:font-size nil
           :text-align :center
           :position :fixed
           :padding {:top 100 :right 100 :bottom 100 :left 100}}})
