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
package org.woped.editor.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.woped.core.model.CreationMap;
import org.woped.core.model.PetriNetModelProcessor;
import org.woped.core.model.petrinet.TransitionModel;
import org.woped.core.model.petrinet.TriggerModel;
import org.woped.core.utilities.Utils;
import org.woped.editor.action.DisposeWindowAction;
import org.woped.editor.controller.vc.EditorVC;
import org.woped.editor.utilities.Messages;

/**
 * @author waschtl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TransitionPropertyEditor extends JDialog implements ActionListener
{
    private JPanel                contentPanel                  = null;
    private JPanel                namePanel                     = null;
    private JLabel                nameLabel                     = null;
    private JTextField            nameTextField                 = null;

    // Trigger
    private JPanel                triggerPanel                  = null;
    private JLabel                triggerLabel                  = null;
    private JRadioButton          triggerNoneRadioButton        = null;
    private JRadioButton          triggerResourceRadioButton    = null;
    private JRadioButton          triggerMessageRadioButton     = null;
    private JRadioButton          triggerTimeRadioButton        = null;
    private ButtonGroup           triggerButtonGroup            = null;
    private DefaultButtonModel    triggerButtonModel            = null;

    // Branching
    private JPanel                branchingPanel                = null;
    private JLabel                branchingLabel                = null;
    private JRadioButton          branchingNoneRadioButton      = null;
    private JRadioButton          branchingAndSplittRadioButton = null;
    private JRadioButton          branchingAndJoinRadioButton   = null;
    private JRadioButton          branchingXorSplittRadioButton = null;
    private JRadioButton          branchingXorJoinRadioButton   = null;
    private ButtonGroup           branchingButtonGroup          = null;

    // Duration
    private JPanel                durationPanel                 = null;
    private JTextField            durationTextField             = null;
    private JLabel                durationLabel                 = null;
    private JComboBox             durationComboBox              = null;
    private static final String   COMBOBOX_HOURS_TEXT           = Messages.getString("Transition.Resources.Hours");
    private static final String   COMBOBOX_MINUTES_TEXT         = Messages.getString("Transition.Resources.Minutes");
    private static final String   COMBOBOX_SECONDS_TEXT         = Messages.getString("Transition.Resources.Seconds");
    private static final Object[] durationComboBoxA             = { COMBOBOX_HOURS_TEXT, COMBOBOX_MINUTES_TEXT, COMBOBOX_SECONDS_TEXT };

    // Resource
    private JPanel                resourcePanel                 = null;
    private JLabel                resourceLabel                 = null;
    private JLabel                resourceRoleLabel             = null;
    private JLabel                resourceOrgUnitLabel          = null;
    private JComboBox             resourceRoleComboBox          = null;
    private JComboBox             resourceOrgUnitComboBox       = null;
    private DefaultComboBoxModel  roleComboBoxModel             = null;
    private DefaultComboBoxModel  orgUnitComboBoxModel          = null;

    private static final String   TRIGGER_NONE                  = Messages.getString("Transition.Resources.Trigger.None");;
    private static final String   TRIGGER_MESSAGE               = Messages.getString("Transition.Resources.Trigger.Message");;
    private static final String   TRIGGER_RESOURCE              = Messages.getString("Transition.Resources.Trigger.Resource");;
    private static final String   TRIGGER_TIME                  = Messages.getString("Transition.Resources.Trigger.Time");;
    // Button Panel
    private JPanel                buttonPanel                   = null;
    private JButton               buttonOk                      = null;
    private JButton               buttonCancel                  = null;
    private JButton               buttonApply                   = null;
    // allgemein

    private TransitionModel       transition                    = null;
    private EditorVC              editor                        = null;

    public TransitionPropertyEditor(Frame owner, TransitionModel transition, EditorVC editor)
    {
        super(owner, true);
        this.transition = transition;
        this.editor = editor;
        this.setVisible(false);
        initialize();
        readTriggerConfiguration();
        this.setSize(280, 400);
        this.setLocation(Utils.getCenterPoint(owner.getBounds(), this.getSize()));
    }

    private void initialize()
    {
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        contentPanel.add(getNamePanel(), c);

        c.gridx = 0;
        c.gridy = 1;
        contentPanel.add(getTriggerPanel(), c);

        c.gridx = 0;
        c.gridy = 2;
        contentPanel.add(getBranchingPanel(), c);

        c.gridx = 0;
        c.gridy = 3;
        contentPanel.add(getDurationPanel(), c);

        c.gridx = 0;
        c.gridy = 4;
        contentPanel.add(getResourcePanel(), c);

        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);

    }

    private void readTriggerConfiguration()
    {
        if (transition.getToolSpecific().getTrigger() == null)
        {
            getTriggerNoneRadioButton().setSelected(true);
            actionPerformed(new ActionEvent(getTriggerNoneRadioButton(), -1, TRIGGER_NONE));
        } else if (transition.hasTrigger())
        {
            if (transition.getToolSpecific().getTrigger().getTriggertype() == TriggerModel.TRIGGER_RESOURCE)
            {
                getTriggerResourceRadioButton().setSelected(true);
                actionPerformed(new ActionEvent(getTriggerResourceRadioButton(), -1, TRIGGER_RESOURCE));
            } else if (transition.getToolSpecific().getTrigger().getTriggertype() == TriggerModel.TRIGGER_EXTERNAL)
            {
                getTriggerMessageRadioButton().setSelected(true);
                actionPerformed(new ActionEvent(getTriggerMessageRadioButton(), -1, TRIGGER_MESSAGE));
            } else if (transition.getToolSpecific().getTrigger().getTriggertype() == TriggerModel.TRIGGER_TIME)
            {
                getTriggerTimeRadioButton().setSelected(true);
                actionPerformed(new ActionEvent(getTriggerTimeRadioButton(), -1, TRIGGER_TIME));
            }
        }
    }

    // **************************NamePanel******************************
    private JPanel getNamePanel()
    {
        if (namePanel == null)
        {
            namePanel = new JPanel();
            namePanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            namePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(Messages.getString("Transition.Resources.Name")), BorderFactory.createEmptyBorder(5, 5, 10, 5)));
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;

            c.weightx = 1;
            c.gridx = 0;
            c.gridy = 0;
            namePanel.add(getNameTextField(), c);

        }
        return namePanel;
    }

    private JTextField getNameTextField()
    {
        if (nameTextField == null)
        {
            nameTextField = new JTextField(transition.getNameValue());
            nameTextField.setPreferredSize(new Dimension(150, 20));
            nameTextField.setMinimumSize(new Dimension(150, 20));
        }
        return nameTextField;
    }

    // ******************************TriggerPanel*****************************************
    private JPanel getTriggerPanel()
    {
        if (triggerPanel == null)
        {
            triggerPanel = new JPanel();
            triggerPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            triggerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(Messages.getString("Transition.Resources.Trigger")), BorderFactory.createEmptyBorder(5, 5, 10, 5)));
            triggerButtonGroup = new ButtonGroup();
            triggerButtonGroup.add(getTriggerNoneRadioButton());
            triggerButtonGroup.add(getTriggerResourceRadioButton());
            triggerButtonGroup.add(getTriggerMessageRadioButton());
            triggerButtonGroup.add(getTriggerTimeRadioButton());

            c.fill = GridBagConstraints.HORIZONTAL;

            // c.weightx = 1;
            c.gridx = 0;
            c.gridy = 0;
            triggerPanel.add(getTriggerNoneRadioButton(), c);

            c.gridx = 1;
            c.gridy = 0;
            triggerPanel.add(getTriggerResourceRadioButton(), c);

            c.gridx = 2;
            c.gridy = 0;
            triggerPanel.add(getTriggerMessageRadioButton(), c);

            c.gridx = 3;
            c.gridy = 0;
            triggerPanel.add(getTriggerTimeRadioButton(), c);

        }
        return triggerPanel;
    }

    private JRadioButton getTriggerNoneRadioButton()
    {
        if (triggerNoneRadioButton == null)
        {
            triggerNoneRadioButton = new JRadioButton(TRIGGER_NONE);
            triggerNoneRadioButton.setActionCommand(TRIGGER_NONE);
            triggerNoneRadioButton.addActionListener(this);
        }
        return triggerNoneRadioButton;
    }

    private JRadioButton getTriggerResourceRadioButton()
    {
        if (triggerResourceRadioButton == null)
        {
            triggerResourceRadioButton = new JRadioButton(TRIGGER_RESOURCE);
            triggerResourceRadioButton.setActionCommand(TRIGGER_RESOURCE);
            triggerResourceRadioButton.addActionListener(this);
        }
        return triggerResourceRadioButton;
    }

    private JRadioButton getTriggerMessageRadioButton()
    {
        if (triggerMessageRadioButton == null)
        {
            triggerMessageRadioButton = new JRadioButton(TRIGGER_MESSAGE);
            triggerMessageRadioButton.setActionCommand(TRIGGER_MESSAGE);
            triggerMessageRadioButton.addActionListener(this);
        }
        return triggerMessageRadioButton;
    }

    private JRadioButton getTriggerTimeRadioButton()
    {
        if (triggerTimeRadioButton == null)
        {
            triggerTimeRadioButton = new JRadioButton(TRIGGER_TIME);
            triggerTimeRadioButton.setActionCommand(TRIGGER_TIME);
            triggerTimeRadioButton.addActionListener(this);
        }
        return triggerTimeRadioButton;
    }

    // ******************************BranchingPanel****************************************
    private JPanel getBranchingPanel()
    {
        if (branchingPanel == null)
        {
            branchingPanel = new JPanel();
            branchingPanel.setLayout(new GridBagLayout());
            // branchingPanel.setMinimumSize(new Dimension(345, 90));
            // branchingPanel.setPreferredSize(new Dimension(345, 90));
            GridBagConstraints c = new GridBagConstraints();
            branchingPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(Messages.getString("Transition.Resources.Branching")), BorderFactory.createEmptyBorder(5, 5, 10, 5)));
            // c.anchor = GridBagConstraints.WEST;
            branchingButtonGroup = new ButtonGroup();
            branchingButtonGroup.add(getBranchingNoneRadioButton());
            branchingButtonGroup.add(getBranchingAndSplitRadioButton());
            branchingButtonGroup.add(getBranchingAndJoinRadioButton());
            branchingButtonGroup.add(getBranchingXorSplitRadioButton());
            branchingButtonGroup.add(getBranchingXorJoinRadioButton());

            // c.weightx = 1;
            c.gridx = 0;
            c.gridy = 0;
            branchingPanel.add(getBranchingNoneRadioButton(), c);
            c.gridx = 1;
            c.gridy = 0;
            branchingPanel.add(getBranchingAndJoinRadioButton(), c);
            c.gridx = 1;
            c.gridy = 1;
            branchingPanel.add(getBranchingAndSplitRadioButton(), c);
            c.gridx = 2;
            c.gridy = 0;
            branchingPanel.add(getBranchingXorSplitRadioButton(), c);
            c.gridx = 2;
            c.gridy = 1;
            branchingPanel.add(getBranchingXorJoinRadioButton(), c);

        }
        return branchingPanel;
    }

    private JRadioButton getBranchingNoneRadioButton()
    {
        if (branchingNoneRadioButton == null)
        {
            branchingNoneRadioButton = new JRadioButton(Messages.getString("Transition.Resources.Branching.None"));
            // TODO:
            branchingNoneRadioButton.setEnabled(false);
        }
        return branchingNoneRadioButton;
    }

    private JRadioButton getBranchingAndJoinRadioButton()
    {
        if (branchingAndJoinRadioButton == null)
        {
            branchingAndJoinRadioButton = new JRadioButton(Messages.getString("Transition.Resources.Branching.AndJoin"));
            // TODO:
            branchingAndJoinRadioButton.setEnabled(false);
        }
        return branchingAndJoinRadioButton;
    }

    private JRadioButton getBranchingAndSplitRadioButton()
    {
        if (branchingAndSplittRadioButton == null)
        {
            branchingAndSplittRadioButton = new JRadioButton(Messages.getString("Transition.Resources.Branching.AndSplit"));
            // TODO:
            branchingAndSplittRadioButton.setEnabled(false);
        }
        return branchingAndSplittRadioButton;
    }

    private JRadioButton getBranchingXorSplitRadioButton()
    {
        if (branchingXorSplittRadioButton == null)
        {
            branchingXorSplittRadioButton = new JRadioButton(Messages.getString("Transition.Resources.Branching.XorSplit"));
            // TODO:
            branchingXorSplittRadioButton.setEnabled(false);
        }
        return branchingXorSplittRadioButton;
    }

    private JRadioButton getBranchingXorJoinRadioButton()
    {
        if (branchingXorJoinRadioButton == null)
        {
            branchingXorJoinRadioButton = new JRadioButton(Messages.getString("Transition.Resources.Branching.XorJoin"));
            // TODO:
            branchingXorJoinRadioButton.setEnabled(false);
        }
        return branchingXorJoinRadioButton;
    }

    // *********************************DurationPanel*****************************************************
    private JPanel getDurationPanel()
    {
        if (durationPanel == null)
        {
            durationPanel = new JPanel();
            durationPanel.setLayout(new GridBagLayout());
            durationPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(Messages.getString("Transition.Resources.Duration")), BorderFactory.createEmptyBorder(5, 5, 10, 5)));
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;

            c.weightx = 1;
            c.gridx = 0;
            c.gridy = 0;
            durationPanel.add(getDurationTextfield(), c);
            c.weightx = 0;
            c.gridx = 1;
            c.gridy = 0;
            durationPanel.add(getDurationComboBox(), c);
        }

        return durationPanel;
    }

    private JTextField getDurationTextfield()
    {
        if (durationTextField == null)
        {
            durationTextField = new JTextField();
            durationTextField.setText("");
            durationTextField.setEnabled(false);
        }
        return durationTextField;
    }

    private JComboBox getDurationComboBox()
    {
        if (durationComboBox == null)
        {
            durationComboBox = new JComboBox(durationComboBoxA);
            durationComboBox.setMinimumSize(new Dimension(70, 20));
            durationComboBox.setEnabled(false);
        }
        return durationComboBox;
    }

    // **********************************************ResourcePanel******************************************************
    private JPanel getResourcePanel()
    {
        if (resourcePanel == null)
        {
            resourcePanel = new JPanel();
            resourcePanel.setLayout(new GridBagLayout());
            resourcePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(Messages.getString("Transition.Resources.Resources")), BorderFactory.createEmptyBorder(5, 5, 10, 5)));
            // resourcePanel.setMinimumSize(new Dimension(345, 40));
            // resourcePanel.setPreferredSize(new Dimension(345, 40));
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.WEST;

            // c.weightx = 1;
            c.gridx = 0;
            c.gridy = 0;
            resourcePanel.add(getResourceRoleLabel(), c);

            c.gridx = 1;
            c.gridy = 0;
            resourcePanel.add(getResourceRoleComboBox(), c);

            c.gridx = 2;
            c.gridy = 0;
            resourcePanel.add(getResourceOrgUnitLabel(), c);

            c.gridx = 3;
            c.gridy = 0;
            resourcePanel.add(getResourceOrgUnitComboBox(), c);

        }
        return resourcePanel;
    }

    private JLabel getResourceRoleLabel()
    {
        if (resourceRoleLabel == null)
        {
            resourceRoleLabel = new JLabel(" " + Messages.getString("Transition.Resources.Role")+ ": ");
        }
        return resourceRoleLabel;
    }

    private JLabel getResourceOrgUnitLabel()
    {
        if (resourceOrgUnitLabel == null)
        {
            resourceOrgUnitLabel = new JLabel(" " + Messages.getString("Transition.Resources.Group")+ ": ");
        }
        return resourceOrgUnitLabel;
    }

    private DefaultComboBoxModel getRoleComboxBoxModel()
    {
        if (roleComboBoxModel == null)
        {
            if (((PetriNetModelProcessor) getEditor().getModelProcessor()).getRoles() != null)
            {
                roleComboBoxModel = new DefaultComboBoxModel();
                roleComboBoxModel.addElement(TRIGGER_NONE);
                for (Iterator iter = ((PetriNetModelProcessor) getEditor().getModelProcessor()).getRoles().iterator(); iter.hasNext();)
                {
                    roleComboBoxModel.addElement(iter.next());
                }
                if (!transition.hasResource())
                {
                    roleComboBoxModel.setSelectedItem(TRIGGER_NONE);
                } else
                {
                    String transRole = transition.getToolSpecific().getTransResource().getTransRoleName();

                    roleComboBoxModel.setSelectedItem(transRole);
                }

            }
        }
        return roleComboBoxModel;
    }

    private DefaultComboBoxModel getOrgUnitComboxBoxModel()
    {
        if (orgUnitComboBoxModel == null)
        {
            if (((PetriNetModelProcessor) getEditor().getModelProcessor()).getOrganizationUnits() != null)
            {
                orgUnitComboBoxModel = new DefaultComboBoxModel();
                orgUnitComboBoxModel.addElement(TRIGGER_NONE);
                for (Iterator iter = ((PetriNetModelProcessor) getEditor().getModelProcessor()).getOrganizationUnits().iterator(); iter.hasNext();)
                {
                    orgUnitComboBoxModel.addElement(iter.next());
                }
                if (!transition.hasResource())
                {
                    orgUnitComboBoxModel.setSelectedItem(TRIGGER_NONE);
                } else
                {
                    String transOrgUnit = transition.getToolSpecific().getTransResource().getTransOrgUnitName();

                    orgUnitComboBoxModel.setSelectedItem(transOrgUnit);
                }

            }
        }
        return orgUnitComboBoxModel;
    }

    private JComboBox getResourceRoleComboBox()
    {
        if (resourceRoleComboBox == null)
        {
            resourceRoleComboBox = new JComboBox(getRoleComboxBoxModel());
            resourceRoleComboBox.setMinimumSize(new Dimension(90, 20));

        }
        return resourceRoleComboBox;
    }

    private JComboBox getResourceOrgUnitComboBox()
    {
        if (resourceOrgUnitComboBox == null)
        {
            resourceOrgUnitComboBox = new JComboBox(getOrgUnitComboxBoxModel());
            resourceOrgUnitComboBox.setMinimumSize(new Dimension(90, 20));
        }
        return resourceOrgUnitComboBox;
    }

    // *****************************************************ButtonPanel****************************************************
    private JPanel getButtonPanel()
    {
        if (buttonPanel == null)
        {
            buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(getButtonOk());
            buttonPanel.add(getButtonCancel());
            buttonPanel.add(getButtonApply());
        }
        return buttonPanel;
    }

    private JButton getButtonOk()
    {
        if (buttonOk == null)
        {
            buttonOk = new JButton();
            buttonOk.setText(Messages.getString("Button.OK.Title"));
            buttonOk.setMnemonic(KeyEvent.VK_O);
            buttonOk.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    apply();
                    TransitionPropertyEditor.this.dispose();
                }

            });

        }
        return buttonOk;
    }

    private JButton getButtonCancel()
    {
        if (buttonCancel == null)
        {
            buttonCancel = new JButton(new DisposeWindowAction());
            buttonCancel.setIcon(null);
            buttonCancel.setMnemonic(KeyEvent.VK_C);
            buttonCancel.setText(Messages.getString("Button.Cancel.Title"));
        }
        return buttonCancel;
    }

    private JButton getButtonApply()
    {
        if (buttonApply == null)
        {
            buttonApply = new JButton();
            buttonApply.setMnemonic(KeyEvent.VK_A);
            buttonApply.setText(Messages.getString("Button.Apply.Title"));
            buttonApply.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    apply();
                }
            });

        }
        return buttonApply;
    }

    // public static void main(String[] args)
    // {
    // TransitionPropertyEditor p = new TransitionPropertyEditor();
    // JFrame f = new JFrame();
    // f.getContentPane().add(p);
    // f.setSize(350, 350);
    // f.setVisible(true);
    // }

    private void apply()
    {
        // Trigger Handling
        int selectedTriggerType = -1;
        if (getTriggerNoneRadioButton().isSelected())
        {
            if (transition.hasTrigger())
            {
                getEditor().deleteCell(transition.getToolSpecific().getTrigger(), true);
            }
        } else if (getTriggerResourceRadioButton().isSelected())
        {
            selectedTriggerType = TriggerModel.TRIGGER_RESOURCE;
        } else if (getTriggerTimeRadioButton().isSelected())
        {
            selectedTriggerType = TriggerModel.TRIGGER_TIME;
        } else if (getTriggerMessageRadioButton().isSelected())
        {
            selectedTriggerType = TriggerModel.TRIGGER_EXTERNAL;
        }
        // Rescource Handling
        if (selectedTriggerType == TriggerModel.TRIGGER_RESOURCE)
        {
            String selectedRole = getRoleComboxBoxModel().getSelectedItem().toString();
            String selectedOrgUnit = getOrgUnitComboxBoxModel().getSelectedItem().toString();
            if (!selectedRole.equals(TRIGGER_NONE) && !selectedOrgUnit.equals(TRIGGER_NONE))
            {
                CreationMap map = transition.getCreationMap();
                map.setResourceOrgUnit(selectedOrgUnit);
                map.setResourceRole(selectedRole);
                if (transition.hasResource())
                {
                    map.setResourcePosition(transition.getToolSpecific().getTransResource().getPosition());
                }
                getEditor().createTransitionResource(map);
            } else
            {
                getEditor().deleteCell(transition.getToolSpecific().getTransResource(), true);
            }
        } else
        {
            getEditor().deleteCell(transition.getToolSpecific().getTransResource(), true);
        }
        // trigger Handling
        CreationMap map = transition.getCreationMap();
        map.setTriggerType(selectedTriggerType);
        if (transition.hasTrigger())
        {
            if (transition.getToolSpecific().getTrigger().getTriggertype() != selectedTriggerType)
            {
                Point p = transition.getToolSpecific().getTrigger().getPosition();
                getEditor().deleteCell(transition.getToolSpecific().getTrigger(), true);
                map.setTriggerPosition(p.x, p.y);
                getEditor().createTrigger(map);
            }

        } else if (selectedTriggerType != -1)
        {
            getEditor().createTrigger(map);
        }
        // name changing handling
        transition.setNameValue(getNameTextField().getText());
    } // editor.createTransitionResource()

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals(TRIGGER_MESSAGE) || e.getActionCommand().equals(TRIGGER_TIME) || e.getActionCommand().equals(TRIGGER_NONE))
        {
            getResourceRoleComboBox().setEnabled(false);
            getResourceOrgUnitComboBox().setEnabled(false);
        } else if (e.getActionCommand().equals(TRIGGER_RESOURCE))
        {
            getResourceRoleComboBox().setEnabled(true);
            getResourceOrgUnitComboBox().setEnabled(true);
        }

    }

    /**
     * @return Returns the editor.
     */
    public EditorVC getEditor()
    {
        return editor;
    }
}