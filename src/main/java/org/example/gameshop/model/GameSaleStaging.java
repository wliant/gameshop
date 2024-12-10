package org.example.gameshop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "game_sales_staging")
public class GameSaleStaging extends AbstractGameSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "file_import_id", referencedColumnName = "id")
    private FileImport fileImport;

    @Override
    public Long getId() {
        return id;
    }

    public FileImport getFileImport() {
        return fileImport;
    }

    public void setFileImport(FileImport fileImport) {
        this.fileImport = fileImport;
    }


}
