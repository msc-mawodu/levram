package com.mawodu.levram;

import com.fasterxml.jackson.databind.JsonNode;
import com.mawodu.levram.entities.Hero;
import com.mawodu.levram.entities.Thumbnail;

import java.util.ArrayList;
import java.util.List;

public class HeroParser {
    private List<Hero> heroes;

    public HeroParser() {
        this.heroes = new ArrayList<>();
    }

    public void addHero(JsonNode jsonNode) {
        this.heroes.add(new HeroBuilder()
                .id(jsonNode.get("id").asInt())
                .name(jsonNode.get("name").asText())
                .description(jsonNode.get("description").asText())
                .thumbnail(jsonNode.get("thumbnail"))
                .create()
        );
    }

    public List<Hero> getHeroes() {
        return heroes;
    }

    private static class HeroBuilder {
        private int id;
        private String name;
        private String description;
        private Thumbnail thumbnail;

        public HeroBuilder() {
        }


        private HeroBuilder id(int id) {
            this.id = id;
            return this;
        }

        private HeroBuilder name(String name) {
            this.name = name;
            return this;
        }

        private HeroBuilder description(String description) {
            this.description = description;
            return this;
        }

        private HeroBuilder thumbnail(JsonNode thumbnail) {
            this.thumbnail = new Thumbnail(thumbnail.get("path").asText(), (thumbnail.get("extension").asText()));
            return this;
        }

        private Hero create() {
            return new Hero(this.id, this.name, this.description, this.thumbnail);
        }

    }

}
