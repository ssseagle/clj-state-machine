(ns clj-state-machine.core
  (:require [clj-state-machine.schema :as schema]
            [clj-state-machine.journal :as journal]))

(defrecord StateMachine [spec current-state context journal])

(defn create-machine
  "Creates a new state machine instance initialized to the spec's initial state."
  ([spec]
   (create-machine spec {}))
  ([spec initial-context]
   (schema/validate-spec! spec)
   (map->StateMachine
    {:spec spec
     :current-state (:initial spec)
     :context initial-context
     :journal []})))

(defn can-transition?
  "Returns true if the machine can accept the given event in its current state."
  [machine event]
  (let [{:keys [spec current-state]} machine
        state-transitions (get-in spec [:transitions current-state])]
    (contains? state-transitions event)))

(defn allowed-events
  "Returns a set of events accepted by the current state."
  [machine]
  (set (keys (get-in (:spec machine) [:transitions (:current-state machine)]))))

(defn transition
  "Applies an event to the state machine, returning a new machine instance.
   Throws an ex-info if the transition is invalid or machine is in a terminal state."
  ([machine event]
   (transition machine event nil))
  ([machine event payload]
   (let [{:keys [spec current-state context journal]} machine]
     (when (schema/terminal-state? spec current-state)
       (throw (ex-info "Cannot transition from terminal state"
                       {:current-state current-state :event event})))
     (let [target-state (get-in spec [:transitions current-state event])]
       (when-not target-state
         (throw (ex-info "Invalid transition for current state"
                         {:current-state current-state
                          :event event
                          :allowed-events (allowed-events machine)})))
       (let [entry (journal/make-journal-entry current-state target-state event payload)
             new-context (if (map? payload) (merge context payload) context)]
         (assoc machine
                :current-state target-state
                :context new-context
                :journal (journal/append-entry journal entry)))))))

(defn history
  "Returns the journal of executed transitions."
  [machine]
  (:journal machine))

(defn replay
  "Replays a sequence of journal entries against a fresh machine spec."
  [spec initial-context entries]
  (reduce (fn [m {:keys [event payload]}]
            (transition m event payload))
          (create-machine spec initial-context)
          entries))
