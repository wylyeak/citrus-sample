package com.alibaba.sample.petstore.dal;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.sample.petstore.dal.dao.UserDao;
import com.alibaba.sample.petstore.dal.dataobject.Account;
import com.alibaba.sample.petstore.dal.dataobject.Profile;
import com.alibaba.sample.petstore.dal.dataobject.User;

public class UserDaoTests extends AbstractDataAccessTests {
    @Autowired
    private UserDao userDao;

    @Test
    public void getUserById() {
        User user = userDao.getUserById("j2ee");
        assertUser(user);
    }

    @Test
    public void getAuthenticatedUser() {
        User user = userDao.getAuthenticatedUser("j2ee", "j2ee");
        assertUser(user);

        // wrong password
        user = userDao.getAuthenticatedUser("j2ee", "wrongpass");
        assertNull(user);

        // no password
        user = userDao.getAuthenticatedUser("j2ee", null);
        assertNull(user);

        // user not exists
        user = userDao.getAuthenticatedUser("nonexist", null);
        assertNull(user);
    }

    private void assertUser(User user) {
        assertUser(user, true);
    }

    private void assertUser(User user, boolean checkUserId) {
        Account account = user.getAccount();
        Profile profile = user.getProfile();

        // user
        if (checkUserId) {
            assertEquals("j2ee", user.getUserId());
        }

        assertEquals(null, user.getPassword()); // ���벻�ɲ�ѯ

        // account
        assertEquals("yourname@yourdomain.com", account.getEmail());
        assertEquals("ABC", account.getFirstName());
        assertEquals("XYX", account.getLastName());
        assertEquals("OK", account.getStatus());
        assertEquals("901 San Antonio Road", account.getAddress1());
        assertEquals("MS UCUP02-206", account.getAddress2());
        assertEquals("Palo Alto", account.getCity());
        assertEquals("CA", account.getState());
        assertEquals("94303", account.getZip());
        assertEquals("US", account.getCountry());
        assertEquals("555-555-5555", account.getPhone());
        assertEquals("1234567", account.getCreditCardNumber());
        assertEquals("Visa", account.getCreditCardType());
        assertEquals("2005-12-15", new SimpleDateFormat("yyyy-MM-dd").format(account.getCreditCardExpiry()));
        assertEquals(12, account.getCreditCardExpiryMonth());
        assertEquals(2005, account.getCreditCardExpiryYear());

        // profile
        assertEquals("english", profile.getLanguagePreference());
        assertEquals("DOGS", profile.getFavoriteCategoryId());
    }

    @Test
    public void insert_update() {
        // init user ids
        List<String> userIdList = userDao.getUserIdList();
        assertArrayEquals(new String[] { "ACID", "j2ee" }, userIdList.toArray(new String[userIdList.size()]));

        // insert new user
        User user = userDao.getUserById("j2ee");

        user.setUserId("myuser");
        user.setPassword("mypass");

        userDao.insertUser(user);

        // check user ids again
        userIdList = userDao.getUserIdList();
        assertArrayEquals(new String[] { "ACID", "j2ee", "myuser" }, userIdList.toArray(new String[userIdList.size()]));

        // check new user
        user = userDao.getAuthenticatedUser("myuser", "mypass");
        assertEquals("myuser", user.getUserId());
        assertUser(user, false);

        // change password
        user.setPassword("newpass");
        userDao.updateUser(user);

        assertNull(userDao.getAuthenticatedUser("myuser", "mypass"));

        user = userDao.getAuthenticatedUser("myuser", "newpass");
        assertEquals("myuser", user.getUserId());
        assertUser(user, false);

        // update other data
        user.getAccount().setCity("newcity");
        user.getAccount().setCreditCardExpiryMonth(1);
        user.getAccount().setCreditCardExpiryYear(2011);
        user.getProfile().setLanguagePreference("chinese");
        userDao.updateUser(user);

        user = userDao.getAuthenticatedUser("myuser", "newpass");
        assertEquals("myuser", user.getUserId());
        assertEquals("newcity", user.getAccount().getCity());
        assertEquals("2011-01-15", new SimpleDateFormat("yyyy-MM-dd").format(user.getAccount().getCreditCardExpiry()));
        assertEquals(1, user.getAccount().getCreditCardExpiryMonth());
        assertEquals(2011, user.getAccount().getCreditCardExpiryYear());
        assertEquals("chinese", user.getProfile().getLanguagePreference());
    }
}
