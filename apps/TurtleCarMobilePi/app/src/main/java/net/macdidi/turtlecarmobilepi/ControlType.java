package net.macdidi.turtlecarmobilepi;

public enum ControlType {
    FORWARD('F'), BACKWARD('B'), LEFT('L'), RIGHT('R'), STOP('S');

    private char code;

    private ControlType(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }
}
