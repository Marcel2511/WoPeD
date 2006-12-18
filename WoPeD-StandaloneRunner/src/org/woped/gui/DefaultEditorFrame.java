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
/*
 * Created on Oct 5, 2004
 */
package org.woped.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import org.woped.editor.controller.PetriNetResourceEditor;
import org.woped.editor.controller.vc.EditorStatusBarVC;
import org.woped.editor.controller.vc.EditorVC;
import org.woped.editor.utilities.Messages;

/**
 * The EditorFrame is the container for each <code>Editor</code>.
 * 
 * @author Thomas Pohl
 */
@SuppressWarnings("serial")
public class DefaultEditorFrame extends JInternalFrame
{
    private EditorVC               m_editor                 = null;
    private PetriNetResourceEditor m_petriNetResourceEditor = null;
    private EditorStatusBarVC      m_statusBar              = null;

    public DefaultEditorFrame(EditorVC editor, PetriNetResourceEditor propEditor)
    {          
        super(editor.getName(), true, true, true, true);
        this.setVisible(false);
        m_editor = editor;
        this.getContentPane().add(getStatusBar(), BorderLayout.SOUTH);
        m_petriNetResourceEditor = propEditor;
    	addInternalFrameListener(m_editor);
        this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

        if (editor.isSubprocessEditor()) {
            this.setFrameIcon(Messages.getImageIcon("Popup.Add.Subprocess"));
            this.getContentPane().add(m_editor, BorderLayout.CENTER);
        } else {
            this.setFrameIcon(Messages.getImageIcon("Document"));
            
            // TabbedPane
            JScrollPane propScrollPane = new JScrollPane(getPetriNetResourceEditor());
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab(Messages.getString("PetriNet.Process.Title"), m_editor);
            tabbedPane.addTab(Messages.getString("PetriNet.Resources.Title"), propScrollPane);
            tabbedPane.getModel().addChangeListener(new ChangeListener()
            		{
            	public void stateChanged(ChangeEvent e)
            	{
            		getPetriNetResourceEditor().reset();
            		
            	}
            	
            		});
            
            this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
            if (propEditor != null)
            {
        	propScrollPane.addFocusListener(new FocusListener()
        	{
        	    public void focusGained(FocusEvent e)
        	    {
        		getPetriNetResourceEditor().reset();
        	    }
        	    
        	    public void focusLost(FocusEvent e)
        	    {
        		;
        	    }
        	});
            } else
            {
        	this.getContentPane().add(m_editor, BorderLayout.CENTER);
            }
        }
                
        setTitle(m_editor.getName());
        
        // Statusbar
        m_editor.setSaved(true);
        
        
        this.pack();
        this.repaint();
        this.setVisible(true);
    }

    private EditorStatusBarVC getStatusBar()
    {
        if (m_statusBar == null)
        {
           m_statusBar = new EditorStatusBarVC(m_editor);
        }
        
        return m_statusBar;
    }
    

    /**
     * 
     * @return returns the containing Editor.
     */
    public EditorVC getEditor()
    {
        return m_editor;
    }

    /**
     * @return Returns the m_petriNetResourceEditor.
     */
    public PetriNetResourceEditor getPetriNetResourceEditor()
    {
        return m_petriNetResourceEditor;
    }
	/**
	 * When a Frame isIconified it should be invisible.
	 */
	public void setIcon(boolean arg0) throws PropertyVetoException
    {
        super.setIcon(arg0);
        setVisible(!arg0);
    }
	

	//! Enable or disable the processing of all mouse events
	//! (also for all child components)
	//! @param if true, mouse events are accepted (default)
	//!        if false, mouse events are disabled
	public void acceptMouseEvents(boolean accept)
	{
        this.setClosable(accept);
        this.setResizable(accept);
        
		if (accept==false)
		{
	        glass.setOpaque(false);
	     
	        glass.addMouseListener(mouseGrabber);
	        glass.addMouseMotionListener(mouseGrabber);
	        glass.setVisible(true);
	        // Change glass pane to our panel
	        if (old==null)
	        	old = getRootPane().getGlassPane();
	        getRootPane().setGlassPane(glass);	        
		}
		else
		{
			glass.removeMouseListener(mouseGrabber);
			glass.removeMouseMotionListener(mouseGrabber);
			glass.setVisible(false);
			if (old!=null)
			{
				getRootPane().setGlassPane(old);
				old = null;
			}
		}
	}
	
	private JPanel glass = new JPanel();
	private Component old = null;
	private MouseInputAdapter mouseGrabber = new MouseInputAdapter() {};
}