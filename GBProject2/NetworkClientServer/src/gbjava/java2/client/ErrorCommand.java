package gbjava.java2.client;

import java.io.Serializable;

public class ErrorCommand implements Serializable {
    private final String error;

    public ErrorCommand(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "ErrorCommand{" +
                "error='" + error + '\'' +
                '}';
    }
}
