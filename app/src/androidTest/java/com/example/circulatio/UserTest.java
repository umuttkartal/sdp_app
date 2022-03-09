package com.example.circulatio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserTest {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @BeforeClass
    private void testSetup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences("CirculatioUserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Test
    private void testValidUserData_True() {
        boolean validUser = User.isValidUserData("Connor","ExampleDevice", "12345", true);
        assertTrue(validUser);
    }

    @Test
    private void testValidUserData_False1() {
        boolean validUser = User.isValidUserData("","ExampleDevice", "12345", true);
        assertFalse(validUser);
    }

    @Test
    private void testValidUserData_False2() {
        boolean validUser = User.isValidUserData("Skaiste","ExampleDevice", "12345", false);
        assertFalse(validUser);
    }

    @Test
    private void testValidUserData_False3() {
        boolean validUser = User.isValidUserData("","ExampleDevice", "12345", false);
        assertFalse(validUser);
    }

    @Test
    private void testUserCreation() {
        User.createUser("Connor","ExampleDevice", "12345", true, context);
        boolean userExists = User.loadUserData(context);
        assertTrue(userExists);
    }

    @Test
    private void testUserCreation_Advanced() {
        String testName = "Connor";
        String testDeviceID = "ExampleDevice";
        String testDevicePin = "12345";
        boolean testManualRead = true;

        User.createUser(testName, testDeviceID,testDevicePin, true, context);

        assertEquals(testName, User.getName());
        assertEquals(testDeviceID, User.getdeviceID());
        assertEquals(testDevicePin, User.getPin());
        assertEquals(testManualRead, User.isManualRead());
    }

    @Test
    private void testUserDeletion() {
        User.deleteUserData(context);
        boolean userExists = User.loadUserData(context);
        assertFalse(userExists);
    }
}