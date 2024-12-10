package org.example.gameshop.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.gameshop.model.GameType;

@Converter
public class GameTypeConverter implements AttributeConverter<GameType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(GameType attribute) {
        return attribute.getCode();
    }

    @Override
    public GameType convertToEntityAttribute(Integer dbData) {
        return GameType.fromCode(dbData);
    }
}
