(ns clj-state-machine.journal)

(defn make-journal-entry
  "Creates an immutable record for an executed transition."
  [from-state to-state event payload]
  {:timestamp (System/currentTimeMillis)
   :from from-state
   :to to-state
   :event event
   :payload payload})

(defn append-entry
  "Appends an entry to the machine journal."
  [journal entry]
  (conj (or journal []) entry))
