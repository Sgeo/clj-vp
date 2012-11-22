(ns clj-vp.attributes
  (:require [clojure.string :as str])
  (:use [clj-vp.private :only [+INSTANCE+ call-vp call-vp-b locking-b]]
        [clojure.reflect :only [reflect]])
  (:import (com.sun.jna.ptr IntByReference
                            FloatByReference)
           (java.nio ByteBuffer)))


(def ^:private sym-to-key
  (comp keyword #(str/replace % "_" "-") str/lower-case str/join #(drop 3 %) name))

(def ^:private key-to-propname
  (comp name #(str/replace % "-" "_") str/upper-case #(str "VP_" %)))

(defn- prop->propint
  "Given a property as a symbol, e.g. 'VP_MY_X, gets the number representing the thing to pass to the native functions."
  [cls prop]
  (.get (.getField cls (name prop)) nil))

(defn- get-int-prop
  [prop]
  (let
      [propint (prop->propint vp.VPLibrary$VPIntegerProperty prop)]
   (fn [bot]
     (.vp_int +INSTANCE+ (:sdk bot) propint))))

(defn- set-int-prop
  [prop]
  (let
      [propint (prop->propint vp.VPLibrary$VPIntegerProperty prop)]
   (fn [bot val]
     (call-vp-b .vp_int_set propint val))))

(defn- get-float-prop
  [prop]
  (let
      [propint (prop->propint vp.VPLibrary$VPFloatProperty prop)]
    (fn [bot]
      (.vp_float +INSTANCE+ (:sdk bot) propint))))

(defn- set-float-prop
  [prop]
  (let
      [propint (prop->propint vp.VPLibrary$VPFloatProperty prop)]
    (fn [bot val]
      (call-vp-b .vp_float_set propint val))))

(defn- get-string-prop
  [prop]
  (let
      [propint (prop->propint vp.VPLibrary$VPStringProperty prop)]
    (fn [bot]
      (.getString (.vp_string +INSTANCE+ (:sdk bot) propint) 0))))

(defn- set-string-prop
  [prop]
  (let
      [propint (prop->propint vp.VPLibrary$VPStringProperty prop)]
    (fn [bot val]
      (.vp_string_set +INSTANCE+ (:sdk bot) propint val))))

(defn- get-data-prop
  [prop]
  (let
      [propint (prop->propint vp.VPLibrary$VPDataProperty prop)]
    (fn [bot]
      (let
          [length-byref (IntByReference.)
           ptr (.vp_data +INSTANCE+
                         (:sdk bot)
                         propint
                         length-byref)
           result (seq (.getByteArray ptr
                                      0
                                      (.getValue length-byref)))]
        result))))

(defn- set-data-prop
  [prop]
  (let
      [propint (prop->propint vp.VPLibrary$VPDataProperty prop)]
    (fn [bot val]
      (let
          [byte-buffer (ByteBuffer/wrap (byte-array val))]
        (.vp_data_set +INSTANCE+
                      (:sdk bot)
                      propint
                      (count val)
                      byte-buffer)))))





(defn produce-getset-map
  "Returns the final getset map.
Uses reflection."
  []
  (let
      [class->names (fn [cls] (->> cls
                                   reflect
                                   :members
                                   (map :name)
                                   (remove '#{VP_HIGHEST_INT VP_HIGHEST_FLOAT VP_HIGHEST_STRING VP_HIGHEST_DATA})
                                   (filter #(.startsWith (name %) "VP_"))))
       
       int-props (class->names vp.VPLibrary$VPIntegerProperty)
       float-props (class->names vp.VPLibrary$VPFloatProperty)
       string-props (class->names vp.VPLibrary$VPStringProperty)
       data-props (class->names vp.VPLibrary$VPDataProperty)]
    (into {}
          (concat
           (for [prop int-props]
             [(sym-to-key prop)
              {:get (get-int-prop prop)
               :set (set-int-prop prop)}])
           (for [prop float-props]
             [(sym-to-key prop)
              {:get (get-float-prop prop)
               :set (set-float-prop prop)}])
           (for [prop string-props]
             [(sym-to-key prop)
              {:get (get-string-prop prop)
               :set (set-string-prop prop)}])
           #_(for [prop data-props]
             [(sym-to-key prop)
              {:get (get-data-prop prop)
               :set (set-data-prop prop)}])))))

(declare +GETSET+)

(defn get-attrib
  [bot attrib]
  ((get-in +GETSET+ [attrib :get]) bot))

(defn set-attrib
  [bot attrib value]
  ((get-in +GETSET+ [attrib :set]) bot value))

(defn get-attribs-flat
  [bot]
  (into {}
        (for [[k v] +GETSET+]
          [k
           ((:get v) bot)])))

(defn set-attribs-flat
  [bot attribs]
  (doseq [[attrib value] attribs]
    (set-attrib bot attrib value)))

(defn init []
  (System/setProperty "jna.encoding" "UTF8")
  (def +GETSET+ (produce-getset-map))
  nil)

(def +SHAPING+
  {
   :avatar-x [:avatar :position :x]
   :avatar-y [:avatar :position :y]
   :avatar-z [:avatar :position :z]
   :avatar-yaw [:avatar :yaw]
   :avatar-pitch [:avatar :pitch]
   :my-x [:my :position :x]
   :my-y [:my :position :y]
   :my-z [:my :position :z]
   :my-yaw [:my :yaw]
   :my-pitch [:my :pitch]
   :object-x [:object :position :x]
   :object-y [:object :position :y]
   :object-z [:object :position :z]
   :object-rotation-x [:object :rotation :x]
   :object-rotation-y [:object :rotation :y]
   :object-rotation-z [:object :rotation :z]
   :object-rotation-angle [:object :rotation :angle]
   :avatar-session [:avatar :session]
   :avatar-type [:avatar :type]
   :my-type [:my :type]
   :object-id [:object :id]
   :object-type [:object :type]
   :object-time [:object :time]
   :object-user-id [:object :user-id]
   :world-state [:world :state]
   :world-users [:world :users]
   :reference-number [:reference-number]
   :callback [:callback]
   :user-id [:user :user-id]
   :user-registration-time [:user :registration-time]
   :user-online-time [:user :online-time]
   :user-last-login [:user :last-login]
   :friend-id [:friend :id]
   :friend-user-id [:friend :user-id]
   :friend-online [:friend :online]
   :my-user-id [:my :user-id]
   :proxy-type [:proxy :type]
   :proxy-port [:proxy :port]
   :cell-x [:cell :x]
   :cell-z [:cell :z]
   :terrain-tile-x [:terrain :tile :x]
   :terrain-tile-z [:terrain :tile :z]
   :terrain-node-x [:terrain :node :x]
   :terrain-node-z [:terrain :node :z]
   :terrain-node-revision [:terrain :node :revision]
   :avatar-name [:avatar :name]
   :chat-message [:chat :message]
   :object-model [:object :model]
   :object-action [:object :action]
   :object-description [:object :description]
   :world-name [:world :name]
   :user-name [:user :name]
   :user-email [:user :email]
   :world-setting-key [:world :setting :key]
   :world-setting-value [:world :setting :value]
   :friend-name [:friend :name]
   :proxy-host [:proxy :host]})

(defn get-attribs-shaped
  [bot]
  (into {}
        (reduce (fn [acc [attr val]]
                  (assoc-in acc
                            (+SHAPING+ attr)
                            val))
                {}
                (get-attribs-flat bot))))

(defn set-attribs-shaped
  [bot shaped]
  (set-attribs-flat bot
                    (into {}
                          (for
                              [[attr shaping] +SHAPING+
                               :let [val (get-in shaped shaping)]
                               :when val]
                            [attr val]))))