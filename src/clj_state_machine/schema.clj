(ns clj-state-machine.schema)

(defn validate-spec!
  "Validates that the provided FSM specification map has required keys."
  [spec]
  (when-not (map? spec)
    (throw (ex-info "FSM specification must be a map" {:spec spec})))
  (when-not (contains? spec :initial)
    (throw (ex-info "FSM specification missing :initial state" {:spec spec})))
  (when-not (contains? spec :transitions)
    (throw (ex-info "FSM specification missing :transitions map" {:spec spec})))
  true)

(defn terminal-state?
  "Checks if the given state is in the terminal states set."
  [spec state]
  (let [terminals (get spec :terminal #{})]
    (contains? terminals state)))
