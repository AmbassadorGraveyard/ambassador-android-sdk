package com.ambassador.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.app.Demo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Integration implements Comparable<Integration> {

    protected String name;
    protected int campaignId;
    protected String campaignName;
    protected RAFOptions rafOptions;
    protected long createdAtDate;

    public Integration() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public RAFOptions getRafOptions() {
        return rafOptions;
    }

    public void setRafOptions(RAFOptions rafOptions) {
        this.rafOptions = rafOptions;
    }

    public long getCreatedAtDate() {
        return createdAtDate;
    }

    public void setCreatedAtDate(long createdAtDate) {
        this.createdAtDate = createdAtDate;
    }

    public void save() {
        String jsonRepresentation = new Gson().toJson(this);
        SharedPreferences sharedPreferences = Demo.get().getSharedPreferences("integrations", Context.MODE_PRIVATE);
        String storedIntegrations = sharedPreferences.getString(User.get().getUniversalId(), "[]");
        JsonArray integrationArray = new JsonParser().parse(storedIntegrations).getAsJsonArray();
        integrationArray.add(jsonRepresentation);
        sharedPreferences.edit().putString(User.get().getUniversalId(), integrationArray.toString()).apply();
    }

    public void delete() {
        SharedPreferences sharedPreferences = Demo.get().getSharedPreferences("integrations", Context.MODE_PRIVATE);
        String storedIntegrations = sharedPreferences.getString(User.get().getUniversalId(), "[]");
        JsonArray integrationArray = new JsonParser().parse(storedIntegrations).getAsJsonArray();
        for (int i = 0; i < integrationArray.size(); i++) {
            String element = integrationArray.get(i).getAsString();
            JsonObject object = new JsonParser().parse(element).getAsJsonObject();
            Integration integration = new Gson().fromJson(object, Integration.class);
            if (integration.getCreatedAtDate() == createdAtDate) {
                integrationArray.remove(i);
            }
        }

        sharedPreferences.edit().putString(User.get().getUniversalId(), integrationArray.toString()).apply();
    }

    public static Integration get(long createdAtDate) {
        SharedPreferences sharedPreferences = Demo.get().getSharedPreferences("integrations", Context.MODE_PRIVATE);
        String storedIntegrations = sharedPreferences.getString(User.get().getUniversalId(), "[]");
        JsonArray integrationArray = new JsonParser().parse(storedIntegrations).getAsJsonArray();
        for (int i = 0; i < integrationArray.size(); i++) {
            String element = integrationArray.get(i).getAsString();
            JsonObject object = new JsonParser().parse(element).getAsJsonObject();
            Integration integration = new Gson().fromJson(object, Integration.class);
            if (integration.getCreatedAtDate() == createdAtDate) {
                return integration;
            }
        }
        return null;
    }

    @Override
    public int compareTo(Integration another) {
        return another.createdAtDate > createdAtDate ? -1 : another.createdAtDate < createdAtDate ? 1 : 0;
    }

    public boolean isEquivalentTo(Integration integration) {
        Object[] thisChecks = new Object[]{ getName(), getCampaignId(), getRafOptions().getLogo(), getRafOptions().getToolbarTitle(), getRafOptions().getHomeToolbarColor(), getRafOptions().getTitleText(), getRafOptions().getHomeWelcomeTitleColor(), getRafOptions().getDescriptionText(), getRafOptions().getHomeWelcomeDescriptionColor(), getRafOptions().getContactsSendButtonColor(), getRafOptions().getChannels() };
        Object[] anotherChecks = new Object[]{ integration.getName(), integration.getCampaignId(), integration.getRafOptions().getLogo(), integration.getRafOptions().getToolbarTitle(), integration.getRafOptions().getHomeToolbarColor(), integration.getRafOptions().getTitleText(), integration.getRafOptions().getHomeWelcomeTitleColor(), integration.getRafOptions().getDescriptionText(), integration.getRafOptions().getHomeWelcomeDescriptionColor(), integration.getRafOptions().getContactsSendButtonColor(), integration.getRafOptions().getChannels() };

        boolean changesMade = false;
        for (int i = 0; i < thisChecks.length; i++) {
            if (!integrationFieldEquals(thisChecks[i], anotherChecks[i])) {
                changesMade = true;
            }
        }

        return !changesMade;
    }

    protected boolean integrationFieldEquals(Object a, Object b) {
        if (a instanceof Object[]) {
            Object[] aArray = (Object[]) a;
            Object[] bArray = (Object[]) b;
            boolean change = false;
            for (int i = 0; i < aArray.length; i++) {
                if (!integrationFieldEquals(aArray[i], bArray[i])) {
                    change = true;
                }
            }

            return !change;
        }

        return (a != null && b != null && a.equals(b)) || a == b;
    }

}
