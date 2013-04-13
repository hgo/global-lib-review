package services;

public class ServiceException extends RuntimeException{
    public Exception e;
    
    public ServiceException(Exception e) {
        this.e = e;
    }

}
