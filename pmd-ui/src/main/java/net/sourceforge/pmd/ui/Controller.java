package net.sourceforge.pmd.ui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sourceforge.pmd.*;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.action.Action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;


import org.controlsfx.control.PropertySheet.Item;


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

    public void setStageAndSetupListeners(Stage stage) {
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
    void addCurrentRulesetTree() {
        CheckBoxTreeItem<TreeNode> root = new CheckBoxTreeItem<TreeNode>(new TreeNode("Current Ruleset"));
        root.setExpanded(true);
        currentRulesetTreeView = new CheckTreeView<TreeNode>(root);

        Collection<Rule> rules = model.getCurrentRuleset().getRules();
        CheckBoxTreeItem<TreeNode> ruleNode;

        for (Rule rule : rules) {
            ruleNode = new CheckBoxTreeItem<TreeNode>(new TreeNode(rule));
            root.getChildren().add(ruleNode);
        }

        currentRulesetTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentRulesetTreeView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<TreeNode>>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TreeItem<TreeNode>> c) {
                Object data = currentRulesetTreeView.getSelectionModel().getSelectedItem().getValue().getData();
                if (data instanceof Rule) {
                    updateConfigurationArea((Rule) currentRulesetTreeView.getSelectionModel().getSelectedItem().getValue().getData());
                }
            }
        });

        currentRulesetTreeView.setPrefHeight(99999);
        midVBox.setPadding(new Insets(2));
        midVBox.getChildren().add(currentRulesetTreeView);
    }

    private void updateConfigurationArea(Rule rule) {
        customDataMap.clear();
        ActionShowInPropertySheet ac = new ActionShowInPropertySheet(rule);
        ac.execute();

    }

    private void updateCurrentRulesetTree() {
        midVBox.getChildren().remove(currentRulesetTreeView);
        addCurrentRulesetTree();
    }

    /**
     * Create and add the library tree on the left.
     */
    void addLibraryTree() {
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

    private CheckBoxTreeItem<TreeNode> createNodeForTree(RuleSet ruleset) {
        // What happens with nested rulesets?
        CheckBoxTreeItem<TreeNode> root = new CheckBoxTreeItem<TreeNode>(new TreeNode(ruleset));
        CheckBoxTreeItem<TreeNode> child;
        Collection<Rule> rules = ruleset.getRules();
        for (Rule rule : rules) {
            child = new CheckBoxTreeItem<TreeNode>(new TreeNode(rule));
            root.getChildren().add(child);
        }
        return root;
    }

    @FXML
    protected void onAddButtonClick() {
        ObservableList<TreeItem<TreeNode>> checkedItems = libraryTreeView.getCheckModel().getCheckedItems();
        for (TreeItem<TreeNode> treeItem : checkedItems) {
            TreeNode node = treeItem.getValue();
            // We do not need to add rulesets as we are adding individual rules at this point.
            if (node.isRule()) {
                model.getCurrentRuleset().addRule((Rule) node.getData());
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
            if (file != null) {
                RuleSetWriter rsw;
                rsw = new RuleSetWriter(new FileOutputStream(file));
                rsw.write(model.getCurrentRuleset());
            }
        } catch (IOException e) {
            // Probably access denied.
            e.printStackTrace();
        }
    }


    public Node getPanel() {

        Button button = new Button("Title");
        //ActionShowInPropertySheet ac = new ActionShowInPropertySheet();
        //ac.execute();
        propertySheet.setModeSwitcherVisible(false);

        return propertySheet;
    }

    @FXML
    protected void onRemoveButtonClick() {
        System.out.println("The button was clicked!");
    }

    private PropertySheet propertySheet = new PropertySheet();


    private static List<PropertyDescriptor> customDataMap = new ArrayList<>();

    class CustomPropertyItem implements Item {

        private PropertyDescriptor descriptor;
        private Rule rule;

        public CustomPropertyItem(PropertyDescriptor descriptor, Rule rule) {
            this.descriptor = descriptor;
            this.rule = rule;
        }

        @Override
        public Class<?> getType() {
            return descriptor.type();
        }

        @Override
        public String getCategory() {
            return "";
        }

        @Override
        public String getName() {
            return descriptor.name();
        }

        @Override
        public String getDescription() {
            return descriptor.description();
        }

        @Override
        public Object getValue() {
            // Possible bug in controlsfx, if null returned for int or double, throws NPE.
            if ((getType().equals(Integer.class) || getType().equals(Double.class)) && rule.getProperty(descriptor) == null) {
                return 0;
            }
            return rule.getProperty(descriptor);
        }

        @Override
        public void setValue(Object value) {
            if (value != null) {
                rule.setProperty(descriptor, value);
            }
        }

    }

    class ActionShowInPropertySheet extends Action {

        Rule rule;

        public ActionShowInPropertySheet(Rule rule) {
            super("title");
            this.rule = rule;
        }

        private ObservableList<Item> getCustomModelProperties() {
            ObservableList<Item> list = FXCollections.observableArrayList();

            List<PropertyDescriptor<?>> pds = rule.getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                list.add(new CustomPropertyItem(pd, rule));
            }

            return list;
        }

        public void execute() {

            Service<?> service = new Service<ObservableList<Item>>() {

                @Override
                protected Task<ObservableList<Item>> createTask() {
                    return new Task<ObservableList<Item>>() {
                        @Override
                        protected ObservableList<Item> call() throws Exception {
                            return getCustomModelProperties();
                        }
                    };
                }

            };
            service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

                @SuppressWarnings("unchecked")
                @Override
                public void handle(WorkerStateEvent e) {
                    propertySheet.getItems().setAll((ObservableList<Item>) e.getSource().getValue());
                }
            });
            service.start();
        }
    }
}
