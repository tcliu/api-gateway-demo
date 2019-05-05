package app.api.demo.util;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class NumUtilsTest {

    @Test
    public void getNums() {
        List<Integer> nums = NumUtils.getNums("1,3:5,7:10,12");
        assertThat(nums, contains(1, 3, 4, 5, 7, 8, 9, 10, 12));
    }
}
