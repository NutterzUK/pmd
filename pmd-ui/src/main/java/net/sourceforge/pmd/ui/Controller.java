package net.sourceforge.pmd.ui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sourceforge.pmd.*;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.property.BeanProperty;
import org.controlsfx.property.BeanPropertyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.control.PropertySheet.Mode;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.property.BeanPropertyUtils;

public class Controller implements Initializable {

    /**
     * The run button at the bottom of the scene.
     */
    @FXML
    private Button runButton;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    VBox leftVBox;

    @FXML
    VBox midVBox;

    @FXML
    VBox rightVBox;

    /**
     * Model stores everything to do with ruleset currently being edited.
     */
    private RuleSetEditorModel ruleSetEditorModel;

    /**
     * Tree view for the library section on the left
     */

    private CheckTreeView<TreeNode> currentRulesetTreeView;
    private CheckTreeView<TreeNode> libraryTreeView;
    private RuleSetEditorModel model;
    private Stage stage;

    public void setStageAndSetupListeners(Stage stage){
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      //  runButton.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.PLAY_CIRCLE));
        model = new RuleSetEditorModel();
        addLibraryTree();
        addCurrentRulesetTree();
        rightVBox.getChildren().add(getPanel());
    }

    /**
     * Create and add the current ruleset tree.
     */
    void addCurrentRulesetTree(){
        CheckBoxTreeItem<TreeNode> root = new CheckBoxTreeItem<TreeNode>(new TreeNode("Current Ruleset"));
        root.setExpanded(true);
        currentRulesetTreeView = new CheckTreeView<TreeNode>(root);

        Collection<Rule> rules = model.getCurrentRuleset().getRules();
        CheckBoxTreeItem<TreeNode> ruleNode;

        for(Rule rule : rules){
            ruleNode = new CheckBoxTreeItem<TreeNode>(new TreeNode(rule));
            root.getChildren().add(ruleNode);
        }

        currentRulesetTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentRulesetTreeView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<TreeNode>>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TreeItem<TreeNode>> c) {
                Object data = currentRulesetTreeView.getSelectionModel().getSelectedItem().getValue().getData();
                if(data instanceof Rule) {
                    updateConfigurationArea((Rule) currentRulesetTreeView.getSelectionModel().getSelectedItem().getValue().getData());
                }
            }
        });

        currentRulesetTreeView.setPrefHeight(99999);
        midVBox.setPadding(new Insets(2));
        midVBox.getChildren().add(currentRulesetTreeView);
    }

    private void updateConfigurationArea(Rule rule){
        List<PropertyDescriptor<?>> pds = rule.getPropertyDescriptors();

        for(PropertyDescriptor pd : pds){



        }

    }

    private void updateCurrentRulesetTree(){
        midVBox.getChildren().remove(currentRulesetTreeView);
        addCurrentRulesetTree();
    }

    /**
     * Create and add the library tree on the left.
     */
    void addLibraryTree(){
        CheckBoxTreeItem<TreeNode> child;

        // Tree labels are the ruleset name and the tree requires a root of type RuleSet
        RuleSet rootRuleset = new RuleSet();
        rootRuleset.setName("All Rulesets");
        CheckBoxTreeItem<TreeNode> root = new CheckBoxTreeItem<TreeNode>(new TreeNode("Root"));
        try {
            Iterator<RuleSet> rulesetIterator = model.getRuleLibrary();
                while (rulesetIterator.hasNext()) {
                    RuleSet rs = rulesetIterator.next();
                    child = createNodeForTree(rs);
                    root.getChildren().add(child);
                }
        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();
        }

        libraryTreeView = new CheckTreeView<TreeNode>(root);
        libraryTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        libraryTreeView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<TreeNode>>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TreeItem<TreeNode>> c) {
                System.out.println(libraryTreeView.getSelectionModel().getSelectedItem().getValue().toString());
            }
        });

        libraryTreeView.setPrefHeight(99999);
        leftVBox.setPadding(new Insets(2));
        leftVBox.getChildren().add(libraryTreeView);
    }

    private CheckBoxTreeItem<TreeNode> createNodeForTree(RuleSet ruleset){
        // What happens with nested rulesets?
        CheckBoxTreeItem<TreeNode> root = new CheckBoxTreeItem<TreeNode>(new TreeNode(ruleset));
        CheckBoxTreeItem<TreeNode> child;
        Collection<Rule> rules = ruleset.getRules();
        for(Rule rule : rules){
            child = new CheckBoxTreeItem<TreeNode>(new TreeNode(rule));
            root.getChildren().add(child);
        }
        return root;
    }

    @FXML
    protected void onAddButtonClick() {
        ObservableList<TreeItem<TreeNode>> checkedItems = libraryTreeView.getCheckModel().getCheckedItems();
        for(TreeItem<TreeNode> treeItem : checkedItems){
            TreeNode node = treeItem.getValue();
            // We do not need to add rulesets as we are adding individual rules at this point.
            if(node.isRule()){
                model.getCurrentRuleset().addRule((Rule)node.getData());
            }
        }

        libraryTreeView.getRoot().getChildren().removeAll(libraryTreeView.getCheckModel().getCheckedItems());
        updateCurrentRulesetTree();
    }

    @FXML
    protected void onSaveAs() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        try {
            if(file != null){
                RuleSetWriter rsw;
                rsw = new RuleSetWriter(new FileOutputStream(file));
                rsw.write(model.getCurrentRuleset());
            }
        } catch (IOException e) {
            // Probably access denied.
            e.printStackTrace();
        }
    }


    @FXML
    protected void onRemoveButtonClick() {
        System.out.println("The button was clicked!");
    }


    private PropertySheet propertySheet = new PropertySheet();


    private static Map<String,Object> customDataMap = new LinkedHashMap<>();

    static {
        customDataMap.put("1. Name#First Name", "Jonathan");
        customDataMap.put("1. Name#Last Name", "Giles");
        customDataMap.put("1. Name#Birthday",  LocalDate.of(1985, Month.JANUARY, 12));
        customDataMap.put("2. Billing Address#Address 1", "");
        customDataMap.put("2. Billing Address#Address 2", "");
        customDataMap.put("2. Billing Address#City", "");
        customDataMap.put("2. Billing Address#State", "");
        customDataMap.put("2. Billing Address#Zip", "");
        customDataMap.put("3. Phone#Home", "123-123-1234");
        customDataMap.put("3. Phone#Mobile", "234-234-2345");
        customDataMap.put("3. Phone#Work", "");
    }

    class CustomPropertyItem implements Item {

        private String key;
        private String category, name;

        public CustomPropertyItem(String key) {
            this.key = key;
            String[] skey = key.split("#");
            category = skey[0];
            name  = skey[1];
        }

        @Override public Class<?> getType() {
            return customDataMap.get(key).getClass();
        }

        @Override public String getCategory() {
            return category;
        }

        @Override public String getName() {
            return name;
        }

        @Override public String getDescription() {
            return null;
        }

        @Override public Object getValue() {
            return customDataMap.get(key);
        }

        @Override public void setValue(Object value) {
            customDataMap.put(key, value);
        }

    }

    class ActionShowInPropertySheet extends Action {


        public ActionShowInPropertySheet(  ) {
            super("title");
        }

        private ObservableList<Item> getCustomModelProperties() {
            ObservableList<Item> list = FXCollections.observableArrayList();
            for (String key : customDataMap.keySet() ) {
                list.add(new CustomPropertyItem(key));
            }
            return list;
        }

        public void execute(ActionEvent ae) {

            Service<?> service = new Service<ObservableList<Item>>() {

                @Override protected Task<ObservableList<Item>> createTask() {
                    return new Task<ObservableList<Item>>() {
                        @Override protected ObservableList<Item> call() throws Exception {
                            return getCustomModelProperties();
                        }
                    };
                }

            };
            service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

                @SuppressWarnings("unchecked") @Override public void handle(WorkerStateEvent e) {
                    propertySheet.getItems().setAll((ObservableList<Item>) e.getSource().getValue());
                }
            });
            service.start();
        }
    }

    public Node getPanel() {

        //VBox infoPane = new VBox(10);
        //infoPane.setPadding( new Insets(20,20,20,20));

        Button button = new Button("Title");
        ActionShowInPropertySheet ac = new ActionShowInPropertySheet();
        ac.execute(null);
        propertySheet.setModeSwitcherVisible(false);

        return propertySheet;
    }


}
