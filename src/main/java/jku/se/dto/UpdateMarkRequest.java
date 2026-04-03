package jku.se.dto;

import jku.se.entity.MARK;

public class UpdateMarkRequest {

    private MARK mark;

    public UpdateMarkRequest() {
    }

    public UpdateMarkRequest(MARK mark) {
        this.mark = mark;
    }

    public MARK getMark() {
        return mark;
    }

    public void setMark(MARK mark) {
        this.mark = mark;
    }
}