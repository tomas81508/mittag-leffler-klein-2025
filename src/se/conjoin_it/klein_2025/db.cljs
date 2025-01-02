(ns se.conjoin-it.klein-2025.db)

(defonce db-atom (atom {}))

(defn create-initial-state
  [state]
  (merge {:page :home}
         state))
