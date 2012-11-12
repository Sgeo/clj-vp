(ns clj-vp.core
  (:use  [clj-vp.rc :only [throw-rc]]))

(def ^:private +INSTANCE+ vp.VPLibrary/INSTANCE)

(defmacro call-vp
  "Calls a method on +INSTANCE and wraps the call in throw-rc"
  [& [method & args]]
  `(throw-rc (~method +INSTANCE+ ~@args)))

(defn init
  "Call before any other VP functions"
  []
  (call-vp .vp_init vp.VPLibrary/VPSDK_VERSION))


(defn create
  "Create a VP bot"
  []
  (.vp_create +INSTANCE+))

(defn destroy
  "Destroy a VP bot"
  [bot]
  (call-vp .vp_destroy bot))

(defn connect-universe
  "Connects to the given universe.
Defaults to Edwin's universe."
  ([bot]
     (connect-universe bot "universe.virtualparadise.org"))
  ([bot host]
     (connect-universe bot host 57000))
  ([bot host port]
     (call-vp .vp_connect_universe bot host port)))

(defn login
  "Logs into the universe"
  [bot & {:keys [user pass name]}]
  (call-vp .vp_login bot user pass name))

(defn enter
  "Enter a world"
  [bot world]
  (call-vp .vp_enter bot world))

(defn leave
  "Leave the current world"
  [bot]
  (call-vp .vp_leave bot))

(defn say
  "Send a message to everyone in the current world"
  [bot msg]
  (call-vp .vp_say bot msg))