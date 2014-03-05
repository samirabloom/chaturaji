package ac.ic.chaturaji;

import ac.ic.chaturaji.chatuService.ChatuService;
import ac.ic.chaturaji.model.Game;
import android.util.Log;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Haider on 03/03/14.
 */
public class ChatuServiceTest {



    @Test
    public void shouldRegisterUser() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        String state = chatuService.createAccount("test@test.com", "testpass", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);
    }

    @Test
    public void shouldNotRegister() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        String state = chatuService.createAccount("NotAProperEmail", "testpass", "testman");

        // Should return Error if a proper email is not entered
        assertEquals("Error", state);

        state = chatuService.createAccount("test@test.com", "pas", "testman");

        // Should return Error if too small a password is entered
        assertEquals("Error", state);

        state = chatuService.createAccount("test@test.com", "pas8ht4343to3ijto5453t43tiorjgoirhgohergouehgojngoh54ogni45ohrtoigerogneroigheuirbgeroingeurohgoerngiuergergjieprng43ohgp3gji03igpiong", "testman");

        // Should return Error if too large a password is entered
        assertEquals("Error", state);
    }


    @Test
    public void shouldnotLogin() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        String state = chatuService.login("randomguy@gmail.com", "randomness");

        // this guy does not exist on the database, therefore should return an Invalid when tryin to log in
        assertEquals("Invalid", state);
    }

    @Test
    public void wrongPassword() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        String state = chatuService.createAccount("wrongpassword@test.com", "password", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("wrongpassword@test.com", "pasword");

        assertEquals("Invalid", state);
    }

    @Test
    public void shouldLogin() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        String state = chatuService.login("test@test.com", "testpass");

        // this is the test account that was previously registered, therefore should be able to log in
        assertEquals("Success", state);
    }

    @Test
    public void getGames() throws Exception {

        ChatuService testService = ChatuService.getInstance();

        String test = testService.getGames();

        Game[] gamesList = null;

        try {

            gamesList = new ObjectMapper().readValue(test, Game[].class);

        }

        catch (JsonGenerationException e) {
            Log.d("JsonGenerationException ", e.toString());
            e.printStackTrace();

        }

        catch (JsonMappingException e) {
            Log.d("JsonMappingException", e.toString());
            e.printStackTrace();

        }

        catch (IOException e) {
            Log.d("IOException", e.toString());
            e.printStackTrace();

        }

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);
    }

    @Test
    public void shouldClearDetails() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        String state = chatuService.createAccount("test@test.com", "testpass", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test@test.com", "testpass");

        assertEquals("Success", state);

        chatuService.clearCookieCred();

        state = chatuService.createGame("2");

        // we cleared the cookie and creds on the client, so should not be able to log in now
        assertEquals("Error", state);
    }

    @Test
    public void shouldCreateGame() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        String state = chatuService.createAccount("test@test.com", "testpass", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test@test.com", "testpass");

        assertEquals("Success", state);

        state = chatuService.createGame("2");

        // we cleared the cookie and creds on the client, so should not be able to log in now
        assertEquals("Success", state);
    }

    @Test
    public void tooManyAIs() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        String state = chatuService.createAccount("test@test.com", "testpass", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test@test.com", "testpass");

        assertEquals("Success", state);

        state = chatuService.createGame("5");

        // should not allow more than 3 AIs
        assertEquals("Invalid AI count", state);
    }

/*
    @Test
    public void shouldJoinGame() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();

        String state = chatuService.login("test@test.com", "testpass");

        assertEquals("Success", state);

        state = chatuService.createGame("0");

        assertEquals("Success", state);

        chatuService.clearCookieCred();

        state = chatuService.createAccount("test2@test.com", "testpass", "testman2");

        assertEquals("Success", state);

        state = chatuService.login("test@test.com", "testpass");

        assertEquals("Success", state);

        state = chatuService.getGames();

        Game[] gamesList = null;

        try {

            gamesList = new ObjectMapper().readValue(state, Game[].class);

        }

        catch (JsonGenerationException e) {
            Log.d("JsonGenerationException ", e.toString());
            e.printStackTrace();

        }

        catch (JsonMappingException e) {
            Log.d("JsonMappingException", e.toString());
            e.printStackTrace();

        }

        catch (IOException e) {
            Log.d("IOException", e.toString());
            e.printStackTrace();

        }

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);

        String id = gamesList[gamesList.length-1].getId();

        state = chatuService.joinGame(id);

        // successfully joined a game
        assertEquals("Success", state);


    }*/

}
