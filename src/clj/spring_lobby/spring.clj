(ns spring-lobby.spring
  "Interface to run Spring."
  (:require
    [com.evocomputing.colors :as colors]
    [taoensso.timbre :refer [info]]))


(defn script-data
  "Given data for a battle, returns data that can be directly formatted to script.txt format for Spring."
  ([battle]
   (script-data battle nil))
  ([battle opts]
   {:game
    (into
      {:gametype (:battle-modname battle)
       :mapname (:battle-map battle)
       :hostip (:battle-ip battle)
       :hostport (:battle-port battle)
       :ishost 1 ; TODO
       :numplayers 1 ; TODO
       :startpostype 2 ; TODO
       :numusers (count (:users battle))} ; TODO
      (concat
        (map
          (fn [[player {:keys [battle-status team-color user]}]]
            (info team-color)
            [(str "player" (:id battle-status))
             {:name player
              :team (:ally battle-status)
              :isfromdemo 0 ; TODO
              :countrycode (:country user)
              :rgbcolor (when-let [decimal-color (or (when (number? team-color) team-color)
                                                     (try (Integer/parseInt team-color) 
                                                          (catch Exception _ nil)))]
                          (info decimal-color)
                          (let [[r g b a :as rgba] (:rgba (colors/create-color decimal-color))]
                            (info r g b a rgba)
                            (str r " " g " " b)))}])
          (:users battle))
        (map
          (fn [[_player {:keys [battle-status]}]]
            [(str "team" (:id battle-status))
             {:teamleader (:id battle-status)
              :handicap (:handicap battle-status)
              :allyteam (:ally battle-status)
              ;:rgbcolor nil TODO
              :side (:side battle-status)}])
          (:users battle))
        (map
          (fn [[_bot-name {:keys [battle-status]}]]
            [(str "team" (:id battle-status))
             {:teamleader (:id battle-status)
              :handicap (:handicap battle-status)
              :allyteam (:ally battle-status)
              ;:rgbcolor nil TODO
              :side (:side battle-status)
              :options {}}]) ; TODO
          (:bots battle))
        (map
          (fn [ally]
            [(str "allyteam" ally) {}])
          (set (map (comp :ally :battle-status second) (mapcat battle [:users :bots]))))
        opts))}))

(defn script-txt-inner
  ([kv]
   (script-txt-inner "" kv))
  ([tabs [k v]]
   (str tabs
        (if (map? v)
          (str "[" (name k ) "]\n" tabs "{\n"
               (apply str (map (partial script-txt-inner (str tabs "\t")) (sort-by first v)))
               tabs "}\n")
          (str (name k) " = " v ";"))
        "\n")))

; https://springrts.com/wiki/Script.txt
; https://github.com/spring/spring/blob/104.0/doc/StartScriptFormat.txt
; https://github.com/springlobby/springlobby/blob/master/src/spring.cpp#L284-L590
(defn script-txt
  "Given data for a battle, return contents of a script.txt file for Spring."
  ([script-data]
   (apply str (map script-txt-inner (sort-by first script-data)))))
