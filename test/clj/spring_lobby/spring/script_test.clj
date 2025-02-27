(ns spring-lobby.spring.script-test
  (:require
    [clojure.string :as string]
    [clojure.test :refer [deftest is]]
    [spring-lobby.spring.script :as spring-script]
    [spring-lobby.spring-test :refer [expected-script-data expected-script-txt]]))


(deftest tag=
  (is (true? (spring-script/tag= {:def "true" :type "bool"} "1")))
  (is (false? (spring-script/tag= {:def "true" :type "bool"} "0")))
  (is (true? (spring-script/tag= {:def "false" :type "bool"} "0")))
  (is (false? (spring-script/tag= {:def "false" :type "bool"} "1"))))


(deftest flatten-scripttags
  (is (= []
         (spring-script/flatten-scripttags nil)
         (spring-script/flatten-scripttags {})))
  (is (= ["game/modoptions/test=true"]
         (spring-script/flatten-scripttags
           {:game
            {:modoptions
             {:test "true"}}})))
  (is (= (set
           (string/split
             "game/mapoptions/fog=0\tgame/mapoptions/inv=0\tgame/mapoptions/extractorradius=100\tgame/mapoptions/metal=normal\tgame/mapoptions/tidal=normal\tgame/mapoptions/timeofday=day\tgame/mapoptions/weather=clear\tgame/mapoptions/wind=normal\tgame/modoptions/allow_buzz=0\tgame/modoptions/anon_ffa=0\tgame/modoptions/critters=1\tgame/modoptions/disablemapdamage=0\tgame/modoptions/fixedallies=1\tgame/modoptions/mo_coop=0\tgame/modoptions/mo_enemycomcount=1\tgame/modoptions/mo_ffa=0\tgame/modoptions/mo_heatmap=1\tgame/modoptions/mo_newbie_placer=0\tgame/modoptions/mo_no_close_spawns=1\tgame/modoptions/mo_preventcombomb=0\tgame/modoptions/shareddynamicalliancevictory=0\tgame/modoptions/capturebonus=0.5\tgame/modoptions/captureradius=500\tgame/modoptions/capturetime=30"
             #"\t"))
         (set
           (spring-script/flatten-scripttags
             {:game
              {:mapoptions
               {:fog "0"
                :inv "0"
                :extractorradius "100"
                :metal "normal"
                :tidal "normal"
                :timeofday "day"
                :weather "clear"
                :wind "normal"},
               :modoptions
               {:disablemapdamage "0"
                :fixedallies "1"
                :mo_heatmap "1"
                :mo_preventcombomb "0"
                :critters "1"
                :mo_coop "0"
                :mo_no_close_spawns "1"
                :mo_ffa "0"
                :captureradius "500"
                :anon_ffa "0"
                :allow_buzz "0"
                :shareddynamicalliancevictory "0"
                :capturebonus "0.5"
                :mo_enemycomcount "1"
                :mo_newbie_placer "0"
                :capturetime "30"}}})))))


(deftest parse-scripttags
  (is (= nil
         (spring-script/parse-scripttags "")))
  (is (= {:game
          {:modoptions
           {:test "true"}}}
         (spring-script/parse-scripttags "game/modoptions/test=true")))
  (is (= {:game
          {:mapoptions
           {:fog "0"
            :inv "0"
            :extractorradius "100"
            :metal "normal"
            :tidal "normal"
            :timeofday "day"
            :weather "clear"
            :wind "normal"},
           :modoptions
           {:disablemapdamage "0"
            :fixedallies "1"
            :mo_heatmap "1"
            :mo_preventcombomb "0"
            :critters "1"
            :mo_coop "0"
            :mo_no_close_spawns "1"
            :mo_ffa "0"
            :captureradius "500"
            :anon_ffa "0"
            :allow_buzz "0"
            :shareddynamicalliancevictory "0"
            :capturebonus "0.5"
            :mo_enemycomcount "1"
            :mo_newbie_placer "0"
            :capturetime "30"}}}
         (spring-script/parse-scripttags "game/mapoptions/fog=0\tgame/mapoptions/inv=0\tgame/mapoptions/extractorradius=100\tgame/mapoptions/metal=normal\tgame/mapoptions/tidal=normal\tgame/mapoptions/timeofday=day\tgame/mapoptions/weather=clear\tgame/mapoptions/wind=normal\tgame/modoptions/allow_buzz=0\tgame/modoptions/anon_ffa=0\tgame/modoptions/critters=1\tgame/modoptions/disablemapdamage=0\tgame/modoptions/fixedallies=1\tgame/modoptions/mo_coop=0\tgame/modoptions/mo_enemycomcount=1\tgame/modoptions/mo_ffa=0\tgame/modoptions/mo_heatmap=1\tgame/modoptions/mo_newbie_placer=0\tgame/modoptions/mo_no_close_spawns=1\tgame/modoptions/mo_preventcombomb=0\tgame/modoptions/shareddynamicalliancevictory=0\tgame/modoptions/capturebonus=0.5\tgame/modoptions/captureradius=500\tgame/modoptions/capturetime=30"))))

(deftest parse-number
  (is (= 0
         (spring-script/parse-number "0")))
  (is (= 1.0
         (spring-script/parse-number "1.0")))
  (is (= "0.00000 0.00000 0.98431"
         (spring-script/parse-number "0.00000 0.00000 0.98431"))))

(deftest parse-script
  (is (= expected-script-data
         (assoc-in
           (spring-script/parse-script expected-script-txt)
           [:game :hostip] nil))))
