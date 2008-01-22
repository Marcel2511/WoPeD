package org.woped.qualanalysis.reachabilitygraph.data;


import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

public class ReachabilityLayoutHierarchic {

	public static JGraph layoutGraph(JGraph graph){
		applyHierarchicLayout(graph);			
		graph.getGraphLayoutCache().reload();
		return graph;
	}
	
	private static void applyHierarchicLayout(JGraph graph){
		GraphModel model = graph.getModel();
		LinkedList<ReachabilityPlaceModel> markings = new LinkedList<ReachabilityPlaceModel>();
		for(int i = 0; i < model.getRootCount(); i++){
			if(model.getRootAt(i) instanceof ReachabilityPlaceModel){
				markings.add((ReachabilityPlaceModel) model.getRootAt(i));
			}
		}
		ReachabilityPlaceModel initialPlace = ReachabilityGraphModel.lookupInitialMarking(markings);
		if(initialPlace != null){
			GraphConstants.setBackground(initialPlace.getAttributes(), Color.green);
			Rectangle2D bounds = GraphConstants.getBounds(initialPlace.getAttributes());
			LinkedList<ReachabilityPlaceModel> toProof = new LinkedList<ReachabilityPlaceModel>();
			ReachabilityLayoutHierarchic.hierarcher(initialPlace, new Rectangle2D.Double(10, 0, bounds.getWidth(), bounds.getHeight()), toProof);
			ReachabilityLayoutHierarchic.hierarcherProofer(toProof);
		}
	}
	
	private static void hierarcherProofer(LinkedList<ReachabilityPlaceModel> places){
		if(places.size() > 0){
			ReachabilityPlaceModel first = places.removeFirst();
			Iterator<ReachabilityPlaceModel> iter = places.iterator();
			boolean changedOne = false;
			while(iter.hasNext()){
				ReachabilityPlaceModel actual = iter.next();
				if(GraphConstants.getBounds(actual.getAttributes()).getX() == GraphConstants.getBounds(first.getAttributes()).getX() && 
						GraphConstants.getBounds(actual.getAttributes()).getY() == GraphConstants.getBounds(first.getAttributes()).getY()){
					Rectangle2D bounds = GraphConstants.getBounds(actual.getAttributes());
					GraphConstants.setBounds(actual.getAttributes(), new Rectangle2D.Double(bounds.getX() + 50 + bounds.getWidth(),bounds.getY(),bounds.getWidth(),bounds.getHeight()));
					changedOne = true;
				}
			}
			if(changedOne){
				places.addFirst(first);
				hierarcherProofer(places);
			} else {
				hierarcherProofer(places);
			}
		}
	}
	
	private static LinkedList<ReachabilityPlaceModel> hierarcher(ReachabilityPlaceModel place, Rectangle2D bounds, LinkedList<ReachabilityPlaceModel> places){
		if(!place.isSetRecursiveBounds()){
			GraphConstants.setBounds(place.getAttributes(), bounds);
			place.setIsSetRecursiveBounds(true);
			places.add(place);
		}
		bounds = GraphConstants.getBounds(place.getAttributes());
		LinkedList<ReachabilityEdgeModel> edges = new LinkedList<ReachabilityEdgeModel>();
		List ports = place.getChildren();
		for(int portIndex = 0; portIndex < ports.size(); portIndex++){
			if(ports.get(portIndex) instanceof ReachabilityPortModel){
				ReachabilityPortModel port = (ReachabilityPortModel) ports.get(portIndex);
				Set<ReachabilityEdgeModel> edgeSet = port.getEdges();
		     	Iterator<ReachabilityEdgeModel> edgeIterator = edgeSet.iterator();
		     	while(edgeIterator.hasNext()){
		     		ReachabilityEdgeModel next = edgeIterator.next();
		     		if(ports.get(portIndex) == next.getSource()){
		     			edges.add(next);	
		     		}
		     	}
			}
		}
		Iterator<ReachabilityEdgeModel> edgesIter = edges.iterator();
		int edgeCount = 0;
		LinkedList<ReachabilityPlaceModel> childs = new LinkedList<ReachabilityPlaceModel>();
     	while(edgesIter.hasNext()){
     		ReachabilityEdgeModel edge = (ReachabilityEdgeModel) edgesIter.next();
     		ReachabilityPortModel otherPort = getOtherPort(place, edge);
     		// (x,y,w,h) y = hoch/runter x = links/rechts		
			ReachabilityPlaceModel childPlace = (ReachabilityPlaceModel) otherPort.getParent();
			if(!childPlace.isSetRecursiveBounds()){
				int puffer = 0;
				if(edgeCount > 0){
					puffer = 50;
				}
				GraphConstants.setBounds(childPlace.getAttributes(), new Rectangle2D.Double(25 + bounds.getX() + ((puffer + bounds.getWidth()) * edgeCount++), bounds.getY() + bounds.getHeight() + 150, bounds.getWidth(), bounds.getHeight()));
				childPlace.setIsSetRecursiveBounds(true);
				places.add(childPlace);
				childs.add(childPlace);
			}	
     	}
     	Iterator<ReachabilityPlaceModel> iterChilds = childs.iterator();
     	while(iterChilds.hasNext()){
     		ReachabilityPlaceModel actualChild = iterChilds.next(); 
     		Rectangle2D childBound = GraphConstants.getBounds(actualChild.getAttributes());
     		hierarcher(actualChild, childBound, places);
     	}
     	return places; 
	}

	private static ReachabilityPortModel getOtherPort(ReachabilityPlaceModel place, ReachabilityEdgeModel edge){
		List ports = place.getChildren();
		for(int portIndex = 0; portIndex < ports.size(); portIndex++){
			if(edge.getSource() == place.getChildAt(portIndex)){
				return (ReachabilityPortModel) edge.getTarget();
			} else if(edge.getTarget() == place.getChildAt(portIndex)){
				return (ReachabilityPortModel) edge.getSource();
			}
		}
		return null;
	}

}