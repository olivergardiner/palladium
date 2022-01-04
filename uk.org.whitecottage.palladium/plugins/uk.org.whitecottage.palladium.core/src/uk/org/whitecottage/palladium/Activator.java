package uk.org.whitecottage.palladium;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "uk.org.whitecottage.palladium";
    
    private static Activator plugin;

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		plugin = this;
		//CreationMenuCleaner.clean();
		//ContextConfigurator.disableContext(Context.UML);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		super.stop(bundleContext);
		plugin = null;
	}

    public static Activator getDefault() {
        return plugin;
    }
    
	/**
	 * @return the id of the plugin
	 */
	public static String getPluginId() {
		return getDefault().getBundle().getSymbolicName();
	}

	/**
	 * Creates an {@link IStatus} from the parameters. If the throwable is an
	 * {@link InvocationTargetException}, the status is created from the first
	 * exception that is either not an InvocationTargetException or that has a
	 * message. If the message passed is empty, tries to supply a message from
	 * that exception.
	 *
	 * @param severity
	 *            of the {@link IStatus}
	 * @param message
	 *            for the status
	 * @param throwable
	 *            that caused the status, may be {@code null}
	 * @return the status
	 */
	private static IStatus toStatus(int severity, String message,
			Throwable throwable) {
		Throwable exc = throwable;
		while (exc instanceof InvocationTargetException) {
			String msg = exc.getLocalizedMessage();
			if (msg != null && !msg.isEmpty()) {
				break;
			}
			Throwable cause = exc.getCause();
			if (cause == null) {
				break;
			}
			exc = cause;
		}
		if (exc != null && (message == null || message.isEmpty())) {
			message = exc.getLocalizedMessage();
		}
		return new Status(severity, getPluginId(), message, exc);
	}

	/**
	 * Handle an error. The error is logged. If <code>show</code> is
	 * <code>true</code> the error is shown to the user.
	 *
	 * @param message
	 *            a localized message
	 * @param throwable
	 * @param show
	 */
	public static void handleError(String message, Throwable throwable,
			boolean show) {
		handleIssue(IStatus.ERROR, message, throwable, show);
	}

	/**
	 * Handle an issue. The issue is logged. If <code>show</code> is
	 * <code>true</code> the issue is shown to the user.
	 *
	 * @param severity
	 *            status severity, use constants defined in {@link IStatus}
	 * @param message
	 *            a localized message
	 * @param throwable
	 * @param show
	 * @since 2.2
	 */
	public static void handleIssue(int severity, String message, Throwable throwable,
			boolean show) {
		IStatus status = toStatus(severity, message, throwable);
		handleStatus(status, show);
	}

	/**
	 * Handle a status. The status is logged. If <code>show</code> is
	 * <code>true</code> the issue is shown to the user.
	 *
	 * @param status
	 *            Status to be handled.
	 *
	 * @param show
	 *            Whether or not to show the issue to the user.
	 * @since 5.2
	 */
	public static void handleStatus(IStatus status, boolean show) {
		int style = StatusManager.LOG;
		if (show) {
			style |= StatusManager.SHOW;
		}
		StatusManager.getManager().handle(status, style);
	}

	/**
	 * Shows an error. The error is NOT logged.
	 *
	 * @param message
	 *            a localized message
	 * @param throwable
	 */
	public static void showError(String message, Throwable throwable) {
		IStatus status = toStatus(IStatus.ERROR, message, throwable);
		StatusManager.getManager().handle(status, StatusManager.SHOW);
	}

	/**
	 * Shows an error. The error is NOT logged.
	 *
	 * @param message
	 *            a localized message
	 * @param status
	 */
	public static void showErrorStatus(String message, IStatus status) {
		StatusManager.getManager().handle(status, StatusManager.SHOW);
	}

	/**
	 * @param message
	 * @param e
	 */
	public static void logError(String message, Throwable e) {
		handleError(message, e, false);
	}

	/**
	 * Utility method to log warnings for this plug-in.
	 *
	 * @param message
	 *            User comprehensible message
	 * @param thr
	 *            The exception through which we noticed the warning
	 */
	public static void logWarning(final String message, final Throwable thr) {
		handleIssue(IStatus.WARNING, message, thr, false);
	}

	/**
	 * @param message
	 * @param e
	 */
	public static void error(String message, Throwable e) {
		handleError(message, e, false);
	}

	/**
	 * Creates an error status
	 *
	 * @param message
	 *            a localized message
	 * @param throwable
	 * @return a new Status object
	 */
	public static IStatus createErrorStatus(String message,
			Throwable throwable) {
		return toStatus(IStatus.ERROR, message, throwable);
	}

	/**
	 * Creates an error status
	 *
	 * @param message
	 *            a localized message
	 * @return a new Status object
	 */
	public static IStatus createErrorStatus(String message) {
		return toStatus(IStatus.ERROR, message, null);
	}

	public static void logInfo(final String message) {
		handleIssue(IStatus.INFO, message, null, false);
	}

}
