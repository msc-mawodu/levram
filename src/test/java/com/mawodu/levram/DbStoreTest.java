package com.mawodu.levram;


import com.mawodu.levram.entities.Hero;
import com.mawodu.levram.parsing.ResponseParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CoreConfig.class }, loader = AnnotationConfigContextLoader.class)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DbStoreTest {

    @Autowired
    HeroStore store;

    @Test
    public void shouldStoreAllCorrectly() {

        ResponseParser parser = new ResponseParser(getJsonFromFile());
        List<Hero> heroes = parser.heroesFromJSON();

        heroes.stream().forEach( hero ->
                store.store(hero)
        );

        List<Integer> retreivedHeroesIds = store.fetchAllHeroIds();
        Assert.assertEquals(20, retreivedHeroesIds.size());

        ids().stream().forEach( i -> {
            if( retreivedHeroesIds.contains(i)) {
                retreivedHeroesIds.remove(i);
            }
        });

        Assertions.assertEquals(0, retreivedHeroesIds.size());
    }

    private List<Integer> ids() {
        return Arrays.asList(1011334, 1017100, 1009144, 1010699, 1009146, 1016823, 1009148, 1009149, 1010903, 1011266, 1010354, 1010846, 1011297, 1011031, 1009150, 1011198, 1011175, 1011136, 1011176, 1010870);
    }

    private String getJsonFromFile() {
        InputStream stream = getClass().getResourceAsStream("/mock/characters-mock.txt");
        String text = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        return text;
    }
}
