package javax.cache;

/**
 * Test Cache using Altered Null Value strategy.
 * TODO: This test must be deleted once we commit to a design
 * @see javax.cache.implementation.RICache#DEFAULT_ALLOW_NULL_VALUE
 */
public class CacheAlteredAllowNullValueTest extends CacheTest {
   protected boolean isAllowNullValue() {
       return !super.isAllowNullValue();
   }
}
