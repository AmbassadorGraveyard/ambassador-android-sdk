package com.ambassador.ambassadorsdk;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


/**
 * Created by JakeDunahee on 9/9/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({BulkShareHelper.class, Toast.class, AmbassadorSingleton.class, AmbassadorSingleton.class})
public class BulkShareHelperTest {
    BulkShareHelper bulkShareHelper;

    @Inject
    RequestManager mockRequestManager;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(BulkShareHelperTest bulkShareHelperTest);
    }

    @Before
    public void setUpMock() {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        TestComponent component = DaggerBulkShareHelperTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);

        PowerMockito.mockStatic(AmbassadorSingleton.class);
        AmbassadorApplicationComponent application = mock(AmbassadorApplicationComponent.class);
        PowerMockito.when(AmbassadorSingleton.getComponent()).thenReturn(application);
        doNothing().when(application).inject(any(BulkShareHelper.class));

        bulkShareHelper = spy(new BulkShareHelper());
        bulkShareHelper.requestManager = mockRequestManager;
    }

    @Test
    public void bulkShareSMSTest() throws Exception {
        // ARRANGE
        String mockMessage = "fakeMessage";
        List mockContacts = mock(List.class);
        BulkShareHelper.BulkShareCompletion mockBulkShareCompletion = mock(BulkShareHelper.BulkShareCompletion.class);

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion)invocation.getArguments()[2];
                completion.onSuccess("fakeResponse");
                return null;
            }
        })
        .doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocation.getArguments()[2];
                completion.onFailure("fakeResponse");
                return null;
            }
        })
        .when(mockRequestManager).bulkShareSms(anyList(), anyString(), any(RequestManager.RequestCompletion.class));

        // ACT
        bulkShareHelper.bulkShare(mockMessage, mockContacts, true, mockBulkShareCompletion);

        // ASSERT
        verify(bulkShareHelper).bulkShare(mockMessage, mockContacts, true, mockBulkShareCompletion);
        verify(mockRequestManager).bulkShareSms(anyList(), anyString(), any(RequestManager.RequestCompletion.class));
        verify(mockRequestManager).bulkShareTrack(anyList(), any(BulkShareHelper.SocialServiceTrackType.class));
        verify(mockBulkShareCompletion).bulkShareSuccess();

        // ACT AGAIN
        bulkShareHelper.bulkShare(mockMessage, mockContacts, true, mockBulkShareCompletion);

        // ASSERT AGAIN
        verify(mockBulkShareCompletion).bulkShareFailure();
    }

    @Test
    public void bulkShareEmailTest() throws Exception {
        // ARRANGE
        String mockMessage = "fakeMessage";
        List mockContacts = mock(List.class);
        BulkShareHelper.BulkShareCompletion mockBulkShareCompletion = mock(BulkShareHelper.BulkShareCompletion.class);

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion)invocation.getArguments()[2];
                completion.onSuccess("fakeResponse");
                return null;
            }
        })
        .doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RequestManager.RequestCompletion completion = (RequestManager.RequestCompletion) invocation.getArguments()[2];
                completion.onFailure("fakeResponse");
                return null;
            }
        })
        .when(mockRequestManager).bulkShareEmail(anyList(), anyString(), any(RequestManager.RequestCompletion.class));

        // ACT
        bulkShareHelper.bulkShare(mockMessage, mockContacts, false, mockBulkShareCompletion);

        // ASSERT
        verify(bulkShareHelper).bulkShare(mockMessage, mockContacts, false, mockBulkShareCompletion);
        verify(mockRequestManager).bulkShareEmail(anyList(), anyString(), any(RequestManager.RequestCompletion.class));
        verify(mockRequestManager).bulkShareTrack(anyList(), any(BulkShareHelper.SocialServiceTrackType.class));
        verify(mockBulkShareCompletion).bulkShareSuccess();

        // ACT AGAIN
        bulkShareHelper.bulkShare(mockMessage, mockContacts, false, mockBulkShareCompletion);

        // ASSERT AGAIN
        verify(mockBulkShareCompletion).bulkShareFailure();
    }

    // STATIC HELPER FUNCTIONS
    @Test
    public void verifiedSMSListTest() {
        // ARRANGE
        ArrayList<ContactObject> mockContacts = new ArrayList<>();
        ContactObject passObject1 = new ContactObject("Success1", "Mobile", "815-555-4562");
        ContactObject failObject1 = new ContactObject("Failure1", "Mobile", "4578-5648-1213");
        ContactObject passObject2 = new ContactObject("Success2", "Mobile", "555-1234");
        ContactObject failObject2 = new ContactObject("Failure2", "Mobile", "4567");
        ContactObject passObject3 = new ContactObject("Success3", "Mobile", "+1-123-555-4561");

        // ACT
        mockContacts.add(passObject1);
        mockContacts.add(failObject1);
        mockContacts.add(passObject2);
        mockContacts.add(failObject2);
        mockContacts.add(passObject3);

        // ASSERT
        assertEquals("[8155554562, 5551234, 11235554561]", BulkShareHelper.verifiedSMSList(mockContacts).toString());
    }

    @Test
    public void verifiedEmailListTest() {
        // ARRANGE
        ArrayList<ContactObject> mockContacts = new ArrayList<>();
        ContactObject object1 = new ContactObject("Success1", "test@test.com");
        ContactObject object2 = new ContactObject("Success2", "test@test1.com");

        // ACT
        mockContacts.add(object1);
        mockContacts.add(object2);
        PowerMockito.spy(BulkShareHelper.class);
        PowerMockito.when(BulkShareHelper.isValidEmail(anyString())).thenReturn(true);

        // ASSERT
        assertEquals("[test@test.com, test@test1.com]", BulkShareHelper.verifiedEmailList(mockContacts).toString());
    }

    @Test
    public void isValidEmailTest() {
        // ARRANGE
        String validAddress = "user@domain.com";
        String validAddress2 = "user@domain.co.in";
        String invalidAddress = "user#domain.com";
        String invalidAddress2 = "@yahoo.com.";

        // ASSERT
        assertEquals(true, BulkShareHelper.isValidEmail(validAddress));
        assertEquals(true, BulkShareHelper.isValidEmail(validAddress2));
        assertEquals(false, BulkShareHelper.isValidEmail(invalidAddress));
        assertEquals(false, BulkShareHelper.isValidEmail(invalidAddress2));
    }

    @Test
    public void contactArrayTest() throws Exception {
        // ARRANGE
        ArrayList<String> phoneNumberList = new ArrayList<>();
        ArrayList<String> emailList = new ArrayList<>();
        String mockShortCode = "whatever";
        String mockPhoneNumber1 = "5555555555";
        String mockPhoneNumber2 = "1234567891";
        String mockEmail = "test@test.com";

        // ACT
        phoneNumberList.add(mockPhoneNumber1);
        phoneNumberList.add(mockPhoneNumber2);
        JSONArray smsResults = bulkShareHelper.contactArray(phoneNumberList, BulkShareHelper.SocialServiceTrackType.SMS, mockShortCode);
        JSONObject smsObj = smsResults.getJSONObject(0);

        emailList.add(mockEmail);
        JSONArray emailResults = bulkShareHelper.contactArray(emailList, BulkShareHelper.SocialServiceTrackType.EMAIL, mockShortCode);
        JSONObject emailObj = emailResults.getJSONObject(0);

        JSONArray twitterResults = bulkShareHelper.contactArray(BulkShareHelper.SocialServiceTrackType.TWITTER, mockShortCode);
        JSONObject twitterObj = twitterResults.getJSONObject(0);

        JSONArray linkedInResults = bulkShareHelper.contactArray(BulkShareHelper.SocialServiceTrackType.LINKEDIN, mockShortCode);
        JSONObject linkedInObj = linkedInResults.getJSONObject(0);

        JSONArray facebookResults = bulkShareHelper.contactArray(BulkShareHelper.SocialServiceTrackType.FACEBOOK, mockShortCode);
        JSONObject facebookObj = facebookResults.getJSONObject(0);

        // ASSERT
        assertEquals(2, smsResults.length());
        assertEquals(mockPhoneNumber1, smsObj.getString("recipient_username"));
        assertEquals("", smsObj.getString("recipient_email"));
        assertEquals(mockShortCode, smsObj.getString("short_code"));
        assertEquals(BulkShareHelper.SocialServiceTrackType.SMS.toString(), smsObj.getString("social_name"));

        assertEquals(1, emailResults.length());
        assertEquals(mockEmail, emailObj.getString("recipient_email"));
        assertEquals("", emailObj.getString("recipient_username"));
        assertEquals(mockShortCode, emailObj.getString("short_code"));
        assertEquals(BulkShareHelper.SocialServiceTrackType.EMAIL.toString(), emailObj.getString("social_name"));

        assertEquals(1, twitterResults.length());
        assertEquals("", twitterObj.getString("recipient_email"));
        assertEquals("", twitterObj.getString("recipient_username"));
        assertEquals(mockShortCode, twitterObj.getString("short_code"));
        assertEquals(BulkShareHelper.SocialServiceTrackType.TWITTER.toString(), twitterObj.getString("social_name"));

        assertEquals(1, linkedInResults.length());
        assertEquals("", linkedInObj.getString("recipient_email"));
        assertEquals("", linkedInObj.getString("recipient_username"));
        assertEquals(mockShortCode, linkedInObj.getString("short_code"));
        assertEquals(BulkShareHelper.SocialServiceTrackType.LINKEDIN.toString(), linkedInObj.getString("social_name"));

        assertEquals(1, facebookResults.length());
        assertEquals("", facebookObj.getString("recipient_email"));
        assertEquals("", facebookObj.getString("recipient_username"));
        assertEquals(mockShortCode, facebookObj.getString("short_code"));
        assertEquals(BulkShareHelper.SocialServiceTrackType.FACEBOOK.toString(), facebookObj.getString("social_name"));
    }

    @Test
    public void payloadObjectForEmailTest() throws Exception {
        // ARRANGE
        ArrayList<String> emailList = new ArrayList<>();
        String mockEmail1 = "test@test.com";
        String mockEmail2 = "test2@test.com";
        String mockShortCode = "whatever";
        String mockEmailSubject = "subject";
        String mockEmailMessage = "message";

        // ACT
        emailList.add(mockEmail1);
        emailList.add(mockEmail2);
        JSONObject emailPayload = bulkShareHelper.payloadObjectForEmail(emailList, mockShortCode, mockEmailSubject, mockEmailMessage);
        JSONArray emailArray = emailPayload.getJSONArray("to_emails");

        // ASSERT
        assertEquals(2, emailArray.length());
        assertEquals(mockEmail1, emailArray.get(0));
        assertEquals(mockEmail2, emailArray.get(1));
        assertEquals(mockShortCode, emailPayload.getString("short_code"));
        assertEquals(mockEmailSubject, emailPayload.getString("subject_line"));
        assertEquals(mockEmailMessage, emailPayload.getString("message"));
    }

    @Test
    public void payloadObjectForSMSTest() throws Exception {
        // ARRANGE
        ArrayList<String> phoneNumberList = new ArrayList<>();
        String mockPhoneNumber1 = "5555555555";
        String mockPhoneNumber2 = "1234567891";
        String mockFullName = "name";
        String mockSmsMessage = "message";

        // ACT
        phoneNumberList.add(mockPhoneNumber1);
        phoneNumberList.add(mockPhoneNumber2);
        JSONObject phonePayload = bulkShareHelper.payloadObjectForSMS(phoneNumberList, mockFullName, mockSmsMessage);
        JSONArray phoneArray = phonePayload.getJSONArray("to");

        // ASSERT
        assertEquals(2, phoneArray.length());
        assertEquals(mockPhoneNumber1, phoneArray.get(0));
        assertEquals(mockPhoneNumber2, phoneArray.get(1));
        assertEquals(mockFullName, phonePayload.getString("from"));
        assertEquals(mockSmsMessage, phonePayload.getString("message"));
    }
}
