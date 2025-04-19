package org.jackhuang.hmcl.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;

import javax.swing.*;

public class FXUtils {
    public static void runInFX(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    public static InvalidationListener onInvalidating(Runnable action) {
        return arg -> action.run();
    }

    public static InvalidationListener observeWeak(Runnable runnable, Observable... observables) {
        InvalidationListener originalListener = observable -> runnable.run();
        WeakInvalidationListener listener = new WeakInvalidationListener(originalListener);
        for (Observable observable : observables) {
            observable.addListener(listener);
        }
        runnable.run();
        return originalListener;
    }
}
