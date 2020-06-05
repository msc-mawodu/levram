package com.mawodu.levram.parsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mawodu.levram.HeroParser;
import com.mawodu.levram.HeroRepositoryMetaData;
import com.mawodu.levram.entities.Hero;

import java.util.List;
import java.util.Optional;

public class ResponseParser {
    private String json;

    public ResponseParser(String jsonResponse) {
        json = jsonResponse;
    }

    public List<Hero> heroesFromJSON() {
        HeroParser heroParser = new HeroParser();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            String code = jsonNode.get("code").asText();

            jsonNode.get("data")
                    .get("results")
                    .elements()
                    .forEachRemaining(heroParser::addHero);

        } catch (JsonMappingException e) {
            e.printStackTrace();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return heroParser.getHeroes();
    }

    public Optional<HeroRepositoryMetaData> metaDataFromJson() {
        Optional<HeroRepositoryMetaData> result = Optional.empty();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode dataNode = jsonNode.get("data");

            result = Optional.of(HeroRepositoryMetaData.create()
                    .responseCode(jsonNode.get("code").asInt())
                    .limit(dataNode.get("limit").asInt())
                    .total( dataNode.get("total").asInt())
                    .count(dataNode.get("count").asInt())
                    .build());

        } catch (JsonProcessingException e) {
            //
        }

        return result;
    }
}
