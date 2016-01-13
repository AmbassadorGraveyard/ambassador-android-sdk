package com.ambassador.ambassadorsdk.internal;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({

})
public class SocialGridModelTest {

    @Test
    public void getterSetterTest() {
        // ARRANGE
        String name = "name";
        int iconDrawable = 5;
        int backgroundColor = 6;
        boolean drawBorder = true;
        int weight = 500;
        SocialGridModel.OnClickListener onClickListener = Mockito.mock(SocialGridModel.OnClickListener.class);
        Mockito.doNothing().when(onClickListener).onClick();

        // ACT
        SocialGridModel socialGridModel = new SocialGridModel.Builder()
                .setName(name)
                .setIconDrawable(iconDrawable)
                .setBackgroundColor(backgroundColor)
                .setDrawBorder(drawBorder)
                .setOnClickListener(onClickListener)
                .setWeight(weight)
                .build();

        socialGridModel.click();

        // ASSERT
        Assert.assertEquals(name, socialGridModel.getName());
        Assert.assertEquals(iconDrawable, socialGridModel.getIconDrawable());
        Assert.assertEquals(backgroundColor, socialGridModel.getBackgroundColor());
        Assert.assertEquals(drawBorder, socialGridModel.willDrawBorder());
        Assert.assertEquals(weight, socialGridModel.getWeight());
        Mockito.verify(onClickListener).onClick();
    }

    @Test
    public void sortTest() {
        // ARRANGE
        List<SocialGridModel> toSort = new ArrayList<>();
        toSort.add(new SocialGridModel.Builder().setName("a").setWeight(500).build());
        toSort.add(new SocialGridModel.Builder().setName("b").setWeight(300).build());
        toSort.add(new SocialGridModel.Builder().setName("c").setWeight(200).build());
        toSort.add(new SocialGridModel.Builder().setName("d").setWeight(400).build());

        // ACT
        Collections.shuffle(toSort);
        Collections.sort(toSort);

        // ASSERT
        Assert.assertEquals(toSort.get(0).getName(), "c");
        Assert.assertEquals(toSort.get(1).getName(), "b");
        Assert.assertEquals(toSort.get(2).getName(), "d");
        Assert.assertEquals(toSort.get(3).getName(), "a");
    }

    @Test
    public void clickNotNullTest() {
        // ARRANGE
        SocialGridModel.OnClickListener onClickListener = Mockito.mock(SocialGridModel.OnClickListener.class);
        Mockito.doNothing().when(onClickListener).onClick();

        // ACT
        SocialGridModel socialGridModel = new SocialGridModel.Builder()
                .setOnClickListener(onClickListener)
                .build();

        socialGridModel.click();

        // ASSERT
        Mockito.verify(onClickListener).onClick();
    }

    @Test
    public void clickNullTest() {
        // ARRANGE
        SocialGridModel.OnClickListener onClickListener = null;

        // ACT
        SocialGridModel socialGridModel = new SocialGridModel.Builder()
                .setOnClickListener(onClickListener)
                .build();

        socialGridModel.click();

        // ASSERT
        Assert.assertNull(onClickListener);
    }

}
