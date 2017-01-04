package org.itracker.persistence.dao;

import org.springframework.dao.DataAccessException;

public class NoSuchEntityException extends DataAccessException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NoSuchEntityException(String msg) {
        super(msg);
    }

    public NoSuchEntityException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
