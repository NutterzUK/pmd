package net.sourceforge.pmd.ui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    /**
     * The run button at the bottom of the scene.
     */
    @FXML
    private Button runButton;

    @FXML
    VBox leftVBox;

    @FXML
    VBox midVBox;

    /**
     * Model stores everything to do with ruleset currently being edited.
     */
    private RuleSetEditorModel ruleSetEditorModel;

    /**
     * Tree view for the library section on the left
     */
    private CheckTreeView<String> libraryTreeView;

    private CheckTreeView<String> currentRulesetTreeView;

    private RuleSetEditorModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        runButton.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.PLAY_CIRCLE));
        model = new RuleSetEditorModel();
        addLibraryTree();
        addCurrentRulesetTree();
    }

    /**
     * Create and add the current ruleset tree.
     */
    void addCurrentRulesetTree(){
        CheckBoxTreeItem<String> treeItem_Jonathan = new CheckBoxTreeItem<>("Jonathan");
        CheckBoxTreeItem<String> treeItem_Eugene = new CheckBoxTreeItem<>("Eugene");
        CheckBoxTreeItem<String> treeItem_Henry = new CheckBoxTreeItem<>("Henry");
        CheckBoxTreeItem<String> treeItem_Samir = new CheckBoxTreeItem<>("Samir");


        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<String>("Root");
        CheckBoxTreeItem<String> child = new CheckBoxTreeItem<String>("child");
        child.getChildren().add(treeItem_Samir);

        root.setExpanded(true);
        root.getChildren().addAll(
                treeItem_Jonathan,
                treeItem_Eugene,
                treeItem_Henry,
                child);

        currentRulesetTreeView = new CheckTreeView<>(root);
        currentRulesetTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentRulesetTreeView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<String>>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TreeItem<String>> c) {
                //updateText(selectedItemsLabel, c.getList());
            }
        });

        currentRulesetTreeView.setPrefHeight(99999);
        // A tiny bit of padding.
        midVBox.setPadding(new Insets(2));
        midVBox.getChildren().add(currentRulesetTreeView);
    }

    /**
     * Create and add the library tree on the left.
     */
    void addLibraryTree(){
        CheckBoxTreeItem<String> treeItem_Jonathan = new CheckBoxTreeItem<>("Jonathan");
        CheckBoxTreeItem<String> treeItem_Eugene = new CheckBoxTreeItem<>("Eugene");
        CheckBoxTreeItem<String> treeItem_Henry = new CheckBoxTreeItem<>("Henry");
        CheckBoxTreeItem<String> treeItem_Samir = new CheckBoxTreeItem<>("Samir");


        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<String>("Root");
        CheckBoxTreeItem<String> child = new CheckBoxTreeItem<String>("child");
        child.getChildren().add(treeItem_Samir);

        root.setExpanded(true);
        root.getChildren().addAll(
                treeItem_Jonathan,
                treeItem_Eugene,
                treeItem_Henry,
                child);

        libraryTreeView = new CheckTreeView<>(root);
        libraryTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        libraryTreeView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<String>>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TreeItem<String>> c) {
                //updateText(selectedItemsLabel, c.getList());
            }
        });

        libraryTreeView.setPrefHeight(99999);
        // A tiny bit of padding.
        leftVBox.setPadding(new Insets(2));
        leftVBox.getChildren().add(libraryTreeView);
    }
}
