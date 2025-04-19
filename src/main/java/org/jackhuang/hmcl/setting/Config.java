package org.jackhuang.hmcl.setting;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Config {
    public BooleanProperty autoDownloadThreadsProperty() {
        return new SimpleBooleanProperty(true);
    }

    public boolean getAutoDownloadThreads() {
        return true;
    }

    public boolean isAutoChooseDownloadType() {
        return false;
    }

    public IntegerProperty downloadThreadsProperty() {
        return new SimpleIntegerProperty(Integer.parseInt(com.ciluu.smcl.Settings.getDownloadThreads()));
    }
}
