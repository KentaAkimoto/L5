(ns L5.export
  (:use [L5.context :only [make-context]]
        [L5.layout])
  (:require [L5.slide :as slide])
  (:import [java.awt Font]
           [java.io FileOutputStream]
           [com.itextpdf.text Document Rectangle]
           [com.itextpdf.text.pdf PdfWriter]))

(defn jframe->pdf [filename context slides]
  (let [width (:width context)
        height (:height context)
        doc (Document. (Rectangle. width (- height 22)))
        writer (PdfWriter/getInstance doc (FileOutputStream. filename))]
    (.open doc)
    (let [cb (.getDirectContent writer)]
      (doseq [slide slides]
        (let [tp (.createTemplate cb width height)
              g2d (.createGraphics tp width height)]
          (.update @(:frame context) g2d)
          (slide)
          (.dispose g2d)
          (.addTemplate cb tp 0 0))
        (.newPage doc)))
    (.close doc)))

(load-file "init.clj")

(defn -main [& [filename]]
  (jframe->pdf (or filename "output.pdf") *context* slides)
  (System/exit 0))