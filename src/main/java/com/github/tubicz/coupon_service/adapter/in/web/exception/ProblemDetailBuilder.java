package com.github.tubicz.coupon_service.adapter.in.web.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.time.Instant;
import java.util.Map;

class ProblemDetailBuilder {
    private final ProblemDetail problem;

    public ProblemDetailBuilder(HttpStatusCode status) {
        this.problem = ProblemDetail.forStatus(status);
        this.problem.setProperty("timestamp", Instant.now());
    }

    public ProblemDetailBuilder title(String title) {
        problem.setTitle(title);
        return this;
    }

    public ProblemDetailBuilder detail(String detail) {
        problem.setDetail(detail);
        return this;
    }

    public ProblemDetailBuilder errorCode(ErrorCode errorCode) {
        if (errorCode != null) {
            problem.setProperty("errorCode", errorCode);
        }
        return this;
    }

    public ProblemDetailBuilder errors(Map<String, String> errors) {
        if (errors != null && !errors.isEmpty()) {
            problem.setProperty("errors", errors);
        }
        return this;
    }

    public ProblemDetail build() {
        return problem;
    }
}
