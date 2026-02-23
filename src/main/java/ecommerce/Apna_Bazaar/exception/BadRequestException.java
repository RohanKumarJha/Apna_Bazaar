package ecommerce.Apna_Bazaar.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String imageIsEmpty) {
        super(imageIsEmpty);
    }
}
