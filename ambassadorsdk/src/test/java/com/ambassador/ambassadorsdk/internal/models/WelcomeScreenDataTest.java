package com.ambassador.ambassadorsdk.internal.models;


import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({

})
public class WelcomeScreenDataTest {

    @Test
    public void replaceNameVarsNameTest() {
        // ARRANGE
        String testCase1 = "{{ name }}{{ name }} is {{ name }} and {{ name }}!";
        String testCase2 = "{{ name }} {{ name }} {{ name }}!";
        String testCase3 = "No name here!";
        String testCase4 = "   {{ name }}   ";

        // ACT
        String result1 = new WelcomeScreenData.Builder().setName("Cat").build().replaceNameVars(testCase1);
        String result2 = new WelcomeScreenData.Builder().setName("Cat").build().replaceNameVars(testCase2);
        String result3 = new WelcomeScreenData.Builder().setName("Cat").build().replaceNameVars(testCase3);
        String result4 = new WelcomeScreenData.Builder().setName("Cat").build().replaceNameVars(testCase4);

        // ASSERT
        Assert.assertEquals("CatCat is Cat and Cat!", result1);
        Assert.assertEquals("Cat Cat Cat!", result2);
        Assert.assertEquals("No name here!", result3);
        Assert.assertEquals("   Cat   ", result4);
    }

    @Test
    public void replaceNameVarsAmbassadorTest() {
        // ARRANGE
        String testCase1 = "{{ name }}{{ name }} is {{ name }} and {{ name }}!";
        String testCase2 = "{{ name }} {{ name }}. {{ name }}! {{ name }}!";
        String testCase3 = "No name here!";
        String testCase4 = "   {{ name }}   ";

        // ACT
        String result1 = new WelcomeScreenData.Builder().setName("An ambassador of Cat").build().replaceNameVars(testCase1);
        String result2 = new WelcomeScreenData.Builder().setName("An ambassador of Cat").build().replaceNameVars(testCase2);
        String result3 = new WelcomeScreenData.Builder().setName("An ambassador of Cat").build().replaceNameVars(testCase3);
        String result4 = new WelcomeScreenData.Builder().setName("An ambassador of Cat").build().replaceNameVars(testCase4);

        // ASSERT
        Assert.assertEquals("An ambassador of Catan ambassador of Cat is an ambassador of Cat and an ambassador of Cat!", result1);
        Assert.assertEquals("An ambassador of Cat an ambassador of Cat. An ambassador of Cat! An ambassador of Cat!", result2);
        Assert.assertEquals("No name here!", result3);
        Assert.assertEquals("   An ambassador of Cat   ", result4);
    }

    @Test
    public void midSentenceNameTest() {
        // ARRANGE
        String case1 = "Cat";
        String case2 = "An ambassador of Cats";

        // ACT
        String result1 = new WelcomeScreenData().midSentenceName(case1);
        String result2 = new WelcomeScreenData().midSentenceName(case2);

        // ASSERT
        Assert.assertEquals("Cat", result1);
        Assert.assertEquals("an ambassador of Cats", result2);

    }

    @Test
    public void isMidSentenceTest() {
        // ARRANGE
        String text = "This is a sentence. This, is a sentence! This is a sentence? This";
        int[] cases = new int[]{ 19, 20, 25, 26, 40, 41, 60, 61, 38, 0 };
        boolean[] expectedResults = new boolean[]{ false, false, true, true, false, false ,false, false, true, false };

        // ACT
        boolean[] results = new boolean[cases.length];
        for (int i = 0; i < cases.length; i++) {
            results[i] = new WelcomeScreenData().isMidSentence(cases[i], text);
        }

        // ASSERT
        for (int i = 0; i < cases.length; i++) {
            Assert.assertEquals(expectedResults[i], results[i]);
        }
    }

}
