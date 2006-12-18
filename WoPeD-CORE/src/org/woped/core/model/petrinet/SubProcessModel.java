/*
 * 
 * Copyright (C) 2004-2005, see @author in JavaDoc for the author 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * For contact information please visit http://woped.ba-karlsruhe.de
 *
 */
package org.woped.core.model.petrinet;

import java.util.Iterator;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;
import org.woped.core.model.AbstractElementModel;
import org.woped.core.model.CreationMap;
import org.woped.core.model.ModelElementContainer;
import org.woped.core.model.ModelElementFactory;

/**
 * @author lai
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */

@SuppressWarnings("serial")
public class SubProcessModel extends TransitionModel implements InnerElementContainer
{

    public static final int WIDTH  = 40;
    public static final int HEIGHT = 40;

    public SubProcessModel(CreationMap map)
    {
        //super(jGraphModel);
        super(map);
        setSize(WIDTH, HEIGHT);
        getToolSpecific().setSubprocess(true);
        
        // The sub element container
        // is owned by the subprocess
        subElementContainer.setOwningElement(this);
                
    }

    public String getToolTipText()
    {
        return "Subprocess\nID: " + getId() + "\nName: " + getNameValue() + "\nSubprocess feature is not implemented, yet.";
    }

    public int getType()
    {
        return PetriNetModelElement.SUBP_TYPE;
    }
    
    private int                   subElementCounter    = 0;
    private ModelElementContainer subElementContainer  = new ModelElementContainer();
    private static final String    SUBELEMENT_SEPERATOR     = "_";
        
    public ModelElementContainer getSimpleTransContainer()
    {
    	return subElementContainer;
    }
	
    public AbstractElementModel addElement(AbstractElementModel element)
    {
    	return subElementContainer.addElement(element);    	
    }
    public void addReference(String arcId, DefaultPort sourceId, DefaultPort targetId)
    {
        subElementContainer.addReference(ModelElementFactory.createArcModel(arcId, sourceId, targetId));
    }
    public AbstractElementModel getElement(Object elementId)
    {
    	return subElementContainer.getElementById(elementId);
    }
    public String getNewElementId()
    {
        subElementCounter++;
        return getId() + SUBELEMENT_SEPERATOR + subElementCounter;    	
    }
    
    //! Overwritten to allow new outgoing connections only
    //! if there is not already an outgoing connection 
    //! (Sub-processes must have exactly one input and one output)
	public boolean getAllowOutgoingConnections()
	{
		boolean result = true;
		int nNumOutgoing = 0;
		for (Iterator i = getPort().edges(); i.hasNext();)
		{
			Object o = i.next();
			if (o instanceof Edge)
			{        				
				Edge e = (Edge)o;
				if (e.getSource()==getPort())
					++nNumOutgoing;
			}
		}
		result = (nNumOutgoing == 0);
		return result;
	}
  
}