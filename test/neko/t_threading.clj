(ns neko.t-threading
  (:require [clojure.test :refer :all]
            [neko.threading :as t])
  (:import android.view.View
           org.robolectric.RuntimeEnvironment
           neko.App))

(deftest ui-thread
  (is (t/on-ui-thread?))

  ;; Should execute immediately.
  (let [thread (Thread/currentThread)]
    (t/on-ui
      (is (= thread (Thread/currentThread)))))

  (future
    (is (not (t/on-ui-thread?)))
    (t/on-ui
      (is (t/on-ui-thread?))))

  (future
    (t/on-ui*
     (fn [] (is (t/on-ui-thread?))))))

(deftest post
  (let [pr (promise)]
    (future
      (t/post (View. RuntimeEnvironment/application)
              (is (t/on-ui-thread?))
              (deliver pr :success)))
    (future
      (is (= :success (deref pr 10000 :fail)))))

  (let [pr (promise)]
    (future
      (t/post-delayed (View. RuntimeEnvironment/application) 1000
                      (is (t/on-ui-thread?))
                      (deliver pr :success)))
    (future
      (is (= :success (deref pr 10000 :fail))))))


