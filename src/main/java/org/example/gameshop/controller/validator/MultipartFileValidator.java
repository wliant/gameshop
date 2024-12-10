package org.example.gameshop.controller.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class MultipartFileValidator implements ConstraintValidator<ValidMultipartFile, MultipartFile> {
    private static final Logger logger = LoggerFactory.getLogger(MultipartFileValidator.class);

    private int fileNameMaxLength;
    private String[] allowedContentTypes;

    @Override
    public void initialize(ValidMultipartFile constraintAnnotation) {
        this.fileNameMaxLength = constraintAnnotation.fileNameMaxLength();
        this.allowedContentTypes = constraintAnnotation.allowedContentTypes();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            logger.error("File is null or empty");
            return false;
        }

        // Check file size
        if (file.getName().length() > fileNameMaxLength) {
            String errorMessage = "File name is too long";
            logger.error(errorMessage);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }
        if (file.getContentType() == null) {
            String errorMessage = "File type is not provided";
            logger.error(errorMessage);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        if (Arrays.stream(this.allowedContentTypes).noneMatch(file.getContentType()::equalsIgnoreCase)) {
            String errorMessage = "File type must be one of: " + Arrays.toString(allowedContentTypes);
            logger.error(errorMessage);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
