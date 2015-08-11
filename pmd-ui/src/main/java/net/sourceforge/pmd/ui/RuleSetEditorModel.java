package net.sourceforge.pmd.ui;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

import java.util.Iterator;

public class RuleSetEditorModel {


    public RuleSetEditorModel(){
        RuleSetFactory factory = new RuleSetFactory();
        Iterator<RuleSet> i = null;
        try {
            i = factory.getRegisteredRuleSets();
            while (i.hasNext()) {
                System.out.println(i.next().getName());
            }
        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();
        }
    }
}
