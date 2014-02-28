package com.strongloop.android.loopback.test;

import android.util.Log;

import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.UserRepository;
import com.strongloop.android.loopback.User;
import com.strongloop.android.loopback.AccessToken;

/**
 * Created by gmxtian on 2/7/14.
 */
public class UserTest extends AsyncTestCase {

    private RestAdapter adapter;
    private UserRepository userRepo;

    static final private String uid = String.valueOf(new java.util.Date().getTime());
    static final private String userEmail = uid + "@test.com";
    static final private String userPassword = "testpassword";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        adapter = createRestAdapter();
        userRepo = adapter.createRepository(UserRepository.class);
    }

    // create and save
    public void testCreateSave() throws Throwable {

        // create user
       final User user = userRepo.createUser(userEmail, userPassword);

        assertEquals(userEmail, user.getEmail());
        assertEquals(userPassword, user.getPassword());
        assertNull(user.getId());

        doAsyncTest(new AsyncTest() {

            @Override
            public void run() {
                user.save(new VoidTestCallback() {

                    @Override
                    public void onSuccess() {
                        assertNotNull(user.getId());
                        Log.i("UserTest: create/save", "id: " + user.getId());
                        notifyFinished();
                    }
                });
            }
        });
    }

    // login / logout
    public void testLoginLogout() throws Throwable {
        // Login the user using the repository
        doAsyncTest(new AsyncTest() {

            @Override
            public void run() {

                userRepo.loginUser(userEmail, userPassword,
                    new UserRepository.LoginCallback() {

                        @Override
                        public void onError (Throwable t){
                            notifyFailed(t);
                        }

                        @Override
                        public void onSuccess (AccessToken token, User currentUser) {
                            assertNotNull("currentUser should be not null", currentUser);
                            assertEquals("currentUser.email", currentUser.getEmail(), userEmail);
                            assertNotNull("accessToken should be not null", token);
                            assertEquals("userId", token.getUserId(), currentUser.getId());
                            Log.i("UserTest", "login id: " + currentUser.getId());
                            notifyFinished();
                        }
                    });
               }

        });

        doAsyncTest(new AsyncTest() {

            @Override
            public void run() {

                userRepo.logout(new User.Callback() {

                    @Override
                    public void onSuccess() {
                        Log.i("UserTest", "logout succeeded");
                        notifyFinished();
                    }

                    @Override
                    public void onError(Throwable t) {
                        notifyFailed(t);
                    }

                });

            }
        });

    }
}