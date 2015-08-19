package net.sourceforge.pmd.ui;


import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;

/**
 * A container for rulesets and rules for the tree.
 * Implements a toString method for the tree labels.
 * Rulesets and rules do not have a common interface and using Object would mean using default toStrings.
 */
public class TreeNode {

    Object data;

    public TreeNode(Object data){
        this.data = data;
    }

    public boolean isRuleset(){
        return data instanceof RuleSet;
    }

    public boolean isRule(){
        return data instanceof Rule;
    }

    public Object getData(){
        return data;
    }

    public String toString(){

        if(isRule()){
            return ((Rule)data).getName();
        }

        if(isRuleset()){
            return ((RuleSet)data).getName();
        }

        return data.toString();
    }
}
