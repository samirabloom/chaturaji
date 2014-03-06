package ac.ic.chaturaji.dao;

import org.junit.Test;

/**
 * Created by vic_arribas on 2/03/14.
 */
public class daoTest {

   //@Resource
   // private BasicDataSource dataSource;

    @Test
    public void GameDaoSaveTest(){
/*
        try {
            BasicDataSource dataSource = null;
            String sql = "INSERT game VALUES (?,?,?)";

            RootConfiguration rootConfiguration = new RootConfiguration();
            dataSource = rootConfiguration.dataSource();
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = null;
            ps =  connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
*/
        GameDAO gameDAO = new GameDAO();
    }
}
