package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.models.Contact;
import com.ambassador.ambassadorsdk.internal.utils.Device;

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
        AmbSingleton.class,
        JSONObject.class,
        BulkShareHelper.class,
        RequestManager.class,
        Uri.class,
        Intent.class
})
public class BulkShareHelperTest {

    private BulkShareHelper bulkShareHelper;
    private RequestManager requestManager;
    private Device device;

    @Before
    public void setUpMock() {
        PowerMockito.mockStatic(
                AmbSingleton.class,
                Uri.class
        );

        bulkShareHelper = Mockito.spy(new BulkShareHelper());

        requestManager = PowerMockito.mock(RequestManager.class);
        bulkShareHelper.requestManager = requestManager;

        device = PowerMockito.mock(Device.class);
        bulkShareHelper.device = device;
    }


    @Test
    public void bulkShareSMSAPI23Test() {
        // ARRANGE
        String message = "message";
        List<Contact> contacts = new ArrayList<>();
        BulkShareHelper.BulkShareCompletion bulkShareCompletion = Mockito.mock(BulkShareHelper.BulkShareCompletion.class);
        Mockito.doReturn(23).when(device).getSdkVersion();

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
    public void bulkShareSMSApi21SingleContactTest() throws Exception {
        // ARRANGE
        String message = "message";
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact.Builder().setPhoneNumber("3133291104").build());
        BulkShareHelper.BulkShareCompletion bulkShareCompletion = Mockito.mock(BulkShareHelper.BulkShareCompletion.class);
        Mockito.doReturn(21).when(device).getSdkVersion();
        Context context = Mockito.mock(Context.class);
        Mockito.when(AmbSingleton.getContext()).thenReturn(context);
        PackageManager packageManager = Mockito.mock(PackageManager.class);
        Mockito.when(context.getPackageManager()).thenReturn(packageManager);
        Mockito.doReturn(true).when(packageManager).hasSystemFeature(Mockito.anyString());

        PowerMockito.doReturn(Mockito.mock(Uri.class)).when(Uri.class, "parse", Mockito.anyString());
        Intent intent = Mockito.mock(Intent.class);
        PowerMockito.whenNew(Intent.class).withAnyArguments().thenReturn(intent);

        // ACT
        bulkShareHelper.bulkShare(message, contacts, true, bulkShareCompletion);

        // ASSERT
        Mockito.verify(bulkShareCompletion).launchSmsIntent(Mockito.eq("3133291104"), Mockito.eq(intent));
        Mockito.verify(intent).putExtra(Mockito.eq("sms_body"), Mockito.eq(message));
    }

    @Test
    public void bulkShareSMSApi21ManyContactsTest() throws Exception {
        // ARRANGE
        String message = "message";
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact.Builder().setPhoneNumber("3133291104").build());
        contacts.add(new Contact.Builder().setPhoneNumber("3133291104").build());
        contacts.add(new Contact.Builder().setPhoneNumber("3133291104").build());
        BulkShareHelper.BulkShareCompletion bulkShareCompletion = Mockito.mock(BulkShareHelper.BulkShareCompletion.class);
        Mockito.doReturn(21).when(device).getSdkVersion();

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
        List<Contact> contacts = new ArrayList<>();
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
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact.Builder().setPhoneNumber("313-329-1104").build());
        contacts.add(new Contact.Builder().setPhoneNumber("1-313-329-1104").build());
        contacts.add(new Contact.Builder().setPhoneNumber("3133291104").build());
        contacts.add(new Contact.Builder().setPhoneNumber("13233291104").build());
        contacts.add(new Contact.Builder().setPhoneNumber("+1-313-329-1104").build());
        contacts.add(new Contact.Builder().setPhoneNumber("123456789012341223122").build());
        contacts.add(new Contact.Builder().setPhoneNumber("+1-cats-333-329-1104").build());
        contacts.add(new Contact.Builder().setPhoneNumber("1-313").build());
        contacts.add(new Contact.Builder().setPhoneNumber("12345").build());
        contacts.add(new Contact.Builder().setPhoneNumber("phoneNumber").build());
        contacts.add(new Contact.Builder().setPhoneNumber("1-3-1-3-3-2-9-1-1-0-4").build());

        // ACT
        List<String> verified = bulkShareHelper.verifiedSMSList(contacts);

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
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact.Builder().setEmailAddress("test@getambassador.com").build());
        contacts.add(new Contact.Builder().setEmailAddress("test").build());
        contacts.add(new Contact.Builder().setEmailAddress("test@getambassador").build());
        contacts.add(new Contact.Builder().setEmailAddress("c@a.io").build());
        contacts.add(new Contact.Builder().setEmailAddress("c@a.io").build());
        contacts.add(new Contact.Builder().setEmailAddress("test@").build());

        // ACT
        List<String> verified = bulkShareHelper.verifiedEmailList(contacts);

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
        boolean result1 = bulkShareHelper.isValidEmail(check1);
        boolean result2 = bulkShareHelper.isValidEmail(check2);
        boolean result3 = bulkShareHelper.isValidEmail(check3);
        boolean result4 = bulkShareHelper.isValidEmail(check4);
        boolean result5 = bulkShareHelper.isValidEmail(check5);

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
        values.add("3133291104");
        values.add("5199729550");
        BulkShareHelper.SocialServiceTrackType trackType = BulkShareHelper.SocialServiceTrackType.SMS;
        String shortCode = "shortCode";
        String fromEmail = "fromEmail";

        // ACT
        BulkShareApi.BulkShareTrackBody[] bodies = bulkShareHelper.contactArray(values, trackType, shortCode, fromEmail);

        // ASSERT
        Assert.assertEquals(2, bodies.length);
        Assert.assertEquals("", bodies[0].recipient_email);
        Assert.assertEquals("", bodies[1].recipient_email);
        Assert.assertEquals("3133291104", bodies[0].recipient_username);
        Assert.assertEquals("5199729550", bodies[1].recipient_username);
        Assert.assertEquals("shortCode", bodies[0].short_code);
        Assert.assertEquals("shortCode", bodies[1].short_code);
        Assert.assertEquals("sms", bodies[0].social_name);
        Assert.assertEquals("sms", bodies[1].social_name);
        Assert.assertEquals(fromEmail, bodies[0].from_email);
    }

    @Test
    public void contactArrayTestEmail() throws Exception {
        // ARRANGE
        List<String> values = new ArrayList<>();
        values.add("dylan@getambassador.com");
        values.add("corey@getambassador.com");
        BulkShareHelper.SocialServiceTrackType trackType = BulkShareHelper.SocialServiceTrackType.EMAIL;
        String shortCode = "shortCode";
        String fromEmail = "fromEmail";

        // ACT
        BulkShareApi.BulkShareTrackBody[] bodies = bulkShareHelper.contactArray(values, trackType, shortCode, fromEmail);

        // ASSERT
        Assert.assertEquals(2, bodies.length);
        Assert.assertEquals("dylan@getambassador.com", bodies[0].recipient_email);
        Assert.assertEquals("corey@getambassador.com", bodies[1].recipient_email);
        Assert.assertEquals("", bodies[0].recipient_username);
        Assert.assertEquals("", bodies[1].recipient_username);
        Assert.assertEquals("shortCode", bodies[0].short_code);
        Assert.assertEquals("shortCode", bodies[1].short_code);
        Assert.assertEquals("email", bodies[0].social_name);
        Assert.assertEquals("email", bodies[1].social_name);
        Assert.assertEquals(fromEmail, bodies[0].from_email);
    }

    @Test
    public void contactArrayTestOther() throws Exception {
        // ARRANGE
        BulkShareHelper.SocialServiceTrackType trackType = BulkShareHelper.SocialServiceTrackType.FACEBOOK;
        String shortCode = "shortCode";
        String fromEmail = "fromEmail";

        // ACT
        BulkShareApi.BulkShareTrackBody[] bodies = bulkShareHelper.contactArray(trackType, shortCode, fromEmail);

        // ASSERT
        Assert.assertEquals(1, bodies.length);
        Assert.assertEquals("", bodies[0].recipient_email);
        Assert.assertEquals("", bodies[0].recipient_username);
        Assert.assertEquals("shortCode", bodies[0].short_code);
        Assert.assertEquals("facebook", bodies[0].social_name);
        Assert.assertEquals(fromEmail, bodies[0].from_email);
    }

    @Test
    public void payloadObjectForEmailTest() throws Exception {
        // ARRANGE
        List<String> emails = new ArrayList<>();
        emails.add("dylan@getambassador.com");
        emails.add("corey@getambassador.com");
        String shortCode = "shortCode";
        String subject = "subject";
        String message = "message";
        String fromEmail = "fromEmail";

        // ACT
        BulkShareApi.BulkShareEmailBody body = bulkShareHelper.payloadObjectForEmail(emails, shortCode, subject, message, fromEmail);

        // ASSERT
        Assert.assertEquals(shortCode, body.short_code);
        Assert.assertEquals(subject, body.subject_line);
        Assert.assertEquals(message, body.message);
        Assert.assertEquals(2, body.to_emails.length);
        Assert.assertEquals("dylan@getambassador.com", body.to_emails[0]);
        Assert.assertEquals("corey@getambassador.com", body.to_emails[1]);
        Assert.assertEquals(fromEmail, body.from_email);
    }

    @Test
    public void payloadObjectForSMSTest() throws Exception {
        // ARRANGE
        List<String> numbers = new ArrayList<>();
        numbers.add("3133291104");
        numbers.add("5199729550");
        String fullName = "fullName";
        String message = "message";
        String fromEmail = "fromEmail";

        // ACT
        BulkShareApi.BulkShareSmsBody body = bulkShareHelper.payloadObjectForSMS(numbers, fullName, message, fromEmail);

        // ASSERT
        Assert.assertEquals(fullName, body.name);
        Assert.assertEquals(message, body.message);
        Assert.assertEquals(2, body.to.length);
        Assert.assertEquals("3133291104", body.to[0]);
        Assert.assertEquals("5199729550", body.to[1]);
        Assert.assertEquals(fromEmail, body.from_email);
    }

}
