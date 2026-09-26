# 🦅 clj-state-machine

A composable, data-driven finite state machine (FSM) engine for Clojure featuring pure transitions, event journaling, guard validations, and snapshot recovery.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Clojure](https://img.shields.io/badge/Clojure-1.11%2B-5881d8.svg)](https://clojure.org)
[![Deps](https://img.shields.io/badge/deps.edn-tools.deps-green.svg)](https://clojure.org/guides/deps_and_cli)

## Highlights

- 🧩 **Pure Functional Design** — All transitions are deterministic, side-effect-free pure functions.
- 📜 **Event Sourcing & Journaling** — Complete append-only audit trail for every state modification.
- 🛡️ **Guards & Predicates** — Conditional transition routing based on payload inspection.
- 🔄 **Replay & Time Travel** — Reconstruct any historic state by replaying journal events.
- 📊 **Zero Boilerplate** — Define FSMs entirely using idiomatic Clojure maps and vectors.

## Installation

Add to your `deps.edn`:

```clojure
io.github.ssseagle/clj-state-machine {:git/tag "v0.1.0" :git/sha "8f2a1b9"}
```

## Quick Start

```clojure
(require '[clj-state-machine.core :as fsm])

;; Define an Order lifecycle state machine
(def order-fsm-spec
  {:initial :pending
   :terminal #{:cancelled :delivered}
   :transitions
   {:pending   {:pay    :paid
                :cancel :cancelled}
    :paid      {:ship   :shipped
                :refund :refunded}
    :shipped   {:deliver :delivered}}})

;; Initialize machine instance
(def order (fsm/create-machine order-fsm-spec {:order-id "ORD-991" :amount 120.0}))

;; Fire transitions
(def paid-order (fsm/transition order :pay {:payment-id "tx_abc123"}))
(:current-state paid-order) ;; => :paid

(def shipped-order (fsm/transition paid-order :ship {:carrier "DHL"}))
(:current-state shipped-order) ;; => :shipped

;; Inspect event history
(fsm/history shipped-order)
;; => [{:event :pay, :from :pending, :to :paid ...}
;;     {:event :ship, :from :paid, :to :shipped ...}]
```

## Running Tests

```bash
clj -M:test -m clj-state-machine.core-test
```

## License

Distributed under the MIT License - Copyright (c) 2024 Sedat Kartal.
