package com.ambassador.ambassadorsdk.internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class
})
public class BulkShareHelperTest {

    BulkShareHelper bulkShareHelper;
    RequestManager requestManager;

    @Before
    public void setUpMock() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class
        );

        AmbassadorApplicationComponent component = Mockito.mock(AmbassadorApplicationComponent.class);
        Mockito.when(AmbassadorSingleton.getComponent()).thenReturn(component);
        Mockito.doNothing().when(component).inject(Mockito.any(BulkShareHelper.class));

        bulkShareHelper = Mockito.spy(new BulkShareHelper());

        requestManager = Mockito.mock(RequestManager.class);
        bulkShareHelper.requestManager = requestManager;
    }

    @Test
    public void bulkShareSMSTest() {
        // ARRANGE
        String message = "message";
        List<ContactObject> contacts = new ArrayList<>();
        BulkShareHelper.BulkShareCompletion bulkShareCompletion = Mockito.mock(BulkShareHelper.BulkShareCompletion.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocation.getArguments()[2];
                completion.onSuccess("success");
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocation.getArguments()[2];
                completion.onFailure("failure");
                return null;
            }
        }).when(requestManager).bulkShareSms(Mockito.eq(contacts), Mockito.eq(message), Mockito.any(RequestManager.RequestCompletion.class));

        // ACT & ASSERT
        bulkShareHelper.bulkShare(message, contacts, true, bulkShareCompletion);
        Mockito.verify(requestManager).bulkShareTrack(Mockito.eq(contacts), Mockito.eq(BulkShareHelper.SocialServiceTrackType.SMS));
        Mockito.verify(bulkShareCompletion).bulkShareSuccess();

        // ACT & ASSERT
        bulkShareHelper.bulkShare(message, contacts, true, bulkShareCompletion);
        Mockito.verify(bulkShareCompletion).bulkShareFailure();
    }

    @Test
    public void bulkShareEmailTest() {
        // ARRANGE
        String message = "message";
        List<ContactObject> contacts = new ArrayList<>();
        BulkShareHelper.BulkShareCompletion bulkShareCompletion = Mockito.mock(BulkShareHelper.BulkShareCompletion.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocation.getArguments()[2];
                completion.onSuccess("success");
                return null;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocation.getArguments()[2];
                completion.onFailure("failure");
                return null;
            }
        }).when(requestManager).bulkShareEmail(Mockito.eq(contacts), Mockito.eq(message), Mockito.any(RequestManager.RequestCompletion.class));

        // ACT & ASSERT
        bulkShareHelper.bulkShare(message, contacts, false, bulkShareCompletion);
        Mockito.verify(requestManager).bulkShareTrack(Mockito.eq(contacts), Mockito.eq(BulkShareHelper.SocialServiceTrackType.EMAIL));
        Mockito.verify(bulkShareCompletion).bulkShareSuccess();

        // ACT & ASSERT
        bulkShareHelper.bulkShare(message, contacts, false, bulkShareCompletion);
        Mockito.verify(bulkShareCompletion).bulkShareFailure();

    }

    @Test
    public void verifiedSMSListTest() {
        // ARRANGE
        List<ContactObject> contacts = new ArrayList<>();
        contacts.add(new ContactObject(null, null, null, null, "313-329-1104"));
        contacts.add(new ContactObject(null, null, null, null, "1-313-329-1104"));
        contacts.add(new ContactObject(null, null, null, null, "3133291104"));
        contacts.add(new ContactObject(null, null, null, null, "13233291104"));
        contacts.add(new ContactObject(null, null, null, null, "+1-313-329-1104"));
        contacts.add(new ContactObject(null, null, null, null, "123456789012341223122"));
        contacts.add(new ContactObject(null, null, null, null, "+1-cats-333-329-1104"));
        contacts.add(new ContactObject(null, null, null, null, "1-313"));
        contacts.add(new ContactObject(null, null, null, null, "12345"));
        contacts.add(new ContactObject(null, null, null, null, "phoneNumber"));
        contacts.add(new ContactObject(null, null, null, null, "1-3-1-3-3-2-9-1-1-0-4"));

        // ACT
        List<String> verified = BulkShareHelper.verifiedSMSList(contacts);

        // ASSERT
        Assert.assertEquals(4, verified.size());
        Assert.assertTrue(verified.contains("3133291104"));
        Assert.assertTrue(verified.contains("13133291104"));
        Assert.assertTrue(verified.contains("13233291104"));
        Assert.assertTrue(verified.contains("13333291104"));
    }

    @Test
    public void verifiedEmailListTest() {
        // ARRANGE
        List<ContactObject> contacts = new ArrayList<>();
        contacts.add(new ContactObject(null, null, null, "test@getambassador.com"));
        contacts.add(new ContactObject(null, null, null, "test"));
        contacts.add(new ContactObject(null, null, null, "test@getambassador"));
        contacts.add(new ContactObject(null, null, null, "c@a.io"));
        contacts.add(new ContactObject(null, null, null, "c@a.io"));
        contacts.add(new ContactObject(null, null, null, "test@"));

        // ACT
        List<String> verified = BulkShareHelper.verifiedEmailList(contacts);

        // ASSERT
        Assert.assertEquals(2, verified.size());
        Assert.assertTrue(verified.contains("test@getambassador.com"));
        Assert.assertTrue(verified.contains("c@a.io"));
    }

    @Test
    public void isValidEmailTest() {
        // ARRANGE
        String check1 = "user@domain.com";
        String check2 = "user@domain.co.in";
        String check3 = "user#domain.com";
        String check4 = "@yahoo.com.";
        String check5 = "test@getambassador";

        // ACT
        boolean result1 = BulkShareHelper.isValidEmail(check1);
        boolean result2 = BulkShareHelper.isValidEmail(check2);
        boolean result3 = BulkShareHelper.isValidEmail(check3);
        boolean result4 = BulkShareHelper.isValidEmail(check4);
        boolean result5 = BulkShareHelper.isValidEmail(check5);

        // ASSERT
        Assert.assertTrue(result1);
        Assert.assertTrue(result2);
        Assert.assertFalse(result3);
        Assert.assertFalse(result4);
        Assert.assertFalse(result5);
    }

    @Test
    public void contactArrayTest() {
        // merge in master before writing this test
    }

    @Test
    public void payloadObjectForEmailTest() {
        // merge in master before writing this test
    }

    @Test
    public void payloadObjectForSMSTest() {
        // merge in master before writing this test
    }

}
