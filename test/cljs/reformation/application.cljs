(ns reformation.application
  "The application with which users fill out the form to make or edit an application"
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [reformation.shared-test :as shared]
            [accountant.core]
            [reformation.routes :as rt]
            [reformation.core :as rfc]
            [re-frame.core :as reframe]
            [reformation.reframe]))

(defn validate-and-submit "Validate the form and submit"
  [form-dom-id]
  (let [form (.getElementById js/document form-dom-id)
        update-id (session/get :application)]
    (-> form .-classList (.add "was-validated"))
    (if (.checkValidity form)
      (js/alert "You have errors in your form. Please correct them before submitting."))))

(def my-atom (r/atom {:mything "hello"}))

(def FILE (r/atom nil))

(def test-form [:myhidden-text {:type :hidden
                                :default-value "whisper"}
                :mydefault-text {:type :text
                                 :label "default text"
                                 :default-value "something good"
                                 :disabled true
                                 :style-classes "I-like-red"}
                :mytext {:type :text
                         :label "My text"}
                :mytextarea {:type :textarea
                             :label "My textarea"}
                #_#_:mymultitable  {:label "My multitable"
                                :id :mymulti
                                :required? true
                                :type :multi-table
                                :min-rows 2
                                :subtext "Indicate any expenses involved in carrying out your request, including a reason for each expense"
                                :value-path [:my-multitable]
                                :sum-field :amount
                                :columns [{:key :item
                                           :title "Item"}
                                          {:key :amount
                                           :title "Amount"
                                           :input-type "number"}
                                          {:key :purpose
                                           :title "Purpose"
                                           :input-type "textarea"}]}
                :mytoggle {:type :togglebox
                           :label "My togglebox"
                           :content [:test {:type :text :label "My toggled "}]}
                :mycheckbox {:type :checkbox :label "My checkbox"}
                :myfileupload {:type :file
                               :label "My file"
                               :submit-text "Click or Drop a File Here"
                               :error-text "Maybe We had an error?"
                               ;:submit-button [:a.btn.btn-success "Submit!"]
                               :submit-fn #(js/alert "Trying to submit:")
                               :save-fn #(reset! FILE %)                               
                               :allowed-extensions-f #{"txt"}
                               :style-classes {:drag-over "dragover"
                                               :inactive "undragged"
                                               :have-file "have-file"}}])

(defn generate-form []
  (let [form-id "needs-validation"]
    [:div.submission-form 
     [:form.form-control {:id form-id}
      (into [:div.form-contents]
            (rfc/render-application test-form #_my-atom
                                    {:READ
                                     (fn [kv]
                                       @(reframe/subscribe [:read-form-item kv]))
                                     #_(partial get-in @my-atom)
                                     :UPDATE
                                     (fn [kv update-function]
                                       ;; dispatch-sync is required here, because the defer involved in plain reframe/dispatch causes the synthetic event to be released and the fn breaks. 
                                       (reframe/dispatch-sync [:update-form kv update-function]))
                                     #_(partial swap! my-atom update-in)}))]]))

 (defn app-page []
  (shared/page-template {:header-title "Reformation Application"
                         :contents [:div.mycontent
                                    [generate-form]
                                    ]}))
