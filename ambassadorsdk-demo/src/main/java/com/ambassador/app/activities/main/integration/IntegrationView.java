package com.ambassador.app.activities.main.integration;

import com.ambassador.app.data.Integration;
import com.ambassador.app.utils.Share;

import java.util.List;

public interface IntegrationView {

    void closeSoftKeyboard();
    void showEmptyListContent();
    void hideEmptyListContent();
    void showPopulatedListContent();
    void hidePopulatedListContent();
    void setListContent(List<Integration> content);
    void createIntegration();
    void toggleEditing();
    void present(Integration integration);
    void edit(Integration integration);
    void askToDelete(Integration integration);
    void share(Share share);

}
