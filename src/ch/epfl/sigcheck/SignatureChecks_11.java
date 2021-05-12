package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_11 {
    private SignatureChecks_11() {}

    void checkClientMain() {
        v01 = new ch.epfl.tchu.gui.ClientMain();
        ch.epfl.tchu.gui.ClientMain.main(v02);
        v01.start(v03);
    }

    void checkServerMain() {
        v04 = new ch.epfl.tchu.gui.ServerMain();
        ch.epfl.tchu.gui.ServerMain.main(v02);
        v04.start(v03);
    }

    ch.epfl.tchu.gui.ClientMain v01;
    java.lang.String[] v02;
    javafx.stage.Stage v03;
    ch.epfl.tchu.gui.ServerMain v04;
}
