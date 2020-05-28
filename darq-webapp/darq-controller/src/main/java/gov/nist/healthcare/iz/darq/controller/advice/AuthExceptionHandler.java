package gov.nist.healthcare.iz.darq.controller.advice;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.adf.service.exception.InvalidFileFormat;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.exception.JobRunningException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class AuthExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public OpAck<String> handleAuthenticationException(AuthenticationException e) {
        return new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public OpAck<String> handleNotFoundException(NotFoundException e) {
        return new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OperationFailureException.class)
    @ResponseBody
    public OpAck<String> handleOperationFailureException(OperationFailureException e) {
        return new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFileFormat.class)
    @ResponseBody
    public OpAck<String> handleOperationFailureException(InvalidFileFormat e) {
        return new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JobRunningException.class)
    @ResponseBody
    public OpAck<String> handleOperationJobRunningException(JobRunningException e) {
        return new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
    }

}
