package cn.tranq;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{



    @Test
    public void shouldAnswerWithTrue()
    {
        new MockUp<App>(){
            @Mock
            void run(String... args){
                Assert.assertEquals(2, args.length);
            }
        };

        try {
            App.main(new String[]{"server", "test/conf"});
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
