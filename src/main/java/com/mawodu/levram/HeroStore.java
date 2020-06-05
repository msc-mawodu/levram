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
import java.util.Collections;
import java.util.List;

@Transactional
public class HeroStore {

    Logger logger = LoggerFactory.getLogger(HeroStore.class);

    private static final String insertHero = "INSERT INTO hero (h_id, h_name) VALUES (?,?)";
    private static final String insertHeroThumbnail = "INSERT INTO thumbnail (h_id, t_path, t_extension) VALUES (?, ?, ?)";
    private static final String insertHeroDescription = "INSERT INTO description (h_id, d_text, d_language) VALUES (?, ?, ?);";

    private JdbcTemplate jdbcTemplate;

    public HeroStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    public void store(Hero hero) {
        this.batchStore(Collections.singletonList(hero));
    }

    public void batchStore(List<Hero> heroes) {
        try {
            Connection jdbc = jdbcTemplate.getDataSource().getConnection();
            jdbc.setAutoCommit(false); // disable auto commit, to enable batch processing as a single transaction.

            PreparedStatement psInsertHero = jdbc.prepareStatement(insertHero);
            PreparedStatement psInsertHeroThumbnail = jdbc.prepareStatement(insertHeroThumbnail);
            PreparedStatement psInsertHeroDescription = jdbc.prepareStatement(insertHeroDescription);

            heroes.stream().forEach( h -> {
                try {
                    prepareBatchItem(psInsertHero, psInsertHeroDescription, psInsertHeroThumbnail, h);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            int[] resultHero = psInsertHero.executeBatch();
            int[] resultDescription = psInsertHeroDescription.executeBatch();
            int[] resultThumbnail = psInsertHeroThumbnail.executeBatch();

            if (resultHero.length != resultDescription.length || resultHero.length != resultDescription.length || resultDescription.length != resultThumbnail.length) {
                // todo: handle, rollback and re-try transaction if something fails.
                jdbc.rollback();
            }

            jdbc.commit();

            logger.info(String.format("Inserted %s heroes into db @Thread: %s", resultHero.length, Thread.currentThread().getId()));

        } catch (SQLException e) {
            logger.error(String.format("FAILED to insert heroes into db @Thread: %s", Thread.currentThread().getId()));
            e.printStackTrace();
        }
    }

    // NB. prepared statement objects are passed as reference and mutated to allow batch execution.
    private void prepareBatchItem(PreparedStatement psInsertHero, PreparedStatement psInsertHeroDescription, PreparedStatement psInsertHeroThumbnail, Hero hero) throws SQLException {
        psInsertHero.setInt(1, hero.getId());
        psInsertHero.setString(2, hero.getName());
        psInsertHero.addBatch();

        psInsertHeroDescription.setInt(1, hero.getId());
        psInsertHeroDescription.setString(2, hero.getDescription());
        psInsertHeroDescription.setString(3, "en");
        psInsertHeroDescription.addBatch();

        psInsertHeroThumbnail.setInt(1, hero.getId());
        psInsertHeroThumbnail.setString(2, hero.getThumbnail().getPath());
        psInsertHeroThumbnail.setString(3, hero.getThumbnail().getExtension());
        psInsertHeroThumbnail.addBatch();
    }
}
