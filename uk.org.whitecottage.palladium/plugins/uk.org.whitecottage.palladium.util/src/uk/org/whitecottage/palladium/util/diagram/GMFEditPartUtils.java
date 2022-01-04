package uk.org.whitecottage.palladium.util.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.render.util.CopyToImageUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;

public class GMFEditPartUtils {
	public static List<?> getEditParts(List<EObject> visibleElements,
			DiagramEditPart diagramEditPart) {
		return getEditParts(visibleElements, diagramEditPart, true);
	}

	public static List<?> getEditParts(List<EObject> visibleElements,
			DiagramEditPart diagramEditPart, boolean includeConnections) {
		List<GraphicalEditPart> result = new LinkedList<GraphicalEditPart>();
		for (EObject e : visibleElements) {
			Object model = diagramEditPart.getModel();
			if (model instanceof Diagram) {
				Diagram diagram = (Diagram) model;
				for (TreeIterator<EObject> i = EcoreUtil.getAllProperContents(
						diagram, true); i.hasNext();) {
					EObject current = i.next();
					if (current instanceof View) {
						View view = (View) current;
						if (equals(e, view.getElement())) {
							Object part = diagramEditPart.getViewer()
									.getEditPartRegistry().get(view);
							if (part instanceof GraphicalEditPart) {
								result.add((GraphicalEditPart) part);
							}
						}
					}
				}
			}
		}
		if (includeConnections)
		{
			// the process is made twice but copying the code 
			// in this case is better than overriding to avoid maintenance problem
			ArrayList<GraphicalEditPart> tmp = new ArrayList<GraphicalEditPart>(result);
			for(GraphicalEditPart g : tmp)
			{
				result.addAll(findConnectionsToPaint(g, result));
			}
		}
		return result;
	}

	protected static boolean equals(EObject e, EObject fromView) {
		boolean result = false ;
		if (fromView == e)
		{
			result = true ;
		}
		else
		{
			// if the diagram editor is the opened one the eobjects are not the same
			if (e.eResource() != null && fromView.eResource() != null)
			{
				Resource eResource =  e.eResource();
				Resource viewResoure =  fromView.eResource();
				if (eResource.getURI() != null && eResource.getURI().equals(viewResoure.getURI()))
				{
					result = (eResource.getURIFragment(e) != null && eResource.getURIFragment(e).equals(viewResoure.getURIFragment(fromView)));
				}
			}
		}
		return result ;
	}

	/**
	 * Collects all connections contained within the given edit part
	 * Code copy from {@link CopyToImageUtil}
	 * @param editPart
	 *            the container editpart
	 * @return connections within it
	 */
	protected static Collection<ConnectionEditPart> findConnectionsToPaint(
			GraphicalEditPart editPart, List<GraphicalEditPart> relatedEditParts) {
		/*
		 * Set of node editparts contained within the given editpart
		 */
		HashSet<GraphicalEditPart> editParts = new HashSet<GraphicalEditPart>();

		/*
		 * All connection editparts that have a source contained within the
		 * given editpart
		 */
		HashSet<ConnectionEditPart> connectionEPs = new HashSet<ConnectionEditPart>();

		/*
		 * Connections contained within the given editpart (or just the
		 * connections to paint
		 */
		HashSet<ConnectionEditPart> connectionsToPaint = new HashSet<ConnectionEditPart>();

		/*
		 * Populate the set of node editparts
		 */
		getNestedEditParts(editPart, editParts);

		/*
		 * Populate the set of connections whose source is within the given
		 * editpart
		 */
		for (GraphicalEditPart gep : editParts) {
			connectionEPs.addAll(getAllConnectionsFrom(gep));
		}

		/*
		 * Populate the set of connections whose source is the given editpart
		 */
		connectionEPs.addAll(getAllConnectionsFrom(editPart));

		/*
		 * Create a set of connections constained within the given editpart
		 */
		while (!connectionEPs.isEmpty()) {
			/*
			 * Take the first connection and check whethe there is a path
			 * through that connection that leads to the target contained within
			 * the given editpart
			 */
			Stack<ConnectionEditPart> connectionsPath = new Stack<ConnectionEditPart>();
			ConnectionEditPart conn = connectionEPs.iterator().next();
			connectionEPs.remove(conn);
			connectionsPath.add(conn);

			/*
			 * Initialize the target for the current path
			 */
			EditPart target = conn.getTarget();
			while (connectionEPs.contains(target)) {
				/*
				 * If the target end is a connection, check if it's one of the
				 * connection's whose target is a connection and within the
				 * given editpart. Append it to the path if it is. Otherwise
				 * check if the target is within the actual connections or nodes
				 * contained within the given editpart
				 */
				ConnectionEditPart targetConn = (ConnectionEditPart) target;
				connectionEPs.remove(targetConn);
				connectionsPath.add(targetConn);

				/*
				 * Update the target for the new path
				 */
				target = targetConn.getTarget();
			}

			/*
			 * The path is built, check if it's target is a node or a connection
			 * contained within the given editpart
			 */
			if (editParts.contains(target)
					|| connectionsToPaint.contains(target)
					|| relatedEditParts.contains(target)) {
				connectionsToPaint.addAll(connectionsPath);
			}
		}
		return connectionsToPaint;
	}

	/**
	 * This method is used to obtain the list of child edit parts for shape
	 * compartments.
	 * 
	 * @param childEditPart
	 *            base edit part to get the list of children editparts
	 * @param editParts
	 *            list of nested shape edit parts
	 */
	protected static void getNestedEditParts(GraphicalEditPart baseEditPart,
			Collection<GraphicalEditPart> editParts) {

		for (Object child : baseEditPart.getChildren()) {
			if (child instanceof GraphicalEditPart) {
				GraphicalEditPart childEP = (GraphicalEditPart) child;
				editParts.add(childEP);
				getNestedEditParts(childEP, editParts);
			}
		}
	}

	/**
	 * Returns all connections orginating from a given editpart. All means that
	 * connections originating from connections that have a source given
	 * editpart will be included
	 * 
	 * @param ep
	 *            the editpart
	 * @return all source connections
	 */
	protected static List<ConnectionEditPart> getAllConnectionsFrom(
			GraphicalEditPart ep) {
		LinkedList<ConnectionEditPart> connections = new LinkedList<ConnectionEditPart>();
		for (Object sourceConnObj : ep.getSourceConnections()) {
			if (sourceConnObj instanceof ConnectionEditPart) {
				ConnectionEditPart sourceConn = (ConnectionEditPart) sourceConnObj;
				connections.add(sourceConn);
				connections.addAll(getAllConnectionsFrom(sourceConn));
			}
		}
		return connections;
	}
}
