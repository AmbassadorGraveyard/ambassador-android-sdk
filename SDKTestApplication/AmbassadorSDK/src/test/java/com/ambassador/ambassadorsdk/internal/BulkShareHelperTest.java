package com.ambassador.ambassadorsdk.internal;

import org.json.JSONArray;
import org.json.JSONObject;
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
        AmbassadorSingleton.class,
        JSONObject.class,
        BulkShareHelper.class
})
public class BulkShareHelperTest {

    BulkShareHelper bulkShareHelper;
    RequestManager requestManager;

    @Before
    public void setUpMock() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class
        );

        PowerMockito.spy(BulkShareHelper.class);

        AmbassadorApplicationComponent component = Mockito.mock(AmbassadorApplicationComponent.class);
        Mockito.when(AmbassadorSingleton.getInstanceComponent()).thenReturn(component);
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
    public void contactArrayTestSms() throws Exception {
        // ARRANGE
        List<String> values = new ArrayList<>();
        values.add("track1");
        values.add("track2");
        values.add("track3");
        BulkShareHelper.SocialServiceTrackType trackType = BulkShareHelper.SocialServiceTrackType.SMS;
        String shortCode = "shortCode";

        JSONArray jsonArray = Mockito.mock(JSONArray.class);
        Mockito.when(BulkShareHelper.buildJSONArray()).thenReturn(jsonArray);

        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.when(BulkShareHelper.buildJSONObject()).thenReturn(jsonObject);

        // ACT
        JSONArray ret = BulkShareHelper.contactArray(values, trackType, shortCode);

        // ASSERT
        Assert.assertEquals(jsonArray, ret);
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("short_code"), Mockito.eq(shortCode));
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("social_name"), Mockito.eq(trackType.toString()));
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("recipient_email"), Mockito.eq(""));
        Mockito.verify(jsonObject).put(Mockito.eq("recipient_username"), Mockito.eq("track1"));
        Mockito.verify(jsonObject).put(Mockito.eq("recipient_username"), Mockito.eq("track2"));
        Mockito.verify(jsonObject).put(Mockito.eq("recipient_username"), Mockito.eq("track3"));
        Mockito.verify(jsonArray, Mockito.times(3)).put(Mockito.any(JSONObject.class));
    }

    @Test
    public void contactArrayTestEmail() throws Exception {
        // ARRANGE
        List<String> values = new ArrayList<>();
        values.add("track1");
        values.add("track2");
        values.add("track3");
        BulkShareHelper.SocialServiceTrackType trackType = BulkShareHelper.SocialServiceTrackType.EMAIL;
        String shortCode = "shortCode";

        JSONArray jsonArray = Mockito.mock(JSONArray.class);
        Mockito.when(BulkShareHelper.buildJSONArray()).thenReturn(jsonArray);

        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.when(BulkShareHelper.buildJSONObject()).thenReturn(jsonObject);

        // ACT
        JSONArray ret = BulkShareHelper.contactArray(values, trackType, shortCode);

        // ASSERT
        Assert.assertEquals(jsonArray, ret);
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("short_code"), Mockito.eq(shortCode));
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("social_name"), Mockito.eq(trackType.toString()));
        Mockito.verify(jsonObject).put(Mockito.eq("recipient_email"), Mockito.eq("track1"));
        Mockito.verify(jsonObject).put(Mockito.eq("recipient_email"), Mockito.eq("track2"));
        Mockito.verify(jsonObject).put(Mockito.eq("recipient_email"), Mockito.eq("track3"));
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("recipient_username"), Mockito.eq(""));
        Mockito.verify(jsonArray, Mockito.times(3)).put(Mockito.any(JSONObject.class));
    }

    @Test
    public void contactArrayTestOther() throws Exception {
        // ARRANGE
        List<String> values = new ArrayList<>();
        values.add("track1");
        values.add("track2");
        values.add("track3");
        BulkShareHelper.SocialServiceTrackType trackType = BulkShareHelper.SocialServiceTrackType.FACEBOOK;
        String shortCode = "shortCode";

        JSONArray jsonArray = Mockito.mock(JSONArray.class);
        Mockito.when(BulkShareHelper.buildJSONArray()).thenReturn(jsonArray);

        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.when(BulkShareHelper.buildJSONObject()).thenReturn(jsonObject);

        // ACT
        JSONArray ret = BulkShareHelper.contactArray(values, trackType, shortCode);

        // ASSERT
        Assert.assertEquals(jsonArray, ret);
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("short_code"), Mockito.eq(shortCode));
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("social_name"), Mockito.eq(trackType.toString()));
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("recipient_email"), Mockito.eq(""));
        Mockito.verify(jsonObject, Mockito.times(3)).put(Mockito.eq("recipient_username"), Mockito.eq(""));
        Mockito.verify(jsonArray, Mockito.times(3)).put(Mockito.any(JSONObject.class));
    }

    @Test
    public void payloadObjectForEmailTest() throws Exception {
        // ARRANGE
        List<String> emails = new ArrayList<>();
        emails.add("test1@gmail.com");
        emails.add("test2@gmail.com");
        emails.add("test3@gmail.com");
        String shortCode = "shortCode";
        String subject = "subject";
        String message = "message";

        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.when(BulkShareHelper.buildJSONObject()).thenReturn(jsonObject);
        Mockito.when(jsonObject.put(Mockito.anyString(), Mockito.anyString())).thenReturn(jsonObject);
        Mockito.when(jsonObject.put(Mockito.anyString(), Mockito.any(JSONArray.class))).thenReturn(jsonObject);

        // ACT
        JSONObject ret = BulkShareHelper.payloadObjectForEmail(emails, shortCode, subject, message);

        // ASSERT
        Assert.assertEquals(ret, jsonObject);
        Mockito.verify(jsonObject).put(Mockito.eq("to_emails"), Mockito.any(JSONArray.class));
        Mockito.verify(jsonObject).put(Mockito.eq("short_code"), Mockito.eq(shortCode));
        Mockito.verify(jsonObject).put(Mockito.eq("message"), Mockito.eq(message));
        Mockito.verify(jsonObject).put(Mockito.eq("subject_line"), Mockito.eq(subject));
    }

    @Test
    public void payloadObjectForSMSTest() throws Exception {
        // ARRANGE
        List<String> numbers = new ArrayList<>();
        numbers.add("3133291103");
        numbers.add("3133291104");
        numbers.add("3133291105");
        String fullName = "fullName";
        String message = "message";

        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.when(BulkShareHelper.buildJSONObject()).thenReturn(jsonObject);
        Mockito.when(jsonObject.put(Mockito.anyString(), Mockito.anyString())).thenReturn(jsonObject);
        Mockito.when(jsonObject.put(Mockito.anyString(), Mockito.any(JSONArray.class))).thenReturn(jsonObject);

        // ACT
        JSONObject ret = BulkShareHelper.payloadObjectForSMS(numbers, fullName, message);

        // ASSERT
        Assert.assertEquals(jsonObject, ret);
        Mockito.verify(jsonObject).put(Mockito.eq("to"), Mockito.any(JSONArray.class));
        Mockito.verify(jsonObject).put("name", fullName);
        Mockito.verify(jsonObject).put("message", message);
    }

}
