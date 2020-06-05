package com.mawodu.levram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mawodu.levram.entities.Hero;
import com.mawodu.levram.parsing.ResponseParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class HeroParserTest {

    @BeforeAll
    public static void setup() {
    }

    @Test
    public void shouldParseCorrectly() throws JsonProcessingException {
        ResponseParser parser = new ResponseParser(getJsonFromFile());
        List<Hero> heroes = parser.heroesFromJSON();

        Assertions.assertEquals(20, heroes.size());
        assertHero(1011334, "3-D Man", "", "http://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784", "jpg", heroes.get(0));
        assertHero(1010354, "Adam Warlock", "Adam Warlock is an artificially created human who was born in a cocoon at a scientific complex called The Beehive.", "http://i.annihil.us/u/prod/marvel/i/mg/a/f0/5202887448860", "jpg", heroes.get(10));
        assertHero(1010870, "Ajaxis", "", "http://i.annihil.us/u/prod/marvel/i/mg/b/70/4c0035adc7d3a", "jpg", heroes.get(19));
    }

    private void assertHero(int id, String name, String description, String thumbnailPath, String thumbnailExt, Hero hero) {
        Assertions.assertEquals(id, hero.getId());
        Assertions.assertEquals(name, hero.getName());
        Assertions.assertEquals(description, hero.getDescription());
        Assertions.assertEquals(thumbnailPath, hero.getThumbnail().getPath());
        Assertions.assertEquals(thumbnailExt, hero.getThumbnail().getExtension());
    }

    private String getJsonFromFile() {
        InputStream stream = getClass().getResourceAsStream("/mock/characters-mock.txt");
        String text = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        return text;
    }

}
