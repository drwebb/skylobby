(ns repl
  (:require
    [cider.nrepl]
    [clojure.string :as string]
    hashp.core
    [io.aviso.repl]
    [nrepl.cmdline]
    [reply.main]
    [taoensso.timbre :as timbre]
    [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
    user))


(def ^:private log-file "repl.log")

(defn log-to-file []
  (println "Setting up log to" log-file)
  (timbre/merge-config!
    {:appenders
     {:println {:enabled? false}
      :rotor (rotor/rotor-appender
               {:path log-file
                :max-size 100000000
                :backlog 1})}}))


(def middleware
  [;'cider.nrepl/cider-middleware
   'cider.nrepl/wrap-clojuredocs
   'cider.nrepl/wrap-classpath
   'cider.nrepl/wrap-complete
   'cider.nrepl/wrap-debug
   'cider.nrepl/wrap-format
   'cider.nrepl/wrap-info
   'cider.nrepl/wrap-inspect
   'cider.nrepl/wrap-macroexpand
   'cider.nrepl/wrap-ns
   'cider.nrepl/wrap-spec
   'cider.nrepl/wrap-profile
   'cider.nrepl/wrap-resource
   ;'cider.nrepl/wrap-refresh TODO re-render
   'cider.nrepl/wrap-stacktrace
   'cider.nrepl/wrap-test
   'cider.nrepl/wrap-trace
   'cider.nrepl/wrap-out
   'cider.nrepl/wrap-undef
   'cider.nrepl/wrap-version])

(defn -main [& _args]
  (log-to-file)
  (future
    (nrepl.cmdline/-main
      "--middleware"
      (str "[" (string/join "," middleware) "]")
      ;"--interactive"
      "--color"))
  (user/init)
  (loop []
    (when
      (try
        (println "Sleeping and trying REPLy")
        (Thread/sleep 500)
        (reply.main/launch-nrepl
          {:attach (slurp ".nrepl-port")
           :caught io.aviso.repl/pretty-pst
           :color true})
        false
        (catch Exception _e
          (println "Error connecting REPLy")
          true))
      (recur)))
  (System/exit 0))
