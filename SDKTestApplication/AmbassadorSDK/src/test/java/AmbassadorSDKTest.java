import android.content.Context;
import android.content.Intent;

import com.example.ambassador.ambassadorsdk.AmbassadorSDK;
import com.example.ambassador.ambassadorsdk.ServiceSelectorPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by JakeDunahee on 9/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest (AmbassadorSDK.class)
public class AmbassadorSDKTest {
    private final AmbassadorSDK mockSDK = mock(AmbassadorSDK.class);
    String valueString;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(AmbassadorSDK.class);
    }

    @Test
    public void presentRAFTest() throws Exception {
        Context context = mock(Context.class);
        Intent mockIntent = mock(Intent.class);
        PowerMockito.whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
    }

    @Test
    public void identifyTest() {
        String identifier = "jake@getambassador.com";
    }
}
