package com.spiddekauga.android.ui;

import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.AppFragment;
import com.spiddekauga.utils.EventBus;
import com.squareup.otto.Subscribe;

/**
 * Some helper methods for creating simple snackbars
 */
public class SnackbarHelper {
@Snackbar.Duration
private static final int DURATION_SHORT = Snackbar.LENGTH_SHORT;
@Snackbar.Duration
private static final int DURATION_MEDIUM = Snackbar.LENGTH_LONG;
private static final int LENGTH_MEDIUM_CHARACTERS = 0;
@Snackbar.Duration
private static final int DURATION_LONG = 6000;
private static final int LENGTH_LONG_CHARACTERS = 20;

/**
 * Can't instantiate the class
 */
private SnackbarHelper() {

}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message
 * @param stringId id of the message to show
 */
public static void showSnackbar(@StringRes int stringId) {
	showSnackbar(getString(stringId));
}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message
 * @param message the message to show
 */
public static void showSnackbar(String message) {
	showSnackbar(message, null, null);
}

private static String getString(@StringRes int stringId) {
	return AppActivity.getActivity().getResources().getString(stringId);
}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message and an action
 * @param message the message to show
 * @param actionTitle button title
 * @param action the action to take
 */
public static void showSnackbar(String message, String actionTitle, View.OnClickListener action) {
	int duration = calculateDuration(message);
	SnackbarMessage snackbarMessage = new SnackbarMessage(message, actionTitle, action, duration);
	snackbarMessage.show();
}

private static int calculateDuration(String message) {
	if (message.length() > LENGTH_LONG_CHARACTERS) {
		return DURATION_LONG;
	} else if (message.length() > LENGTH_MEDIUM_CHARACTERS) {
		return DURATION_MEDIUM;
	} else {
		return DURATION_SHORT;
	}
}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message
 * @param stringId id of the message to show
 * @param actionTitleId button title as a string resource id
 * @param action the action to take
 */
public static void showSnackbar(@StringRes int stringId, @StringRes int actionTitleId, View.OnClickListener action) {
	showSnackbar(getString(stringId), getString(actionTitleId), action);
}

/**
 * Container for snackbar messages
 */
private static class SnackbarMessage {
	private static final EventBus mEventBus = EventBus.getInstance();
	private static SnackbarMessage mLastMessage = null;
	private String mMessage;
	private String mActionTitle;
	private int mDuration;
	private View.OnClickListener mAction;
	private Snackbar mSnackbar;

	SnackbarMessage(String message, String actionTitle, View.OnClickListener action, int duration) {
		mMessage = message;
		mActionTitle = actionTitle;
		mAction = action;
		mDuration = duration;
	}

	@Subscribe
	public void onFragmentResume(AppFragment.FragmentResumeEvent event) {
		mEventBus.unregister(this);
		if (isShown() && this == mLastMessage) {
			show();
		}
	}

	private boolean isShown() {
		if (mSnackbar != null) {
			return mSnackbar.isShownOrQueued();
		} else {
			return false;
		}
	}

	void show() {
		final View view = getView();
		mSnackbar = Snackbar.make(view, mMessage, mDuration);
		if (mActionTitle != null && mAction != null) {
			mSnackbar.setAction(mActionTitle, mAction);
		}
		mSnackbar.setCallback(new Snackbar.Callback() {
			@Override
			public void onDismissed(Snackbar snackbar, int event) {
				if (view instanceof FloatingActionButton) {
					fixFloatingActionButtonPosition((FloatingActionButton) view);
				}
			}
		});
		mSnackbar.show();
		mEventBus.register(this);
		mLastMessage = this;
	}

	private View getView() {
		View rootView = AppActivity.getRootView();
		if (rootView instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) rootView;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				View child = viewGroup.getChildAt(i);
				if (child instanceof FloatingActionButton) {
					return child;
				}
			}
		}
		return rootView;
	}

	private void fixFloatingActionButtonPosition(FloatingActionButton button) {
		button.setTranslationY(0);
	}
}
}
