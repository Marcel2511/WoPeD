package org.woped.qualanalysis.coverabilitygraph.assistant;

import org.woped.core.controller.IEditor;
import org.woped.qualanalysis.coverabilitygraph.assistant.algorithms.mp.CoverabilityGraphAssistantUsingMonotonePruning;
import org.woped.qualanalysis.coverabilitygraph.assistant.sidebar.SidebarVC;
import org.woped.qualanalysis.coverabilitygraph.model.CoverabilityGraphModel;
import org.woped.qualanalysis.coverabilitygraph.events.CoverabilityGraphMouseListener;
import org.woped.qualanalysis.coverabilitygraph.gui.*;
import org.woped.qualanalysis.coverabilitygraph.gui.layout.CoverabilityGraphLayoutSettings;
import org.woped.qualanalysis.coverabilitygraph.gui.views.CoverabilityGraphView;
import org.woped.qualanalysis.coverabilitygraph.gui.views.CoverabilityGraphWrapper;
import org.woped.qualanalysis.coverabilitygraph.gui.views.CoverabilityGraphViewFactory;
import org.woped.qualanalysis.coverabilitygraph.gui.views.formatters.NodeFormatter;

import javax.swing.*;
import java.awt.*;

/**
 * This class should be used to display a coverability graph assistant in a {@link CoverabilityGraphVC} to provide a consistent look & feel.
 */
public class CoverabilityGraphAssistantView extends CoverabilityGraphView {

    public static final String VIEW_NAME = "AssistantView";

    private IEditor editor;
    private CoverabilityGraphAssistantModel graphModel;
    private PetriNetHighlighter netHighlighter;
    private SidebarVC sidebarVC;
    private CoverabilityGraphWrapper graphView;
    private NodeFormatter nodeFormatter;
    private CoverabilityGraphAssistant assistant;

    /**
     * Constructs a new coverability graph assistant view for the petri net designed in the provided editor.
     *
     * @param editor the editor containing the underlying petri net
     */
    public CoverabilityGraphAssistantView(IEditor editor) {
        super();
        this.editor = editor;

        this.initialize();
    }

    /**
     * Refreshes the view.
     */
    @Override
    public void refresh() {
        graphModel.refresh();
        graphView.refresh();

        validate();
        repaint();
    }

    /**
     * Resets the view to its initial state.
     */
    @Override
    public void reset() {
        assistant.reset();
        refresh();
    }

    /**
     * Notifies the view that it is out of sync with the petri net.
     */
    @Override
    public void setOutOfSync() {

    }

    /**
     * Gets the current settings of the view.
     *
     * @return the current settings of the view
     */
    @Override
    public CoverabilityGraphSettings getSettings() {

        CoverabilityGraphSettings settings  = new CoverabilityGraphSettings();

        settings.markingFormatter = nodeFormatter.getNodeTextFormatter().getMarkingFormatter();
        settings.colorSchemeSupported = false;

        CoverabilityGraphLayoutSettings layoutSettings = graphModel.getLayoutSettings();
        settings.layout = layoutSettings.layout;
        settings.edgeRouting = layoutSettings.edgeRouting;
        settings.minNodeSize = layoutSettings.minNodeSize;
        settings.horizontalGap = layoutSettings.horizontalGap;
        settings.verticalGap = layoutSettings.verticalGap;

        return settings;
    }

    /**
     * Applies the provided settings to the view.
     *
     * @param settings the new settings
     */
    @Override
    public void applySettings(CoverabilityGraphSettings settings) {
        nodeFormatter.getNodeTextFormatter().setMarkingFormatter(settings.markingFormatter);

        CoverabilityGraphLayoutSettings layoutSettings = graphModel.getLayoutSettings();
        layoutSettings.layout =settings.layout;
        layoutSettings.edgeRouting = settings.edgeRouting;
        layoutSettings.minNodeSize = settings.minNodeSize;
        layoutSettings.horizontalGap = settings.horizontalGap;
        layoutSettings.verticalGap = settings.verticalGap;

        refresh();
    }

    /**
     * Gets the zoom controller of the view.
     *
     * @return the zoom controller of the view
     */
    @Override
    public ZoomController getZoomController() {
        return graphView.getZoomController();
    }

    /**
     * Gets the graph model of the view.
     *
     * @return the graph model of the view
     */
    @Override
    public CoverabilityGraphModel getGraphModel() {
        return graphModel;
    }

    /**
     * Removes all highlighting from the view.
     */
    @Override
    public void removeHighlighting() {
        netHighlighter.removeHighlighting();
        assistant.deselect();
    }

    /**
     * Adds a listener that is interested in mouse events from the graph.
     *
     * @param listener the listener to add
     */
    @Override
    public void addCoverabilityGraphMouseListener(CoverabilityGraphMouseListener listener) {
        graphView.addCoverabilityGraphMouseListener(listener);
    }

    @Override
    public int hashCode() {
        return editor.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        if (!(obj instanceof CoverabilityGraphAssistantView)) return false;
        CoverabilityGraphAssistantView other = (CoverabilityGraphAssistantView) obj;

        return other.editor.equals(this.editor);
    }

    private void initialize() {
        sidebarVC = new SidebarVC();

        CoverabilityGraphViewFactory viewFactory = new CoverabilityGraphViewFactory();
        nodeFormatter = viewFactory.getNodeFormatter();
        graphModel = new CoverabilityGraphAssistantModel(editor, viewFactory);
        assistant = new CoverabilityGraphAssistantUsingMonotonePruning(editor, sidebarVC, graphModel);

        createView();

        this.netHighlighter = new PetriNetHighlighter(editor);
        graphView.addCoverabilityGraphMouseListener(netHighlighter.getMouseListener());
        graphView.addCoverabilityGraphMouseListener(assistant.getMouseListener());
    }

    private void createView(){

        JSplitPane splitter = new JSplitPane();
        splitter.setOneTouchExpandable(true);
        splitter.setResizeWeight(1);

        graphView = new CoverabilityGraphWrapper(graphModel.getGraph());
        splitter.setLeftComponent(graphView);
        splitter.setRightComponent(sidebarVC.getView());

        this.setMinimumSize(new Dimension(640, 480));
        this.setLayout(new BorderLayout());
        this.add(splitter, BorderLayout.CENTER);
    }

}
