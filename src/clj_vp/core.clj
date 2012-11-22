(ns clj-vp.core
  (:use
   [clj-vp.private :only [+INSTANCE+ call-vp call-vp-b locking-b]]))



(defn init
  "Call before any other VP functions"
  []
  (System/setProperty "jna.encoding" "UTF8")
  (call-vp .vp_init vp.VPLibrary/VPSDK_VERSION))


(defn create
  "Create a VP bot"
  []
  {:sdk (.vp_create +INSTANCE+) ; Can't use call-vp because return value is not an RC
   :lock (Object.)}) 

(defn destroy
  "Destroy a VP bot"
  [bot]
  (locking-b
   (call-vp-b .vp_destroy)))

(defn connect-universe
  "Connects to the given universe.
Defaults to Edwin's universe."
  ([bot]
     (connect-universe bot "universe.virtualparadise.org"))
  ([bot host]
     (connect-universe bot host 57000))
  ([bot host port]
     (locking-b
      (call-vp-b .vp_connect_universe host port))))

(defn login
  "Logs into the universe"
  [bot & {:keys [user pass name]}]
  (locking-b
   (call-vp-b .vp_login user pass name)))

(defn enter
  "Enter a world"
  [bot world]
  (locking-b
   (call-vp-b .vp_enter world)))

(defn leave
  "Leave the current world"
  [bot]
  (locking-b
   (call-vp-b .vp_leave)))

(defn say
  "Send a message to everyone in the current world"
  [bot msg]
  (locking-b
   (call-vp-b .vp_say msg)))