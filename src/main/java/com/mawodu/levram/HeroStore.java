package com.mawodu.levram;


import com.mawodu.levram.entities.Hero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class HeroStore {

    Logger logger = LoggerFactory.getLogger(HeroStore.class);

    private JdbcTemplate jdbcTemplate;

    public HeroStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void store(Hero hero) {
        logger.info(String.format("Thread %s attempting to store hero in db;", Thread.currentThread().getId()));

        try {
            Connection jdbc = jdbcTemplate.getDataSource().getConnection();

            jdbc.setAutoCommit(false); //transaction block start

            String insertHero = "INSERT INTO hero (h_id, h_name) VALUES (?,?)";
            String insertHeroThumbnail = "INSERT INTO thumbnail (h_id, t_path, t_extension) VALUES (?, ?, ?)";
            String insertHeroDescription = "INSERT INTO description (h_id, d_text, d_language) VALUES (?, ?, ?);";

            PreparedStatement psInsertHero = jdbc.prepareStatement(insertHero);
            PreparedStatement psInsertHeroThumbnail = jdbc.prepareStatement(insertHeroThumbnail);
            PreparedStatement psInsertHeroDescription = jdbc.prepareStatement(insertHeroDescription);

            psInsertHero.setInt(1, hero.getId());
            psInsertHero.setString(2, hero.getName());
            psInsertHero.executeUpdate();

            psInsertHeroThumbnail.setInt(1, hero.getId());
            psInsertHeroThumbnail.setString(2, hero.getThumbnail().getPath());
            psInsertHeroThumbnail.setString(3, hero.getThumbnail().getExtension());
            psInsertHeroThumbnail.executeUpdate();

            psInsertHeroDescription.setInt(1, hero.getId());
            psInsertHeroDescription.setString(2, hero.getDescription());
            psInsertHeroDescription.setString(3, "en"); // todo: drive by language
            psInsertHeroDescription.executeUpdate();

            jdbc.commit(); // commit as a transaction (3 separate inserts)
            jdbc.close();
            logger.info(String.format("Stored %s in db;", hero.getName()));

        } catch (SQLException e) {
            e.printStackTrace();
            // handle...
        }
    }

    // todo:
    public void batchStore(List<Hero> heroes) {
        // todo insert all as a single transaction.
    }

    public List<Integer> fetchAllHeroIds() {
        List<Integer> ids = new ArrayList<>();
        try {
            Connection jdbc = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement psSelect = jdbc.prepareStatement("SELECT h_id from hero;");
            ResultSet rows = psSelect.executeQuery();
            while(rows.next()) {
                ids.add(rows.getInt("h_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // handle...
        }

        return ids;
    }
}
