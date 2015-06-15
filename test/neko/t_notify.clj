(ns neko.t-notify
  (:require [clojure.test :refer :all]
            [neko.context :as ctx]
            [neko.notify :as notify]
            [neko.-utils :as u])
  (:import android.app.Activity
           [org.robolectric RuntimeEnvironment Shadows]
           org.robolectric.shadows.ShadowToast
           neko.App))

(set! App/instance RuntimeEnvironment/application)

(deftest disguised-toast
  (is (= 0 (ShadowToast/shownToastCount)))
  (notify/toast RuntimeEnvironment/application "Disguised toast" :short)
  (notify/toast RuntimeEnvironment/application "Disguised toast" :long)
  (notify/toast RuntimeEnvironment/application "Disguised toast" :long)
  (notify/toast (Activity.) "Disguised toast" :long)
  (is (= 4 (ShadowToast/shownToastCount))))

(deftest notifications
  (let [nm (ctx/get-service :notification)
        n (notify/notification {:content-title "Title"
                                :content-text "Text"
                                :action [:activity "foo.bar.MAIN"]})]
    (notify/fire ::test n)
    (is (= n (.getNotification (Shadows/shadowOf nm) nil (u/int-id ::test))))
    (notify/cancel ::test)
    (is (= nil (.getNotification (Shadows/shadowOf nm) nil (u/int-id ::test))))))
