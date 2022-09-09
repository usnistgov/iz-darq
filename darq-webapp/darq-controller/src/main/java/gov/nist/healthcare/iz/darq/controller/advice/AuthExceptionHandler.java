package gov.nist.healthcare.iz.darq.controller.advice;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.domain.exception.ResourceAccessForbidden;
import gov.nist.healthcare.iz.darq.adf.service.exception.InvalidFileFormat;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.exception.JobRunningException;
import gov.nist.healthcare.iz.darq.service.exception.OperationPartialFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionInvocationTargetException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class AuthExceptionHandler {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AuthenticationEntryPoint entryPoint;

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(AuthenticationException e, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.entryPoint.commence(request, response, e);
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
    @ExceptionHandler(OperationPartialFailureException.class)
    @ResponseBody
    public OpAck<String> handleOperationPartialFailureException(OperationPartialFailureException e) {
        return new OpAck<>(OpAck.AckStatus.WARNING, e.getMessage(), e.getMessage(), "warning");
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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ResourceAccessForbidden.class)
    @ResponseBody
    public OpAck<String> handleAccessDenied(ResourceAccessForbidden e) {
        return new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public OpAck<String> handleAccessDenied(AccessDeniedException e) {
        return new OpAck<>(OpAck.AckStatus.FAILED, "User does not have permission to access/perform action", e.getMessage(), "error");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public OpAck<String> handleException(Exception e) {
        return new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        if(e.getCause() instanceof ExpressionInvocationTargetException) {
            if(e.getCause().getCause() instanceof ResourceAccessForbidden) {
                handleResourceAccessForbidden(response, (ResourceAccessForbidden) e.getCause().getCause());
                return;
            } else if(e.getCause().getCause() instanceof NotFoundException) {
                handleNotFoundException(response, (NotFoundException) e.getCause().getCause());
                return;
            }
        }
        handleIllegalArgumentException(response, e);
    }

    public void handleResourceAccessForbidden(HttpServletResponse response, ResourceAccessForbidden e) throws IOException {
        response.setStatus(403);
        OpAck<String> ack = new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
        this.objectMapper.writeValue(response.getOutputStream(), ack);
    }

    public void handleNotFoundException(HttpServletResponse response, NotFoundException e) throws IOException {
        response.setStatus(400);
        OpAck<String> ack = new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
        this.objectMapper.writeValue(response.getOutputStream(), ack);
    }

    public void handleIllegalArgumentException(HttpServletResponse response, IllegalArgumentException e) throws IOException {
        response.setStatus(400);
        OpAck<String> ack = new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), e.getMessage(), "error");
        this.objectMapper.writeValue(response.getOutputStream(), ack);
    }


}
