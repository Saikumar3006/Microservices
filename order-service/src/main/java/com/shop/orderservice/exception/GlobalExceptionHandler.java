package com.shop.orderservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Translates exceptions into HTTP responses in one place, so controllers stay
 * free of try/catch. Uses ProblemDetail (RFC 9457), the standard error shape
 * Spring returns by default.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Real product, no stock. A business answer, so 409 Conflict. */
    @ExceptionHandler(OutOfStockException.class)
    public ProblemDetail handleOutOfStock(OutOfStockException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Out of stock");
        problem.setProperty("skuCodes", ex.getSkuCodes());
        return problem;
    }

    /** No such product. The request itself is wrong, so 400. */
    @ExceptionHandler(UnknownSkuException.class)
    public ProblemDetail handleUnknownSku(UnknownSkuException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Unknown SKU");
        problem.setProperty("skuCodes", ex.getSkuCodes());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * inventory-service is unreachable, or did not answer inside the timeout.
     * <p>
     * 503 with a Retry-After hint, NOT 500: nothing is wrong with order-service
     * or with the customer's request, and the problem is expected to be
     * temporary. Getting this distinction right matters because callers - and
     * later, the API gateway and any retry logic - behave differently for
     * "you broke it" versus "try again shortly".
     * <p>
     * Note we FAIL the order rather than accepting it optimistically. That is a
     * deliberate policy choice, and the honest one while there is no way to
     * reconcile afterwards: better to reject an order than to accept one that
     * cannot be fulfilled.
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleInventoryUnreachable(ResourceAccessException ex) {
        log.error("inventory-service unreachable: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Cannot verify stock right now. Please try again shortly.");
        problem.setTitle("Inventory service unavailable");
        problem.setProperty("retryAfterSeconds", 10);
        return problem;
    }

    /** inventory-service answered, but with an error status of its own. */
    @ExceptionHandler(RestClientResponseException.class)
    public ProblemDetail handleInventoryError(RestClientResponseException ex) {
        log.error("inventory-service returned {}: {}", ex.getStatusCode(), ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Stock check failed. Please try again shortly.");
        problem.setTitle("Inventory service error");
        return problem;
    }
}
