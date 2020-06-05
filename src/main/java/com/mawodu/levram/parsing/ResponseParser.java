package com.mawodu.levram.parsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mawodu.levram.HeroParser;
import com.mawodu.levram.HeroRepositoryMetaData;
import com.mawodu.levram.entities.Hero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ResponseParser {
    private String json;

    Logger logger = LoggerFactory.getLogger(ResponseParser.class);

    public ResponseParser(String jsonResponse) {
        json = jsonResponse;
    }

    public List<Hero> heroesFromJSON() {
        logger.info(String.format("Attempting to parse JSON @Thread: %s", Thread.currentThread().getId()));
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

        logger.info(String.format("Successfully parsed JSON @Thread: %s", Thread.currentThread().getId()));
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
