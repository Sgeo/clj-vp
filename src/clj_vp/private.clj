(ns clj-vp.private
  (:use [clj-vp.rc :only [throw-rc]]))

(def +INSTANCE+ vp.VPLibrary/INSTANCE)

(defmacro call-vp
  "Calls a method on +INSTANCE+ and wraps the call in throw-rc"
  [method & args]
  `(throw-rc (~method +INSTANCE+ ~@args)))

(defmacro call-vp-b
  "Anaphoric macro that calls a method on +INSTANCE+ and whatever (:sdk bot) is in scope, and wraps it in throw-rc"
  [method & args]
  `(call-vp ~method (:sdk ~'bot) ~@args))

(defmacro locking-b
  "Anaphoric macro that locks on whatever bot is in scope."
  [& body]
  `(locking (:lock ~'bot)
     ~@body))