import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.test.InstrumentationTestCase;

import com.example.ambassador.ambassadorsdk.AmbassadorActivity;
import com.facebook.share.model.ShareLinkContent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AmbassadorActivityTest extends InstrumentationTestCase {

    @Mock
    Context mContext = mock(ContextWrapper.class);
    Dialog fbDialog = mock(Dialog.class);

    @Test
    public void facebook_dialog_is_initialized() {
        when(mContext.getApplicationContext()).thenReturn(mContext);

        AmbassadorActivity a = new AmbassadorActivity();
        //a.initFacebook();
        //assertThat(a.fbDialog, is(not(null)));
        a.shareWithFacebook();
        assertThat(fbDialog.isShowing(), is(true));
    }
}