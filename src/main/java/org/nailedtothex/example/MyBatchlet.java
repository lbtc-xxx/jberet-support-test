package org.nailedtothex.example;

import javax.batch.api.AbstractBatchlet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.logging.Logger;

public class MyBatchlet extends AbstractBatchlet {
    private static final Logger log = Logger.getLogger(MyBatchlet.class.getName());

    @Override
    public String process() throws Exception {
        try (Connection cn = MyDatabaseUtil.getConnection()) {
            try (Statement st = cn.createStatement()) {
                try {
                    st.executeUpdate("drop table src");
                    log.info("drop table succeeded.");
                } catch (Exception e) {
                    log.info("drop table failed.");
                }
                try {
                    st.executeUpdate("drop table dest");
                    log.info("drop table succeeded.");
                } catch (Exception e) {
                    log.info("drop table failed.");
                }
                st.executeUpdate("create table src (data int)");
                st.executeUpdate("create table dest (data int primary key)");
            }

            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement("insert into src (data) values (?)")) {
                for (int i = 1; i <= 20; i++) {
                    ps.setInt(1, i);
                    ps.executeUpdate();
                }

                // to make exception happen in ItemWriter due to duplicate key
                ps.setInt(1, 15);
                ps.executeUpdate();

                cn.commit();
            }
        }
        return null;
    }
}
