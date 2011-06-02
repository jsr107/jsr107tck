package javax.cache;

import javax.cache.implementation.RICache;

/**
 * Test Cache using Altered Null Key On Read strategy.
 * TODO: This test must be deleted once we commit to a design
 * @see javax.cache.implementation.RICache#DEFAULT_IGNORE_NULL_KEY_ON_READ
 */
public class CacheAlteredNullKeyOnReadTest extends CacheTest {
    protected boolean isIgnoreNullKeyOnRead() {
        return !super.isIgnoreNullKeyOnRead();
    }
}
