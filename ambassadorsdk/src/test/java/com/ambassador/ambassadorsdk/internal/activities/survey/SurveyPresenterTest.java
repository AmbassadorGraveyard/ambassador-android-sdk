package com.ambassador.ambassadorsdk.internal.activities.survey;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Color.class
})
public class SurveyPresenterTest {

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                Color.class
        );
    }

    @Test
    public void testsBindViewDoesLoadDataWhenModelNull() {
        SurveyPresenter surveyPresenter = Mockito.spy(new SurveyPresenter());
        SurveyView surveyView = Mockito.mock(SurveyView.class);
        Mockito.doNothing().when(surveyPresenter).loadData();
        surveyPresenter.bindView(surveyView);
        Mockito.verify(surveyPresenter).loadData();
        Mockito.verify(surveyView).showLoading();
    }

    @Test
    public void testsBindViewDoesNotLoadDataWhenModelNotNull() {
        SurveyPresenter surveyPresenter = Mockito.spy(new SurveyPresenter());
        SurveyView surveyView = Mockito.mock(SurveyView.class);
        Mockito.doNothing().when(surveyPresenter).loadData();
        surveyPresenter.setModel(new SurveyModel());
        surveyPresenter.bindView(surveyView);
        Mockito.verify(surveyPresenter, Mockito.never()).loadData();
        Mockito.verify(surveyView, Mockito.never()).showLoading();
    }

}
