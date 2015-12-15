import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.DisplayMetrics;
import android.util.Log;

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

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by dylan on 11/6/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConversionUtility.class})
public class ConversionUtilityTest {

    Context mockContext;

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(com.ambassador.ambassadorsdk.ConversionUtilityTest conversionUtilityTest);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        mockContext = mock(Context.class);

        TestComponent component = DaggerConversionUtilityTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);
    }

    @Test
    public void registerConversionTest() {

    }

    @Test
    public void createJSONConversionTest() {

    }

    @Test
    public void readAndSaveDatabaseEntriesTest() {

    }

    @Test
    public void makeConversionRequestTest() {

    }

}
