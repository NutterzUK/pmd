package net.sourceforge.pmd.ui;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

import java.util.Collection;
import java.util.Iterator;


/**
 * The rulesetEditorModel stores a ruleset currently being edited as well as a collection of available rules.
 * It keeps track of the current state of the editor.
 */
public class RuleSetEditorModel {

    RuleSetFactory rsf;
    RuleSet currentRuleset;

    public RuleSetEditorModel(){
        rsf = new RuleSetFactory();
        currentRuleset = new RuleSet();
        Iterator<RuleSet> rulesetIterator = null;
        Collection<Rule> ruleCollection = null;
    }

    public Iterator<RuleSet> getRuleLibrary() throws RuleSetNotFoundException {
        return rsf.getRegisteredRuleSets();
    }

    public RuleSet getCurrentRuleset(){
        return currentRuleset;
    }
}
