
(ns neko.dialog.alert
  "Helps build and manage alert dialogs.  The core functionality of this
  namespace is built around the AlertDialogBuilder protocol.  This allows using
  the protocol with the FunctionalAlertDialogBuilder generated by new-builder
  as well as the AlertDialog.Builder class provided by the Android platform.

  In general, it is preferable to use the functional version of the builder as
  it is immutable.  Using the protocol with an AlertDialog.Builder object works
  by mutating the object."
  (:require [neko.listeners.dialog :as listeners]
            [neko.resource :as res]
            neko.ui
            [neko.find-view]
            [neko.ui.mapping :refer [defelement]]
            [neko.ui.traits :refer [deftrait]]
            [neko.ui :refer [make-ui-element]])
  (:import android.app.AlertDialog android.app.AlertDialog$Builder))

(deftrait :positive-button
  "Takes :positive-text (either string or resource ID)
  and :positive-callback (function of 2 args: dialog and result), and sets it as
  the positive button for the dialog."
  {:attributes [:positive-text :positive-callback]}
  [^AlertDialog$Builder builder,
   {:keys [positive-text positive-callback]} _]
  (.setPositiveButton builder (res/get-string (.getContext builder) positive-text)
                      (listeners/on-click-call positive-callback)))

(deftrait :negative-button
  "Takes :negative-text (either string or resource ID)
  and :negative-callback (function of 2 args: dialog and result), and sets it as
  the negative button for the dialog."
  {:attributes [:negative-text :negative-callback]}
  [^AlertDialog$Builder builder,
   {:keys [negative-text negative-callback]} _]
  (.setNegativeButton builder (res/get-string (.getContext builder) negative-text)
                      (listeners/on-click-call negative-callback)))

(deftrait :neutral-button
  "Takes :neutral-text (either string or resource ID)
  and :neutral-callback (function of 2 args: dialog and result), and sets it as
  the neutral button for the dialog."
  {:attributes [:neutral-text :neutral-callback]}
  [^AlertDialog$Builder builder,
   {:keys [neutral-text neutral-callback]} _]
  (.setNeutralButton builder (res/get-string (.getContext builder) neutral-text)
                     (listeners/on-click-call neutral-callback)))

(deftrait :view
  "Takes a tree of a view description and sets it as the custom view of the
  dialog."
  [^AlertDialog$Builder builder
   {:keys [view]} _]
  (.setView builder (make-ui-element (.getContext builder) view {})))


(deftrait :items
  "Takes :item-list (a collection of strings or IDs pointing to strings)
  and :item-callback (function of 2 args: dialog and picked item as integer), and sets it as
  the list for the dialog."
  {:attributes [:item-list :item-callback]}
  [^AlertDialog$Builder builder,
   {:keys [item-list item-callback]} _]
  (.setItems builder #^"[Ljava.lang.String;" (into-array ^String java.lang.CharSequence (map res/get-string item-list))
             (listeners/on-click-call item-callback)))

(defelement :alert-dialog-builder
  :classname AlertDialog$Builder
  :inherits nil
  :traits [:positive-button :negative-button :neutral-button :items :view])

(defn ^AlertDialog$Builder alert-dialog-builder
  "Creates a AlertDialog$Builder options with the given parameters."
  [context options-map]
  (neko.ui/make-ui context [:alert-dialog-builder options-map]))

; extend protocol for finding custom views in alert dialogs
(extend-protocol neko.find-view/ViewFinder
  AlertDialog
  (find-view [^AlertDialog alert-dialog, id]
    (.findViewById alert-dialog (neko.-utils/int-id id))))
