package org.jackhuang.hmcl.setting;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.LinkedHashSet;

public class GlobalConfig{
    public ObservableSet<String> getDisabledJava() {
        return FXCollections.observableSet(new LinkedHashSet<>());
    }

    public ObservableSet<String> getUserJava() {
        return FXCollections.observableSet(new LinkedHashSet<>());
    }
}
