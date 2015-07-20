(defproject neko "4.0.0-SNAPSHOT"
  :description "Neko is a toolkit designed to make Android development using Clojure easier and more fun."
  :url "https://github.com/clojure-android/neko"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repositories [["maven-snapshots" {:id "oss.sonatype"
                                     :url "https://oss.sonatype.org/content/repositories/snapshots"}]]
  :dependencies [[org.clojure-android/clojure "1.7.0-beta3-r2"]
                 [com.android.support/multidex "1.0.0" :extension "aar"]]
  :plugins [[lein-droid "0.4.0-SNAPSHOT"]]

  :source-paths ["src" "src/clojure"]
  :java-source-paths ["src/java"]

  :profiles {:default [:android-common]

             :local-testing
             [:android-common
              {:target-path "target/local-testing"
               :dependencies [[junit/junit "4.12"]
                              [org.robolectric/robolectric "3.0-SNAPSHOT"]
                              [org.clojure-android/droid-test "0.1.1-SNAPSHOT"]
                              [venantius/ultra "0.3.3"]]
               :android {:aot [#"neko.t-.+" #"neko.data.t-.+" #"neko.listeners.t-.+"
                               "ultra.test"]
                         :build-type :release}}]

             :travis
             [:local-testing
              {:dependencies [[cloverage "1.0.6" :exclusions [org.clojure/tools.logging]]
                              [org.clojure-android/tools.logging "0.3.2-r1"]]
               :plugins [[lein-shell "0.4.0"]]
               :aliases {"coverage" ["do" ["droid" "local-test" "cloverage"]
                                     ["shell" "curl" "-F"
                                      "json_file=@target/coverage/coveralls.json"
                                      "https://coveralls.io/api/v1/jobs"]]}
               :android {:sdk-path "/usr/local/android-sdk/"
                         :aot ["cloverage.coverage"]}}]}


  :android {:library true
            :target-version 18})
