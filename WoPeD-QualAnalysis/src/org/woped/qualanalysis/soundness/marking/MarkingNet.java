package org.woped.qualanalysis.soundness.marking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.woped.qualanalysis.soundness.datamodel.AbstractNode;
import org.woped.qualanalysis.soundness.datamodel.LowLevelPetriNet;
import org.woped.qualanalysis.soundness.datamodel.PlaceNode;
import org.woped.qualanalysis.soundness.datamodel.TransitionNode;

/**
 * this class represents a marking net
 * 
 * @author Patrick Spies, Patrick Kirchgaessner, Joern Liebau, Enrico Moeller, Sebastian Fuss
 */
public class MarkingNet {

    /** source lowLevel petri net. */
    private final LowLevelPetriNet lolNet;
    /** all places of the source lowLevel petri net. ->static order! */
    private final PlaceNode[] places;
    /** all transitions of the source lowLevel petri net. */
    private final TransitionNode[] transitions;
    /** set of all markings. */
    private final Set<Marking> markings = new HashSet<Marking>();
    /** initial marking. */
    private Marking initialMarking;

    private List<Marking> markingList = new ArrayList<Marking>();

    /**
     * 
     * @param lolNet the LowLevelPetriNet on that this marking net is builded
     */
    public MarkingNet(LowLevelPetriNet lolNet) {

        this.lolNet = lolNet;
        this.places = this.lolNet.getPlaces();
        this.transitions = this.lolNet.getTransitions();
        Integer[] tokens = new Integer[this.places.length];
        Boolean[] placeUnlimited = new Boolean[this.places.length];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = places[i].getTokenCount();
            placeUnlimited[i] = false;
        }
        initialMarking = new Marking(tokens, places, placeUnlimited);
        initialMarking.setInitial(true);
        markings.add(initialMarking);
        markingList.add(initialMarking);
    }

    /**
     * @param marking the marking to check transitions with
     * @return an array of transitions which are activated
     */
    public TransitionNode[] getActivatedTransitions(Marking marking) {
        // declaration
        Set<TransitionNode> activatedTransitions = new HashSet<TransitionNode>();
        AbstractNode[] preNodes;
        Integer[] tokens = marking.getTokens(); // tokens of the given marking
        Boolean[] placeUnlimited = marking.getPlaceUnlimited();
        Boolean activated; // flag if transition is activated or not

        for (int i = 0; i < transitions.length; i++) {
            preNodes = transitions[i].getPreNodes(); // get prePlaces of current
            // transition
            activated = true; // initialize flag for current transition
            for (int j = 0; j < preNodes.length && activated; j++) {
                for (int k = 0; k < places.length && activated; k++) {
                    if (preNodes[j] == places[k]) {
                        if (tokens[k] <= 0 && !placeUnlimited[k]) { // current PrePlace without token?
                            activated = false;
                        }
                    }
                }
            }
            if (activated) {
                activatedTransitions.add(transitions[i]); // add transition to
                // set if activated
            }
        }
        // return all activated transitions as an array
        return activatedTransitions.toArray(new TransitionNode[0]);
    }

    /**
     * @return the initialMarking
     */
    public Marking getInitialMarking() {
        return initialMarking;
    }

    /**
     * @return the set of markings
     */
    public Set<Marking> getMarkings() {
        return this.markings;
    }

    /**
     * @return the places as an array
     */
    public PlaceNode[] getPlaces() {
        return this.places;
    }

    /**
     * @return the transitions as an array
     */
    public TransitionNode[] getTransitions() {
        return transitions;
    }

    /**
     * @return the places name and id as string
     */
    public String placesToString() {
        String line = "";
        for (int i = 0; i < this.places.length; i++) {
            line += this.places[i] + ",";
        }
        return line.substring(0, line.length() - 1);
    }

    /**
     * @return the places id as string
     */
    public String placesToStringId() {
        String line = "";
        for (int i = 0; i < this.places.length; i++) {
            line += this.places[i].getId() + ",";
        }
        return line.substring(0, Math.max(line.length() - 1, 0));
    }

    /**
     * @return the places name as string
     */
    public String placesToStringName() {
        String line = "";
        for (int i = 0; i < this.places.length; i++) {
            line += this.places[i].getName() + ",";
        }
        return line.substring(0, Math.max(line.length() - 1, 0));
    }

    /**
     * calculate the succeeding marking for a specified Transition Node switched with a specific marking
     * 
     * @param parentMarking
     * @param transition the transition to switch
     * @return a new Marking with the tokens after the transition is switched
     */
    public Marking calculateSucceedingMarking(Marking parentMarking, TransitionNode transition) {
        Integer[] tokens = new Integer[parentMarking.getTokens().length];
        AbstractNode[] preNodes = transition.getPreNodes();
        AbstractNode[] postNodes = transition.getPostNodes();

        // copy tokens from given marking (call by value)
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = parentMarking.getTokens()[i];
        }

        // decrease all tokenCounts for prePlaces of given transition
        // and increase all tokenCounts for postPlaces of given transition
        for (int i = 0; i < places.length; i++) {
            for (int j = 0; j < preNodes.length; j++) {
                if (preNodes[j] == places[i]) {
                    tokens[i]--;
                }
            }
            for (int j = 0; j < postNodes.length; j++) {
                if (postNodes[j] == places[i]) {
                    tokens[i]++;
                }
            }
        }
        return new Marking(tokens, places, parentMarking.getPlaceUnlimited());
    }

}
