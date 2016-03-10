(ns telegram.client
  (:require  [clj-http.client :as client]
    [clojure.data.json :as json]))

(def api-url (str "https://api.telegram.org/bot"))

(def help [
            "Commands:"
            "!h - see this message."
            ])

(defn- request [verb token data]
  (let [response (client/post (str api-url token "/" verb)
                   {:form-params data
                     :as :json})
         json     (json/read-str (:body response) :key-fn keyword)]
    (clojure.pprint/pprint json)
    json))

(defn get-me [token]
  (request "getMe" token nil))

(defn send-message [token chat-id text & optionals]
  (request "sendMessage" token (merge {:chat_id chat-id
                                        :text (clojure.string/join "\r\n" text)} optionals)))

(defn get-updates
  ([token] (request "getUpdates" token nil))
  ([token offset] (request "getUpdates" token {:offset offset})))

(defn process-update [token update]
  (let [m (:message update)
         chat-id (-> m :chat :id)
         text (-> m :text)
         update-id (:update_id update)
         [command string] (clojure.string/split text #" " 2)]
    (case command
      "/start" [chat-id help]
      [chat-id ["Command not found"]])))