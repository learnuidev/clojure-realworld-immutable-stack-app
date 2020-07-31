(ns conduit.db.utils)

(defn uuid [] (java.util.UUID/randomUUID))

(defn string->uuid [string]
  (if (uuid? string)
    string
    (java.util.UUID/fromString string)))
