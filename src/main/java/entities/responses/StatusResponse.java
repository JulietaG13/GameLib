package entities.responses;

import interfaces.Responses;

public class StatusResponse implements Responses {
    private int status;
    private Responses response;

    public StatusResponse(int status, Responses response) {
        this.status = status;
        this.response = response;
    }

    public StatusResponse(int status) {
        this.status = status;
        this.response = null;
    }

    @Override
    public Responses getResponse() {
        return response;
    }

    @Override
    public Integer getStatusCode() {
        return status;
    }
}
