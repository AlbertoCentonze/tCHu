package ch.epfl.tchu.gui;

import javafx.scene.layout.VBox;

class InfoViewCreator {
    // non-instantiable class
    private InfoViewCreator() {
        throw new UnsupportedOperationException(); //TODO to this to all non-instantiable classes
    }

    public static VBox createInfoView() {
        return new VBox(); // TODO
    }
}
