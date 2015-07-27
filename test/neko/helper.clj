(ns neko.helper
  (:require [clojure.test :as test]))

(defmacro deftest
  "Same as clojure.test/deftest, but also ensures that all assertions were
  checked."
  [name & body]
  (let [hits (gensym "hits")
        hits2 (atom 0)]
    `(let [~hits (atom 0)]
       (test/deftest ~name
         ~@(clojure.walk/postwalk (fn [form]
                                    (if (and (sequential? form)
                                             (= (first form) 'is))
                                      (do (swap! hits2 inc)
                                          `(do ~form
                                               (swap! ~hits inc)))
                                      form)) body)
         (test/is (= ~(deref hits2) (deref ~hits))
                  "Not all assertions were checked.")))))
