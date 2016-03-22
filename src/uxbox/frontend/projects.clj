;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) 2016 Andrey Antukh <niwi@niwi.nz>

(ns uxbox.frontend.projects
  (:require [promesa.core :as p]
            [catacumba.http :as http]
            [uxbox.schema :as us]
            [uxbox.services :as sv]
            [uxbox.util.uuid :as uuid]
            [uxbox.frontend.core :as ufc]
            [uxbox.frontend.auth :as ufa])
  (:import java.util.UUID))

(defn list-projects
  [{user :identity}]
  (let [params {:user user :type :project/list}]
    (-> (sv/query params)
        (p/then #(http/ok (ufc/rsp %))))))

(defn create-project
  [{user :identity params :data}]
  (p/alet [params (assoc params
                         :type :project/create
                         :user user)
           result (p/await (sv/novelty params))
           loc (str "/api/projects/" (:id result))]
    (http/created loc (ufc/rsp result))))

(defn update-project
  [{user :identity params :route-params data :data}]
  (let [params (merge data
                      {:id (uuid/from-string (:id params))
                       :type :project/update
                       :user user})]
    (-> (sv/novelty params)
        (p/then #(http/ok (ufc/rsp %))))))

(defn delete-project
  [{user :identity params :route-params}]
  (let [params {:id (uuid/from-string (:id params))
                :type :project/delete
                :user user}]
    (-> (sv/novelty params)
        (p/then (fn [v] (http/no-content))))))

