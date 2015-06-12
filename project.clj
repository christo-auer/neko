(defproject neko "4.0.0-SNAPSHOT"
  :description "Neko is a toolkit designed to make Android development using Clojure easier and more fun."
  :url "https://github.com/clojure-android/neko"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure-android/clojure "1.7.0-beta3-r2"]]
  :source-paths ["src" "src/clojure"]
  :java-source-paths ["src/java"]

  :plugins [[lein-droid "0.4.0-alpha3"]]

  :profiles {:default [:android-common]

             :local-testing
             [:android-common
              {:target-path "target/local-testing"
               :dependencies [[junit/junit "4.12"]
                              [org.robolectric/robolectric "3.0-SNAPSHOT"]
                              [org.clojure-android/droid-test "0.1.0"]
                              [venantius/ultra "0.3.3"]]
               :android {:aot [#"neko.t-.+" #"neko.data.t-.+" #"neko.listeners.t-.+"
                               "ultra.test"]
                         :build-type :release}}]

             :travis
             [:local-testing
              {:android {:sdk-path "/usr/local/android-sdk/"}}]}


  :android {:library true
            :target-version 18})
