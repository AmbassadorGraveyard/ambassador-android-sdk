package com.ambassador.ambassadorsdk;

import android.widget.Toast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static junit.framework.Assert.assertEquals;

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
public class BulkShareHelperUnitTest {
    BulkShareHelper bulkShareHelper;

    @Inject
    RequestManager mockRequestManager;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(BulkShareHelperUnitTest bulkShareHelperUnitTest);
    }

    @Before
    public void setUpMock() {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        TestComponent component = DaggerBulkShareHelperUnitTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
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
//
//    @Test
//    public void bulkSMSShareTest() throws Exception {
//        // ARRANGE
//        BulkShareSMSRequest mockRequest = mock(BulkShareSMSRequest.class);
//        AsyncTask<Void, Void, Void> mockExecuteTask = mock(AsyncTask.class);
//        ArrayList<ContactObject> mockContacts = mock(ArrayList.class);
//
//        // ACT
//        PowerMockito.whenNew(BulkShareSMSRequest.class).withAnyArguments().thenReturn(mockRequest);
//        when(mockRequest.execute()).thenReturn(mockExecuteTask);
//        bulkShareHelper.bulkShare(mockContacts, true);
//
//        // ASSERT
//        assertEquals(mockExecuteTask, mockRequest.execute());
//        verify(bulkShareHelper).bulkShare(mockContacts, true);
//    }
//
//    @Test
//    public void bulkEmailShareTest() throws Exception {
//        // ARRANGE
//        BulkShareEmailRequest mockRequest = mock(BulkShareEmailRequest.class);
//        AsyncTask<Void, Void, Void> mockExecuteTask = mock(AsyncTask.class);
//        ArrayList<ContactObject> mockContacts = mock(ArrayList.class);
//
//        // ACT
//        PowerMockito.whenNew(BulkShareEmailRequest.class).withAnyArguments().thenReturn(mockRequest);
//        when(mockRequest.execute()).thenReturn(mockExecuteTask);
//        bulkShareHelper.bulkShare(mockContacts, false);
//
//        // ASSERT
//        assertEquals(mockExecuteTask, mockRequest.execute());
//        verify(bulkShareHelper).bulkShare(mockContacts, false);
//    }
//
//    @Test
//    public void callSuccessfulTest() {
//        // ARRANGE
//        PowerMockito.mockStatic(Toast.class);
//        Toast mockToast = mock(Toast.class);
//        Activity mockOwnerActivity = mock(Activity.class);
//
//        // ACT
//        when(mockDialog.getOwnerActivity()).thenReturn(mockOwnerActivity);
//        PowerMockito.when(Toast.makeText(mockOwnerActivity, "Message successfully shared!", Toast.LENGTH_SHORT)).thenReturn(mockToast);
//        doNothing().when(mockToast).show();
//        doNothing().when(mockDialog).dismiss();
//        doNothing().when(mockOwnerActivity).finish();
//        bulkShareHelper.callIsSuccessful();
//
//        // ASSERT
//        assertEquals(mockOwnerActivity, mockDialog.getOwnerActivity());
//        verify(bulkShareHelper).callIsSuccessful();
//    }
//
//    @Test
//    public void callUnsuccessfulTest() {
//        // ARRANGE
//        PowerMockito.mockStatic(Toast.class);
//        Toast mockToast = mock(Toast.class);
//        Activity mockOwnerActivity = mock(Activity.class);
//
//        // ACT
//        when(mockDialog.getOwnerActivity()).thenReturn(mockOwnerActivity);
//        PowerMockito.when(Toast.makeText(mockOwnerActivity, "Unable to share message. Please try again.", Toast.LENGTH_SHORT)).thenReturn(mockToast);
//        doNothing().when(mockToast).show();
//        doNothing().when(mockDialog).dismiss();
//        bulkShareHelper.callIsUnsuccessful();
//
//        // ASSERT
//        verify(bulkShareHelper).callIsUnsuccessful();
//    }
//
//    // STATIC HELPER FUNCTIONS
//    @Test
//    public void verifiedSMSListTest() {
//        // ARRANGE
//        ArrayList<ContactObject> mockContacts = new ArrayList<>();
//        ContactObject passObject1 = new ContactObject("Success1", "Mobile", "815-555-4562");
//        ContactObject failObject1 = new ContactObject("Failure1", "Mobile", "4578-5648-1213");
//        ContactObject passObject2 = new ContactObject("Success2", "Mobile", "555-1234");
//        ContactObject failObject2 = new ContactObject("Failure2", "Mobile", "4567");
//        ContactObject passObject3 = new ContactObject("Success3", "Mobile", "+1-123-555-4561");
//
//        // ACT
//        mockContacts.add(passObject1);
//        mockContacts.add(failObject1);
//        mockContacts.add(passObject2);
//        mockContacts.add(failObject2);
//        mockContacts.add(passObject3);
//
//        // ASSERT
//        assertEquals("[8155554562, 5551234, 11235554561]", BulkShareHelper.verifiedSMSList(mockContacts).toString());
//    }
//
//    @Test
//    public void verifiedEmailListTest() {
//        // ARRANGE
//        ArrayList<ContactObject> mockContacts = new ArrayList<>();
//        ContactObject object1 = new ContactObject("Success1", "test@test.com");
//        ContactObject object2 = new ContactObject("Success2", "test@test1.com");
//
//        // ACT
//        mockContacts.add(object1);
//        mockContacts.add(object2);
//        PowerMockito.spy(BulkShareHelper.class);
//        PowerMockito.when(BulkShareHelper.isValidEmail(anyString())).thenReturn(true);
//
//        // ASSERT
//        assertEquals("[test@test.com, test@test1.com]", BulkShareHelper.verifiedEmailList(mockContacts).toString());
//    }
//
//    @Test
//    public void isValidEmailTest() {
//        // ARRANGE
//        String validAddress = "user@domain.com";
//        String validAddress2 = "user@domain.co.in";
//        String invalidAddress = "user#domain.com";
//        String invalidAddress2 = "@yahoo.com.";
//
//        // ASSERT
//        assertEquals(true, BulkShareHelper.isValidEmail(validAddress));
//        assertEquals(true, BulkShareHelper.isValidEmail(validAddress2));
//        assertEquals(false, BulkShareHelper.isValidEmail(invalidAddress));
//        assertEquals(false, BulkShareHelper.isValidEmail(invalidAddress2));
//    }
//
//    @Test
//    public void contactArrayTest() throws Exception {
//        // ARRANGE
//        PowerMockito.mockStatic(AmbassadorSingleton.class);
//        AmbassadorSingleton mockSingleton = mock(AmbassadorSingleton.class);
//        ArrayList<String> phoneNumberList = new ArrayList<>();
//        JSONArray mockArray = mock(JSONArray.class);
//        JSONObject mockObject = mock(JSONObject.class);
//
//        // ACT
//        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(mockArray);
//        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(mockObject);
//        when(AmbassadorSingleton.getInstance()).thenReturn(mockSingleton);
//        when(mockSingleton.getShortCode()).thenReturn("fakeShortCode");
//        when(mockObject.put(anyString(), any())).thenReturn(mockObject);
//        phoneNumberList.add("5555555555");
//        bulkShareHelper.contactArray(phoneNumberList, true);
//
//        // ASSERT
//        assertNotNull(mockObject);
//    }
//
//    @Test
//    public void payloadObjectForSMSTest() throws Exception {
//        // ARRANGE
//        PowerMockito.mockStatic(AmbassadorSingleton.class);
//        AmbassadorSingleton mockSingleton = mock(AmbassadorSingleton.class);
//        JSONObject mockObject = mock(JSONObject.class);
//        ArrayList<String> phoneNumberList = new ArrayList<>();
//
//        // ACT
//        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(mockObject);
//        when(AmbassadorSingleton.getInstance()).thenReturn(mockSingleton);
//        when(mockObject.put(anyString(), any())).thenReturn(mockObject);
//        when(mockSingleton.getFullName()).thenReturn("fakeName");
//        BulkShareHelper.payloadObjectForSMS(phoneNumberList, "test message");
//
//        // ASSERT
//        assertNotNull(mockObject);
//    }
//
//    @Test
//    public void payloadObjectForEmailTest() throws Exception {
//        // ARRANGE
//        PowerMockito.mockStatic(AmbassadorSingleton.class);
//        AmbassadorSingleton mockSingleton = mock(AmbassadorSingleton.class);
//        JSONObject mockObject = mock(JSONObject.class);
//        ArrayList<String> emailList = new ArrayList<>();
//
//        // ACT
//        PowerMockito.whenNew(JSONObject.class).withAnyArguments().thenReturn(mockObject);
//        when(AmbassadorSingleton.getInstance()).thenReturn(mockSingleton);
//        when(mockObject.put(anyString(), any())).thenReturn(mockObject);
//        when(mockSingleton.getShortCode()).thenReturn("123456");
//        when(mockSingleton.getEmailSubjectLine()).thenReturn("email subject line");
//        BulkShareHelper.payloadObjectForEmail(emailList, "test message");
//
//        // ASSERT
//        assertNotNull(mockObject);
//    }
}
