(ns clj-state-machine.visualizer
  (:require [clojure.string :as str]))

(defn to-mermaid
  "Converts an FSM specification into a Mermaid stateDiagram string."
  [spec]
  (let [{:keys [initial terminal transitions]} spec
        lines (atom ["stateDiagram-v2"
                     (str "    [*] --> " (name initial))])]
    (doseq [[from-state event-map] transitions]
      (doseq [[event to-state] event-map]
        (swap! lines conj
               (str "    " (name from-state) " --> " (name to-state)
                    " : " (name event)))))
    (doseq [t terminal]
      (swap! lines conj (str "    " (name t) " --> [*]")))
    (str/join "\n" @lines)))
