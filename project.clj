(defproject textimg "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/jiro4989/textimg-clj"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :repl-options {:init-ns textimg.core}
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
  :main textimg.core
  :target-path "target/%s"
  :native-image {:graal-bin :env/GRAALVM_HOME
                 :opts ["--verbose"
                        "--report-unsupported-elements-at-runtime"
                        "--initialize-at-build-time"]
                 :name "textimg"}
  :profiles {:dev {:global-vars {*warn-on-reflection* true
                                 *assert* true}}
             :uberjar {:aot :all
                       :native-image {:jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                                      :graal-bin :env/GRAALVM_HOME}}}
  :plugins [[lein-kibit "0.1.7"]
            [lein-cljfmt "0.6.4"]
            [io.taylorwood/lein-native-image "0.3.1"]])
