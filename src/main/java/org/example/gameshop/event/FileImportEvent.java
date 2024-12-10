package org.example.gameshop.event;

import org.example.gameshop.model.FileImport;
import org.springframework.context.ApplicationEvent;

public class FileImportEvent extends ApplicationEvent {
    private final FileImport fileImport;

    public FileImportEvent(Object source, FileImport fileImport) {
        super(source);
        this.fileImport = fileImport;

    }

    public FileImport getFileImport() {
        return fileImport;
    }
}
