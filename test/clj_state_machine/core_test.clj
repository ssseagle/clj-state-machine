(ns clj-state-machine.core-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clj-state-machine.core :as fsm]))

(def traffic-light-spec
  {:initial :red
   :terminal #{:out-of-order}
   :transitions
   {:red    {:next :green :break :out-of-order}
    :green  {:next :yellow :break :out-of-order}
    :yellow {:next :red :break :out-of-order}}})

(deftest test-fsm-lifecycle
  (testing "Initialization"
    (let [m (fsm/create-machine traffic-light-spec)]
      (is (= :red (:current-state m)))
      (is (fsm/can-transition? m :next))
      (is (not (fsm/can-transition? m :invalid)))))

  (testing "Sequential transitions"
    (let [m0 (fsm/create-machine traffic-light-spec)
          m1 (fsm/transition m0 :next)
          m2 (fsm/transition m1 :next)
          m3 (fsm/transition m2 :next)]
      (is (= :green (:current-state m1)))
      (is (= :yellow (:current-state m2)))
      (is (= :red (:current-state m3)))
      (is (= 3 (count (fsm/history m3))))))

  (testing "Terminal state prohibition"
    (let [m0 (fsm/create-machine traffic-light-spec)
          m1 (fsm/transition m0 :break)]
      (is (= :out-of-order (:current-state m1)))
      (is (thrown? Exception (fsm/transition m1 :next))))))

(defn -main [& _args]
  (let [res (run-tests 'clj-state-machine.core-test)]
    (when (or (pos? (:fail res)) (pos? (:error res)))
      (System/exit 1))))
