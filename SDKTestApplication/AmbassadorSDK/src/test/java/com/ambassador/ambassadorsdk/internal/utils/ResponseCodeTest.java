package com.ambassador.ambassadorsdk.internal.utils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({

})
public class ResponseCodeTest {

    @Test
    public void isSuccessfulTrueBottomCaseTest() {
        // ARRANGE
        int successBottomCase = 200;

        // ACT
        boolean successBottomResult = new ResponseCode(successBottomCase).isSuccessful();

        // ASSERT
        Assert.assertTrue(successBottomResult);
    }

    @Test
    public void isSuccessfulTrueTopCaseTest() {
        // ARRANGE
        int successTopCase = 299;

        // ACT
        boolean successTopResult = new ResponseCode(successTopCase).isSuccessful();

        // ASSERT
        Assert.assertTrue(successTopResult);
    }

    @Test
    public void isSuccessfulFalseBottomCaseTest() {
        // ARRANGE
        int failBottomCase = 300;

        // ACT
        boolean failBottomResult = new ResponseCode(failBottomCase).isSuccessful();

        // ASSERT
        Assert.assertFalse(failBottomResult);
    }

    @Test
    public void isSuccessfulFalseTopCaseTest() {
        // ARRANGE
        int failLowerTopCase = 199;

        // ACT
        boolean failLowerTopResult = new ResponseCode(failLowerTopCase).isSuccessful();

        // ASSERT
        Assert.assertFalse(failLowerTopResult);
    }

    @Test
    public void isSuccessfulFalseNegativeCaseTest() {
        // ARRANGE
        int negativeCase = -1;

        // ACT
        boolean negativeResult = new ResponseCode(negativeCase).isSuccessful();

        // ASSERT
        Assert.assertFalse(negativeResult);
    }

}
