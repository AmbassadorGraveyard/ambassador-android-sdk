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
        ShareMethod.OnClickListener onClickListener = Mockito.mock(ShareMethod.OnClickListener.class);
        Mockito.doNothing().when(onClickListener).onClick();

        // ACT
        ShareMethod shareMethod = new ShareMethod.Builder()
                .setName(name)
                .setIconDrawable(iconDrawable)
                .setBackgroundColor(backgroundColor)
                .setDrawBorder(drawBorder)
                .setOnClickListener(onClickListener)
                .setWeight(weight)
                .build();

        shareMethod.click();

        // ASSERT
        Assert.assertEquals(name, shareMethod.getName());
        Assert.assertEquals(iconDrawable, shareMethod.getIconDrawable());
        Assert.assertEquals(backgroundColor, shareMethod.getBackgroundColor());
        Assert.assertEquals(drawBorder, shareMethod.willDrawBorder());
        Assert.assertEquals(weight, shareMethod.getWeight());
        Mockito.verify(onClickListener).onClick();
    }

    @Test
    public void sortTest() {
        // ARRANGE
        List<ShareMethod> toSort = new ArrayList<>();
        toSort.add(new ShareMethod.Builder().setName("a").setWeight(500).build());
        toSort.add(new ShareMethod.Builder().setName("b").setWeight(300).build());
        toSort.add(new ShareMethod.Builder().setName("c").setWeight(200).build());
        toSort.add(new ShareMethod.Builder().setName("d").setWeight(400).build());

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
        ShareMethod.OnClickListener onClickListener = Mockito.mock(ShareMethod.OnClickListener.class);
        Mockito.doNothing().when(onClickListener).onClick();

        // ACT
        ShareMethod shareMethod = new ShareMethod.Builder()
                .setOnClickListener(onClickListener)
                .build();

        shareMethod.click();

        // ASSERT
        Mockito.verify(onClickListener).onClick();
    }

    @Test
    public void clickNullTest() {
        // ARRANGE
        ShareMethod.OnClickListener onClickListener = null;

        // ACT
        ShareMethod shareMethod = new ShareMethod.Builder()
                .setOnClickListener(onClickListener)
                .build();

        shareMethod.click();

        // ASSERT
        Assert.assertNull(onClickListener);
    }

}
