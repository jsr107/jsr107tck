#Signature file v4.1
#Version 1.0.0

CLSS public abstract interface java.io.Closeable
intf java.lang.AutoCloseable
meth public abstract void close() throws java.io.IOException

CLSS public abstract interface java.io.Serializable

CLSS public abstract interface java.lang.AutoCloseable
meth public abstract void close() throws java.lang.Exception

CLSS public abstract interface java.lang.Comparable<%0 extends java.lang.Object>
meth public abstract int compareTo({java.lang.Comparable%0})

CLSS public abstract java.lang.Enum<%0 extends java.lang.Enum<{java.lang.Enum%0}>>
cons protected <init>(java.lang.String,int)
intf java.io.Serializable
intf java.lang.Comparable<{java.lang.Enum%0}>
meth protected final java.lang.Object clone() throws java.lang.CloneNotSupportedException
meth protected final void finalize()
meth public final boolean equals(java.lang.Object)
meth public final int compareTo({java.lang.Enum%0})
meth public final int hashCode()
meth public final int ordinal()
meth public final java.lang.Class<{java.lang.Enum%0}> getDeclaringClass()
meth public final java.lang.String name()
meth public java.lang.String toString()
meth public static <%0 extends java.lang.Enum<{%%0}>> {%%0} valueOf(java.lang.Class<{%%0}>,java.lang.String)
supr java.lang.Object
hfds name,ordinal

CLSS public java.lang.Exception
cons protected <init>(java.lang.String,java.lang.Throwable,boolean,boolean)
cons public <init>()
cons public <init>(java.lang.String)
cons public <init>(java.lang.String,java.lang.Throwable)
cons public <init>(java.lang.Throwable)
supr java.lang.Throwable
hfds serialVersionUID

CLSS public abstract interface java.lang.Iterable<%0 extends java.lang.Object>
meth public abstract java.util.Iterator<{java.lang.Iterable%0}> iterator()
meth public java.util.Spliterator<{java.lang.Iterable%0}> spliterator()
meth public void forEach(java.util.function.Consumer<? super {java.lang.Iterable%0}>)

CLSS public java.lang.Object
cons public <init>()
meth protected java.lang.Object clone() throws java.lang.CloneNotSupportedException
meth protected void finalize() throws java.lang.Throwable
meth public boolean equals(java.lang.Object)
meth public final java.lang.Class<?> getClass()
meth public final void notify()
meth public final void notifyAll()
meth public final void wait() throws java.lang.InterruptedException
meth public final void wait(long) throws java.lang.InterruptedException
meth public final void wait(long,int) throws java.lang.InterruptedException
meth public int hashCode()
meth public java.lang.String toString()

CLSS public java.lang.RuntimeException
cons protected <init>(java.lang.String,java.lang.Throwable,boolean,boolean)
cons public <init>()
cons public <init>(java.lang.String)
cons public <init>(java.lang.String,java.lang.Throwable)
cons public <init>(java.lang.Throwable)
supr java.lang.Exception
hfds serialVersionUID

CLSS public java.lang.Throwable
cons protected <init>(java.lang.String,java.lang.Throwable,boolean,boolean)
cons public <init>()
cons public <init>(java.lang.String)
cons public <init>(java.lang.String,java.lang.Throwable)
cons public <init>(java.lang.Throwable)
intf java.io.Serializable
meth public final java.lang.Throwable[] getSuppressed()
meth public final void addSuppressed(java.lang.Throwable)
meth public java.lang.StackTraceElement[] getStackTrace()
meth public java.lang.String getLocalizedMessage()
meth public java.lang.String getMessage()
meth public java.lang.String toString()
meth public java.lang.Throwable fillInStackTrace()
meth public java.lang.Throwable getCause()
meth public java.lang.Throwable initCause(java.lang.Throwable)
meth public void printStackTrace()
meth public void printStackTrace(java.io.PrintStream)
meth public void printStackTrace(java.io.PrintWriter)
meth public void setStackTrace(java.lang.StackTraceElement[])
supr java.lang.Object
hfds CAUSE_CAPTION,EMPTY_THROWABLE_ARRAY,NULL_CAUSE_MESSAGE,SELF_SUPPRESSION_MESSAGE,SUPPRESSED_CAPTION,SUPPRESSED_SENTINEL,UNASSIGNED_STACK,backtrace,cause,detailMessage,serialVersionUID,stackTrace,suppressedExceptions
hcls PrintStreamOrWriter,SentinelHolder,WrappedPrintStream,WrappedPrintWriter

CLSS public abstract interface java.lang.annotation.Annotation
meth public abstract boolean equals(java.lang.Object)
meth public abstract int hashCode()
meth public abstract java.lang.Class<? extends java.lang.annotation.Annotation> annotationType()
meth public abstract java.lang.String toString()

CLSS public abstract interface !annotation java.lang.annotation.Documented
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation

CLSS public abstract interface !annotation java.lang.annotation.Retention
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation
meth public abstract java.lang.annotation.RetentionPolicy value()

CLSS public abstract interface !annotation java.lang.annotation.Target
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation
meth public abstract java.lang.annotation.ElementType[] value()

CLSS public abstract interface java.util.EventListener

CLSS public java.util.EventObject
cons public <init>(java.lang.Object)
fld protected java.lang.Object source
intf java.io.Serializable
meth public java.lang.Object getSource()
meth public java.lang.String toString()
supr java.lang.Object
hfds serialVersionUID

CLSS public abstract interface java.util.concurrent.Future<%0 extends java.lang.Object>
meth public abstract boolean cancel(boolean)
meth public abstract boolean isCancelled()
meth public abstract boolean isDone()
meth public abstract {java.util.concurrent.Future%0} get() throws java.lang.InterruptedException,java.util.concurrent.ExecutionException
meth public abstract {java.util.concurrent.Future%0} get(long,java.util.concurrent.TimeUnit) throws java.lang.InterruptedException,java.util.concurrent.ExecutionException,java.util.concurrent.TimeoutException

CLSS public abstract interface javax.cache.Cache<%0 extends java.lang.Object, %1 extends java.lang.Object>
innr public abstract interface static Entry
intf java.io.Closeable
intf java.lang.Iterable<javax.cache.Cache$Entry<{javax.cache.Cache%0},{javax.cache.Cache%1}>>
meth public abstract !varargs <%0 extends java.lang.Object> java.util.Map<{javax.cache.Cache%0},javax.cache.processor.EntryProcessorResult<{%%0}>> invokeAll(java.util.Set<? extends {javax.cache.Cache%0}>,javax.cache.processor.EntryProcessor<{javax.cache.Cache%0},{javax.cache.Cache%1},{%%0}>,java.lang.Object[])
meth public abstract !varargs <%0 extends java.lang.Object> {%%0} invoke({javax.cache.Cache%0},javax.cache.processor.EntryProcessor<{javax.cache.Cache%0},{javax.cache.Cache%1},{%%0}>,java.lang.Object[])
meth public abstract <%0 extends java.lang.Object> {%%0} unwrap(java.lang.Class<{%%0}>)
meth public abstract <%0 extends javax.cache.configuration.Configuration<{javax.cache.Cache%0},{javax.cache.Cache%1}>> {%%0} getConfiguration(java.lang.Class<{%%0}>)
meth public abstract boolean containsKey({javax.cache.Cache%0})
meth public abstract boolean isClosed()
meth public abstract boolean putIfAbsent({javax.cache.Cache%0},{javax.cache.Cache%1})
meth public abstract boolean remove({javax.cache.Cache%0})
meth public abstract boolean remove({javax.cache.Cache%0},{javax.cache.Cache%1})
meth public abstract boolean replace({javax.cache.Cache%0},{javax.cache.Cache%1})
meth public abstract boolean replace({javax.cache.Cache%0},{javax.cache.Cache%1},{javax.cache.Cache%1})
meth public abstract java.lang.String getName()
meth public abstract java.util.Iterator<javax.cache.Cache$Entry<{javax.cache.Cache%0},{javax.cache.Cache%1}>> iterator()
meth public abstract java.util.Map<{javax.cache.Cache%0},{javax.cache.Cache%1}> getAll(java.util.Set<? extends {javax.cache.Cache%0}>)
meth public abstract javax.cache.CacheManager getCacheManager()
meth public abstract void clear()
meth public abstract void close()
meth public abstract void deregisterCacheEntryListener(javax.cache.configuration.CacheEntryListenerConfiguration<{javax.cache.Cache%0},{javax.cache.Cache%1}>)
meth public abstract void loadAll(java.util.Set<? extends {javax.cache.Cache%0}>,boolean,javax.cache.integration.CompletionListener)
meth public abstract void put({javax.cache.Cache%0},{javax.cache.Cache%1})
meth public abstract void putAll(java.util.Map<? extends {javax.cache.Cache%0},? extends {javax.cache.Cache%1}>)
meth public abstract void registerCacheEntryListener(javax.cache.configuration.CacheEntryListenerConfiguration<{javax.cache.Cache%0},{javax.cache.Cache%1}>)
meth public abstract void removeAll()
meth public abstract void removeAll(java.util.Set<? extends {javax.cache.Cache%0}>)
meth public abstract {javax.cache.Cache%1} get({javax.cache.Cache%0})
meth public abstract {javax.cache.Cache%1} getAndPut({javax.cache.Cache%0},{javax.cache.Cache%1})
meth public abstract {javax.cache.Cache%1} getAndRemove({javax.cache.Cache%0})
meth public abstract {javax.cache.Cache%1} getAndReplace({javax.cache.Cache%0},{javax.cache.Cache%1})

CLSS public abstract interface static javax.cache.Cache$Entry<%0 extends java.lang.Object, %1 extends java.lang.Object>
 outer javax.cache.Cache
meth public abstract <%0 extends java.lang.Object> {%%0} unwrap(java.lang.Class<{%%0}>)
meth public abstract {javax.cache.Cache$Entry%0} getKey()
meth public abstract {javax.cache.Cache$Entry%1} getValue()

CLSS public javax.cache.CacheException
cons public <init>()
cons public <init>(java.lang.String)
cons public <init>(java.lang.String,java.lang.Throwable)
cons public <init>(java.lang.Throwable)
supr java.lang.RuntimeException
hfds serialVersionUID

CLSS public abstract interface javax.cache.CacheManager
intf java.io.Closeable
meth public abstract <%0 extends java.lang.Object, %1 extends java.lang.Object, %2 extends javax.cache.configuration.Configuration<{%%0},{%%1}>> javax.cache.Cache<{%%0},{%%1}> createCache(java.lang.String,{%%2})
meth public abstract <%0 extends java.lang.Object, %1 extends java.lang.Object> javax.cache.Cache<{%%0},{%%1}> getCache(java.lang.String)
meth public abstract <%0 extends java.lang.Object, %1 extends java.lang.Object> javax.cache.Cache<{%%0},{%%1}> getCache(java.lang.String,java.lang.Class<{%%0}>,java.lang.Class<{%%1}>)
meth public abstract <%0 extends java.lang.Object> {%%0} unwrap(java.lang.Class<{%%0}>)
meth public abstract boolean isClosed()
meth public abstract java.lang.ClassLoader getClassLoader()
meth public abstract java.lang.Iterable<java.lang.String> getCacheNames()
meth public abstract java.net.URI getURI()
meth public abstract java.util.Properties getProperties()
meth public abstract javax.cache.spi.CachingProvider getCachingProvider()
meth public abstract void close()
meth public abstract void destroyCache(java.lang.String)
meth public abstract void enableManagement(java.lang.String,boolean)
meth public abstract void enableStatistics(java.lang.String,boolean)

CLSS public final javax.cache.Caching
fld public final static java.lang.String JAVAX_CACHE_CACHING_PROVIDER = "javax.cache.spi.CachingProvider"
meth public static <%0 extends java.lang.Object, %1 extends java.lang.Object> javax.cache.Cache<{%%0},{%%1}> getCache(java.lang.String,java.lang.Class<{%%0}>,java.lang.Class<{%%1}>)
meth public static java.lang.ClassLoader getDefaultClassLoader()
meth public static java.lang.Iterable<javax.cache.spi.CachingProvider> getCachingProviders()
meth public static java.lang.Iterable<javax.cache.spi.CachingProvider> getCachingProviders(java.lang.ClassLoader)
meth public static javax.cache.spi.CachingProvider getCachingProvider()
meth public static javax.cache.spi.CachingProvider getCachingProvider(java.lang.ClassLoader)
meth public static javax.cache.spi.CachingProvider getCachingProvider(java.lang.String)
meth public static javax.cache.spi.CachingProvider getCachingProvider(java.lang.String,java.lang.ClassLoader)
meth public static void setDefaultClassLoader(java.lang.ClassLoader)
supr java.lang.Object
hfds CACHING_PROVIDERS
hcls CachingProviderRegistry

CLSS public abstract interface !annotation javax.cache.annotation.CacheDefaults
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[TYPE])
intf java.lang.annotation.Annotation
meth public abstract !hasdefault java.lang.Class<? extends javax.cache.annotation.CacheKeyGenerator> cacheKeyGenerator()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends javax.cache.annotation.CacheResolverFactory> cacheResolverFactory()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.String cacheName()
 anno 0 javax.enterprise.util.Nonbinding()

CLSS public abstract interface javax.cache.annotation.CacheInvocationContext<%0 extends java.lang.annotation.Annotation>
intf javax.cache.annotation.CacheMethodDetails<{javax.cache.annotation.CacheInvocationContext%0}>
meth public abstract <%0 extends java.lang.Object> {%%0} unwrap(java.lang.Class<{%%0}>)
meth public abstract java.lang.Object getTarget()
meth public abstract javax.cache.annotation.CacheInvocationParameter[] getAllParameters()

CLSS public abstract interface javax.cache.annotation.CacheInvocationParameter
meth public abstract int getParameterPosition()
meth public abstract java.lang.Class<?> getRawType()
meth public abstract java.lang.Object getValue()
meth public abstract java.util.Set<java.lang.annotation.Annotation> getAnnotations()

CLSS public abstract interface !annotation javax.cache.annotation.CacheKey
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[PARAMETER])
intf java.lang.annotation.Annotation

CLSS public abstract interface javax.cache.annotation.CacheKeyGenerator
meth public abstract javax.cache.annotation.GeneratedCacheKey generateCacheKey(javax.cache.annotation.CacheKeyInvocationContext<? extends java.lang.annotation.Annotation>)

CLSS public abstract interface javax.cache.annotation.CacheKeyInvocationContext<%0 extends java.lang.annotation.Annotation>
intf javax.cache.annotation.CacheInvocationContext<{javax.cache.annotation.CacheKeyInvocationContext%0}>
meth public abstract javax.cache.annotation.CacheInvocationParameter getValueParameter()
meth public abstract javax.cache.annotation.CacheInvocationParameter[] getKeyParameters()

CLSS public abstract interface javax.cache.annotation.CacheMethodDetails<%0 extends java.lang.annotation.Annotation>
meth public abstract java.lang.String getCacheName()
meth public abstract java.lang.reflect.Method getMethod()
meth public abstract java.util.Set<java.lang.annotation.Annotation> getAnnotations()
meth public abstract {javax.cache.annotation.CacheMethodDetails%0} getCacheAnnotation()

CLSS public abstract interface !annotation javax.cache.annotation.CachePut
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[METHOD, TYPE])
intf java.lang.annotation.Annotation
meth public abstract !hasdefault boolean afterInvocation()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends java.lang.Throwable>[] cacheFor()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends java.lang.Throwable>[] noCacheFor()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends javax.cache.annotation.CacheKeyGenerator> cacheKeyGenerator()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends javax.cache.annotation.CacheResolverFactory> cacheResolverFactory()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.String cacheName()
 anno 0 javax.enterprise.util.Nonbinding()

CLSS public abstract interface !annotation javax.cache.annotation.CacheRemove
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[METHOD, TYPE])
intf java.lang.annotation.Annotation
meth public abstract !hasdefault boolean afterInvocation()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends java.lang.Throwable>[] evictFor()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends java.lang.Throwable>[] noEvictFor()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends javax.cache.annotation.CacheKeyGenerator> cacheKeyGenerator()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends javax.cache.annotation.CacheResolverFactory> cacheResolverFactory()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.String cacheName()
 anno 0 javax.enterprise.util.Nonbinding()

CLSS public abstract interface !annotation javax.cache.annotation.CacheRemoveAll
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[METHOD, TYPE])
intf java.lang.annotation.Annotation
meth public abstract !hasdefault boolean afterInvocation()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends java.lang.Throwable>[] evictFor()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends java.lang.Throwable>[] noEvictFor()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends javax.cache.annotation.CacheResolverFactory> cacheResolverFactory()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.String cacheName()
 anno 0 javax.enterprise.util.Nonbinding()

CLSS public abstract interface javax.cache.annotation.CacheResolver
meth public abstract <%0 extends java.lang.Object, %1 extends java.lang.Object> javax.cache.Cache<{%%0},{%%1}> resolveCache(javax.cache.annotation.CacheInvocationContext<? extends java.lang.annotation.Annotation>)

CLSS public abstract interface javax.cache.annotation.CacheResolverFactory
meth public abstract javax.cache.annotation.CacheResolver getCacheResolver(javax.cache.annotation.CacheMethodDetails<? extends java.lang.annotation.Annotation>)
meth public abstract javax.cache.annotation.CacheResolver getExceptionCacheResolver(javax.cache.annotation.CacheMethodDetails<javax.cache.annotation.CacheResult>)

CLSS public abstract interface !annotation javax.cache.annotation.CacheResult
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[METHOD, TYPE])
intf java.lang.annotation.Annotation
meth public abstract !hasdefault boolean skipGet()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends java.lang.Throwable>[] cachedExceptions()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends java.lang.Throwable>[] nonCachedExceptions()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends javax.cache.annotation.CacheKeyGenerator> cacheKeyGenerator()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.Class<? extends javax.cache.annotation.CacheResolverFactory> cacheResolverFactory()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.String cacheName()
 anno 0 javax.enterprise.util.Nonbinding()
meth public abstract !hasdefault java.lang.String exceptionCacheName()
 anno 0 javax.enterprise.util.Nonbinding()

CLSS public abstract interface !annotation javax.cache.annotation.CacheValue
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[PARAMETER])
intf java.lang.annotation.Annotation

CLSS public abstract interface javax.cache.annotation.GeneratedCacheKey
intf java.io.Serializable
meth public abstract boolean equals(java.lang.Object)
meth public abstract int hashCode()

CLSS public abstract interface javax.cache.configuration.CacheEntryListenerConfiguration<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf java.io.Serializable
meth public abstract boolean isOldValueRequired()
meth public abstract boolean isSynchronous()
meth public abstract javax.cache.configuration.Factory<javax.cache.event.CacheEntryEventFilter<? super {javax.cache.configuration.CacheEntryListenerConfiguration%0},? super {javax.cache.configuration.CacheEntryListenerConfiguration%1}>> getCacheEntryEventFilterFactory()
meth public abstract javax.cache.configuration.Factory<javax.cache.event.CacheEntryListener<? super {javax.cache.configuration.CacheEntryListenerConfiguration%0},? super {javax.cache.configuration.CacheEntryListenerConfiguration%1}>> getCacheEntryListenerFactory()

CLSS public abstract interface javax.cache.configuration.CompleteConfiguration<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf java.io.Serializable
intf javax.cache.configuration.Configuration<{javax.cache.configuration.CompleteConfiguration%0},{javax.cache.configuration.CompleteConfiguration%1}>
meth public abstract boolean isManagementEnabled()
meth public abstract boolean isReadThrough()
meth public abstract boolean isStatisticsEnabled()
meth public abstract boolean isWriteThrough()
meth public abstract java.lang.Iterable<javax.cache.configuration.CacheEntryListenerConfiguration<{javax.cache.configuration.CompleteConfiguration%0},{javax.cache.configuration.CompleteConfiguration%1}>> getCacheEntryListenerConfigurations()
meth public abstract javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy> getExpiryPolicyFactory()
meth public abstract javax.cache.configuration.Factory<javax.cache.integration.CacheLoader<{javax.cache.configuration.CompleteConfiguration%0},{javax.cache.configuration.CompleteConfiguration%1}>> getCacheLoaderFactory()
meth public abstract javax.cache.configuration.Factory<javax.cache.integration.CacheWriter<? super {javax.cache.configuration.CompleteConfiguration%0},? super {javax.cache.configuration.CompleteConfiguration%1}>> getCacheWriterFactory()

CLSS public abstract interface javax.cache.configuration.Configuration<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf java.io.Serializable
meth public abstract boolean isStoreByValue()
meth public abstract java.lang.Class<{javax.cache.configuration.Configuration%0}> getKeyType()
meth public abstract java.lang.Class<{javax.cache.configuration.Configuration%1}> getValueType()

CLSS public abstract interface javax.cache.configuration.Factory<%0 extends java.lang.Object>
intf java.io.Serializable
meth public abstract {javax.cache.configuration.Factory%0} create()

CLSS public final javax.cache.configuration.FactoryBuilder
innr public static ClassFactory
innr public static SingletonFactory
meth public static <%0 extends java.io.Serializable> javax.cache.configuration.Factory<{%%0}> factoryOf({%%0})
meth public static <%0 extends java.lang.Object> javax.cache.configuration.Factory<{%%0}> factoryOf(java.lang.Class<{%%0}>)
meth public static <%0 extends java.lang.Object> javax.cache.configuration.Factory<{%%0}> factoryOf(java.lang.String)
supr java.lang.Object

CLSS public static javax.cache.configuration.FactoryBuilder$ClassFactory<%0 extends java.lang.Object>
 outer javax.cache.configuration.FactoryBuilder
cons public <init>(java.lang.Class<{javax.cache.configuration.FactoryBuilder$ClassFactory%0}>)
cons public <init>(java.lang.String)
fld public final static long serialVersionUID = 201305101626
intf java.io.Serializable
intf javax.cache.configuration.Factory<{javax.cache.configuration.FactoryBuilder$ClassFactory%0}>
meth public boolean equals(java.lang.Object)
meth public int hashCode()
meth public {javax.cache.configuration.FactoryBuilder$ClassFactory%0} create()
supr java.lang.Object
hfds className

CLSS public static javax.cache.configuration.FactoryBuilder$SingletonFactory<%0 extends java.lang.Object>
 outer javax.cache.configuration.FactoryBuilder
cons public <init>({javax.cache.configuration.FactoryBuilder$SingletonFactory%0})
fld public final static long serialVersionUID = 201305101634
intf java.io.Serializable
intf javax.cache.configuration.Factory<{javax.cache.configuration.FactoryBuilder$SingletonFactory%0}>
meth public boolean equals(java.lang.Object)
meth public int hashCode()
meth public {javax.cache.configuration.FactoryBuilder$SingletonFactory%0} create()
supr java.lang.Object
hfds instance

CLSS public javax.cache.configuration.MutableCacheEntryListenerConfiguration<%0 extends java.lang.Object, %1 extends java.lang.Object>
cons public <init>(javax.cache.configuration.CacheEntryListenerConfiguration<{javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},{javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}>)
cons public <init>(javax.cache.configuration.Factory<? extends javax.cache.event.CacheEntryListener<? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}>>,javax.cache.configuration.Factory<? extends javax.cache.event.CacheEntryEventFilter<? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}>>,boolean,boolean)
fld public final static long serialVersionUID = 201306200822
intf javax.cache.configuration.CacheEntryListenerConfiguration<{javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},{javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}>
meth public boolean equals(java.lang.Object)
meth public boolean isOldValueRequired()
meth public boolean isSynchronous()
meth public int hashCode()
meth public javax.cache.configuration.Factory<javax.cache.event.CacheEntryEventFilter<? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}>> getCacheEntryEventFilterFactory()
meth public javax.cache.configuration.Factory<javax.cache.event.CacheEntryListener<? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}>> getCacheEntryListenerFactory()
meth public javax.cache.configuration.MutableCacheEntryListenerConfiguration<{javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},{javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}> setCacheEntryEventFilterFactory(javax.cache.configuration.Factory<? extends javax.cache.event.CacheEntryEventFilter<? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}>>)
meth public javax.cache.configuration.MutableCacheEntryListenerConfiguration<{javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},{javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}> setCacheEntryListenerFactory(javax.cache.configuration.Factory<? extends javax.cache.event.CacheEntryListener<? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},? super {javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}>>)
meth public javax.cache.configuration.MutableCacheEntryListenerConfiguration<{javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},{javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}> setOldValueRequired(boolean)
meth public javax.cache.configuration.MutableCacheEntryListenerConfiguration<{javax.cache.configuration.MutableCacheEntryListenerConfiguration%0},{javax.cache.configuration.MutableCacheEntryListenerConfiguration%1}> setSynchronous(boolean)
supr java.lang.Object
hfds filterFactory,isOldValueRequired,isSynchronous,listenerFactory

CLSS public javax.cache.configuration.MutableConfiguration<%0 extends java.lang.Object, %1 extends java.lang.Object>
cons public <init>()
cons public <init>(javax.cache.configuration.CompleteConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}>)
fld protected boolean isManagementEnabled
fld protected boolean isReadThrough
fld protected boolean isStatisticsEnabled
fld protected boolean isStoreByValue
fld protected boolean isWriteThrough
fld protected java.lang.Class<{javax.cache.configuration.MutableConfiguration%0}> keyType
fld protected java.lang.Class<{javax.cache.configuration.MutableConfiguration%1}> valueType
fld protected java.util.HashSet<javax.cache.configuration.CacheEntryListenerConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}>> listenerConfigurations
fld protected javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy> expiryPolicyFactory
fld protected javax.cache.configuration.Factory<javax.cache.integration.CacheLoader<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}>> cacheLoaderFactory
fld protected javax.cache.configuration.Factory<javax.cache.integration.CacheWriter<? super {javax.cache.configuration.MutableConfiguration%0},? super {javax.cache.configuration.MutableConfiguration%1}>> cacheWriterFactory
fld public final static long serialVersionUID = 201306200821
intf javax.cache.configuration.CompleteConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}>
meth public boolean equals(java.lang.Object)
meth public boolean isManagementEnabled()
meth public boolean isReadThrough()
meth public boolean isStatisticsEnabled()
meth public boolean isStoreByValue()
meth public boolean isWriteThrough()
meth public int hashCode()
meth public java.lang.Class<{javax.cache.configuration.MutableConfiguration%0}> getKeyType()
meth public java.lang.Class<{javax.cache.configuration.MutableConfiguration%1}> getValueType()
meth public java.lang.Iterable<javax.cache.configuration.CacheEntryListenerConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}>> getCacheEntryListenerConfigurations()
meth public javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy> getExpiryPolicyFactory()
meth public javax.cache.configuration.Factory<javax.cache.integration.CacheLoader<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}>> getCacheLoaderFactory()
meth public javax.cache.configuration.Factory<javax.cache.integration.CacheWriter<? super {javax.cache.configuration.MutableConfiguration%0},? super {javax.cache.configuration.MutableConfiguration%1}>> getCacheWriterFactory()
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> addCacheEntryListenerConfiguration(javax.cache.configuration.CacheEntryListenerConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}>)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> removeCacheEntryListenerConfiguration(javax.cache.configuration.CacheEntryListenerConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}>)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> setCacheLoaderFactory(javax.cache.configuration.Factory<? extends javax.cache.integration.CacheLoader<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}>>)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> setCacheWriterFactory(javax.cache.configuration.Factory<? extends javax.cache.integration.CacheWriter<? super {javax.cache.configuration.MutableConfiguration%0},? super {javax.cache.configuration.MutableConfiguration%1}>>)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> setExpiryPolicyFactory(javax.cache.configuration.Factory<? extends javax.cache.expiry.ExpiryPolicy>)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> setManagementEnabled(boolean)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> setReadThrough(boolean)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> setStatisticsEnabled(boolean)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> setStoreByValue(boolean)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> setTypes(java.lang.Class<{javax.cache.configuration.MutableConfiguration%0}>,java.lang.Class<{javax.cache.configuration.MutableConfiguration%1}>)
meth public javax.cache.configuration.MutableConfiguration<{javax.cache.configuration.MutableConfiguration%0},{javax.cache.configuration.MutableConfiguration%1}> setWriteThrough(boolean)
supr java.lang.Object

CLSS public final !enum javax.cache.configuration.OptionalFeature
fld public final static javax.cache.configuration.OptionalFeature STORE_BY_REFERENCE
meth public static javax.cache.configuration.OptionalFeature valueOf(java.lang.String)
meth public static javax.cache.configuration.OptionalFeature[] values()
supr java.lang.Enum<javax.cache.configuration.OptionalFeature>

CLSS public abstract interface javax.cache.event.CacheEntryCreatedListener<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf javax.cache.event.CacheEntryListener<{javax.cache.event.CacheEntryCreatedListener%0},{javax.cache.event.CacheEntryCreatedListener%1}>
meth public abstract void onCreated(java.lang.Iterable<javax.cache.event.CacheEntryEvent<? extends {javax.cache.event.CacheEntryCreatedListener%0},? extends {javax.cache.event.CacheEntryCreatedListener%1}>>)

CLSS public abstract javax.cache.event.CacheEntryEvent<%0 extends java.lang.Object, %1 extends java.lang.Object>
cons public <init>(javax.cache.Cache,javax.cache.event.EventType)
intf javax.cache.Cache$Entry<{javax.cache.event.CacheEntryEvent%0},{javax.cache.event.CacheEntryEvent%1}>
meth public abstract boolean isOldValueAvailable()
meth public abstract {javax.cache.event.CacheEntryEvent%1} getOldValue()
meth public final javax.cache.Cache getSource()
meth public final javax.cache.event.EventType getEventType()
supr java.util.EventObject
hfds eventType

CLSS public abstract interface javax.cache.event.CacheEntryEventFilter<%0 extends java.lang.Object, %1 extends java.lang.Object>
meth public abstract boolean evaluate(javax.cache.event.CacheEntryEvent<? extends {javax.cache.event.CacheEntryEventFilter%0},? extends {javax.cache.event.CacheEntryEventFilter%1}>)

CLSS public abstract interface javax.cache.event.CacheEntryExpiredListener<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf javax.cache.event.CacheEntryListener<{javax.cache.event.CacheEntryExpiredListener%0},{javax.cache.event.CacheEntryExpiredListener%1}>
meth public abstract void onExpired(java.lang.Iterable<javax.cache.event.CacheEntryEvent<? extends {javax.cache.event.CacheEntryExpiredListener%0},? extends {javax.cache.event.CacheEntryExpiredListener%1}>>)

CLSS public abstract interface javax.cache.event.CacheEntryListener<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf java.util.EventListener

CLSS public javax.cache.event.CacheEntryListenerException
cons public <init>()
cons public <init>(java.lang.String)
cons public <init>(java.lang.String,java.lang.Throwable)
cons public <init>(java.lang.Throwable)
supr javax.cache.CacheException
hfds serialVersionUID

CLSS public abstract interface javax.cache.event.CacheEntryRemovedListener<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf javax.cache.event.CacheEntryListener<{javax.cache.event.CacheEntryRemovedListener%0},{javax.cache.event.CacheEntryRemovedListener%1}>
meth public abstract void onRemoved(java.lang.Iterable<javax.cache.event.CacheEntryEvent<? extends {javax.cache.event.CacheEntryRemovedListener%0},? extends {javax.cache.event.CacheEntryRemovedListener%1}>>)

CLSS public abstract interface javax.cache.event.CacheEntryUpdatedListener<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf javax.cache.event.CacheEntryListener<{javax.cache.event.CacheEntryUpdatedListener%0},{javax.cache.event.CacheEntryUpdatedListener%1}>
meth public abstract void onUpdated(java.lang.Iterable<javax.cache.event.CacheEntryEvent<? extends {javax.cache.event.CacheEntryUpdatedListener%0},? extends {javax.cache.event.CacheEntryUpdatedListener%1}>>)

CLSS public final !enum javax.cache.event.EventType
fld public final static javax.cache.event.EventType CREATED
fld public final static javax.cache.event.EventType EXPIRED
fld public final static javax.cache.event.EventType REMOVED
fld public final static javax.cache.event.EventType UPDATED
meth public static javax.cache.event.EventType valueOf(java.lang.String)
meth public static javax.cache.event.EventType[] values()
supr java.lang.Enum<javax.cache.event.EventType>

CLSS public final javax.cache.expiry.AccessedExpiryPolicy
cons public <init>(javax.cache.expiry.Duration)
fld public final static long serialVersionUID = 201305101601
intf java.io.Serializable
intf javax.cache.expiry.ExpiryPolicy
meth public boolean equals(java.lang.Object)
meth public int hashCode()
meth public javax.cache.expiry.Duration getExpiryForAccess()
meth public javax.cache.expiry.Duration getExpiryForCreation()
meth public javax.cache.expiry.Duration getExpiryForUpdate()
meth public static javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy> factoryOf(javax.cache.expiry.Duration)
supr java.lang.Object
hfds expiryDuration

CLSS public final javax.cache.expiry.CreatedExpiryPolicy
cons public <init>(javax.cache.expiry.Duration)
fld public final static long serialVersionUID = 201305291023
intf java.io.Serializable
intf javax.cache.expiry.ExpiryPolicy
meth public boolean equals(java.lang.Object)
meth public int hashCode()
meth public javax.cache.expiry.Duration getExpiryForAccess()
meth public javax.cache.expiry.Duration getExpiryForCreation()
meth public javax.cache.expiry.Duration getExpiryForUpdate()
meth public static javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy> factoryOf(javax.cache.expiry.Duration)
supr java.lang.Object
hfds expiryDuration

CLSS public javax.cache.expiry.Duration
cons public <init>()
cons public <init>(java.util.concurrent.TimeUnit,long)
cons public <init>(long,long)
fld public final static javax.cache.expiry.Duration ETERNAL
fld public final static javax.cache.expiry.Duration FIVE_MINUTES
fld public final static javax.cache.expiry.Duration ONE_DAY
fld public final static javax.cache.expiry.Duration ONE_HOUR
fld public final static javax.cache.expiry.Duration ONE_MINUTE
fld public final static javax.cache.expiry.Duration TEN_MINUTES
fld public final static javax.cache.expiry.Duration THIRTY_MINUTES
fld public final static javax.cache.expiry.Duration TWENTY_MINUTES
fld public final static javax.cache.expiry.Duration ZERO
fld public final static long serialVersionUID = 201305101442
intf java.io.Serializable
meth public boolean equals(java.lang.Object)
meth public boolean isEternal()
meth public boolean isZero()
meth public int hashCode()
meth public java.util.concurrent.TimeUnit getTimeUnit()
meth public long getAdjustedTime(long)
meth public long getDurationAmount()
supr java.lang.Object
hfds durationAmount,timeUnit

CLSS public final javax.cache.expiry.EternalExpiryPolicy
cons public <init>()
fld public final static long serialVersionUID = 201305101603
intf java.io.Serializable
intf javax.cache.expiry.ExpiryPolicy
meth public boolean equals(java.lang.Object)
meth public int hashCode()
meth public javax.cache.expiry.Duration getExpiryForAccess()
meth public javax.cache.expiry.Duration getExpiryForCreation()
meth public javax.cache.expiry.Duration getExpiryForUpdate()
meth public static javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy> factoryOf()
supr java.lang.Object

CLSS public abstract interface javax.cache.expiry.ExpiryPolicy
meth public abstract javax.cache.expiry.Duration getExpiryForAccess()
meth public abstract javax.cache.expiry.Duration getExpiryForCreation()
meth public abstract javax.cache.expiry.Duration getExpiryForUpdate()

CLSS public final javax.cache.expiry.ModifiedExpiryPolicy
cons public <init>(javax.cache.expiry.Duration)
fld public final static long serialVersionUID = 201305101602
intf java.io.Serializable
intf javax.cache.expiry.ExpiryPolicy
meth public boolean equals(java.lang.Object)
meth public int hashCode()
meth public javax.cache.expiry.Duration getExpiryForAccess()
meth public javax.cache.expiry.Duration getExpiryForCreation()
meth public javax.cache.expiry.Duration getExpiryForUpdate()
meth public static javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy> factoryOf(javax.cache.expiry.Duration)
supr java.lang.Object
hfds expiryDuration

CLSS public final javax.cache.expiry.TouchedExpiryPolicy
cons public <init>(javax.cache.expiry.Duration)
fld public final static long serialVersionUID = 201305291023
intf java.io.Serializable
intf javax.cache.expiry.ExpiryPolicy
meth public boolean equals(java.lang.Object)
meth public int hashCode()
meth public javax.cache.expiry.Duration getExpiryForAccess()
meth public javax.cache.expiry.Duration getExpiryForCreation()
meth public javax.cache.expiry.Duration getExpiryForUpdate()
meth public static javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy> factoryOf(javax.cache.expiry.Duration)
supr java.lang.Object
hfds expiryDuration

CLSS public abstract interface javax.cache.integration.CacheLoader<%0 extends java.lang.Object, %1 extends java.lang.Object>
meth public abstract java.util.Map<{javax.cache.integration.CacheLoader%0},{javax.cache.integration.CacheLoader%1}> loadAll(java.lang.Iterable<? extends {javax.cache.integration.CacheLoader%0}>)
meth public abstract {javax.cache.integration.CacheLoader%1} load({javax.cache.integration.CacheLoader%0})

CLSS public javax.cache.integration.CacheLoaderException
cons public <init>()
cons public <init>(java.lang.String)
cons public <init>(java.lang.String,java.lang.Throwable)
cons public <init>(java.lang.Throwable)
supr javax.cache.CacheException
hfds serialVersionUID

CLSS public abstract interface javax.cache.integration.CacheWriter<%0 extends java.lang.Object, %1 extends java.lang.Object>
meth public abstract void delete(java.lang.Object)
meth public abstract void deleteAll(java.util.Collection<?>)
meth public abstract void write(javax.cache.Cache$Entry<? extends {javax.cache.integration.CacheWriter%0},? extends {javax.cache.integration.CacheWriter%1}>)
meth public abstract void writeAll(java.util.Collection<javax.cache.Cache$Entry<? extends {javax.cache.integration.CacheWriter%0},? extends {javax.cache.integration.CacheWriter%1}>>)

CLSS public javax.cache.integration.CacheWriterException
cons public <init>()
cons public <init>(java.lang.String)
cons public <init>(java.lang.String,java.lang.Throwable)
cons public <init>(java.lang.Throwable)
supr javax.cache.CacheException
hfds serialVersionUID

CLSS public abstract interface javax.cache.integration.CompletionListener
meth public abstract void onCompletion()
meth public abstract void onException(java.lang.Exception)

CLSS public javax.cache.integration.CompletionListenerFuture
cons public <init>()
intf java.util.concurrent.Future<java.lang.Void>
intf javax.cache.integration.CompletionListener
meth public boolean cancel(boolean)
meth public boolean isCancelled()
meth public boolean isDone()
meth public java.lang.Void get() throws java.lang.InterruptedException,java.util.concurrent.ExecutionException
meth public java.lang.Void get(long,java.util.concurrent.TimeUnit) throws java.lang.InterruptedException,java.util.concurrent.ExecutionException,java.util.concurrent.TimeoutException
meth public void onCompletion()
meth public void onException(java.lang.Exception)
supr java.lang.Object
hfds exception,isCompleted

CLSS public abstract interface javax.cache.management.CacheMXBean
 anno 0 javax.management.MXBean(boolean value=true)
meth public abstract boolean isManagementEnabled()
meth public abstract boolean isReadThrough()
meth public abstract boolean isStatisticsEnabled()
meth public abstract boolean isStoreByValue()
meth public abstract boolean isWriteThrough()
meth public abstract java.lang.String getKeyType()
meth public abstract java.lang.String getValueType()

CLSS public abstract interface javax.cache.management.CacheStatisticsMXBean
 anno 0 javax.management.MXBean(boolean value=true)
meth public abstract float getAverageGetTime()
meth public abstract float getAveragePutTime()
meth public abstract float getAverageRemoveTime()
meth public abstract float getCacheHitPercentage()
meth public abstract float getCacheMissPercentage()
meth public abstract long getCacheEvictions()
meth public abstract long getCacheGets()
meth public abstract long getCacheHits()
meth public abstract long getCacheMisses()
meth public abstract long getCachePuts()
meth public abstract long getCacheRemovals()
meth public abstract void clear()

CLSS public abstract interface javax.cache.processor.EntryProcessor<%0 extends java.lang.Object, %1 extends java.lang.Object, %2 extends java.lang.Object>
meth public abstract !varargs {javax.cache.processor.EntryProcessor%2} process(javax.cache.processor.MutableEntry<{javax.cache.processor.EntryProcessor%0},{javax.cache.processor.EntryProcessor%1}>,java.lang.Object[])

CLSS public javax.cache.processor.EntryProcessorException
cons public <init>()
cons public <init>(java.lang.String)
cons public <init>(java.lang.String,java.lang.Throwable)
cons public <init>(java.lang.Throwable)
supr javax.cache.CacheException
hfds serialVersionUID

CLSS public abstract interface javax.cache.processor.EntryProcessorResult<%0 extends java.lang.Object>
meth public abstract {javax.cache.processor.EntryProcessorResult%0} get()

CLSS public abstract interface javax.cache.processor.MutableEntry<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf javax.cache.Cache$Entry<{javax.cache.processor.MutableEntry%0},{javax.cache.processor.MutableEntry%1}>
meth public abstract boolean exists()
meth public abstract void remove()
meth public abstract void setValue({javax.cache.processor.MutableEntry%1})

CLSS public abstract interface javax.cache.spi.CachingProvider
intf java.io.Closeable
meth public abstract boolean isSupported(javax.cache.configuration.OptionalFeature)
meth public abstract java.lang.ClassLoader getDefaultClassLoader()
meth public abstract java.net.URI getDefaultURI()
meth public abstract java.util.Properties getDefaultProperties()
meth public abstract javax.cache.CacheManager getCacheManager()
meth public abstract javax.cache.CacheManager getCacheManager(java.net.URI,java.lang.ClassLoader)
meth public abstract javax.cache.CacheManager getCacheManager(java.net.URI,java.lang.ClassLoader,java.util.Properties)
meth public abstract void close()
meth public abstract void close(java.lang.ClassLoader)
meth public abstract void close(java.net.URI,java.lang.ClassLoader)

CLSS public abstract interface !annotation javax.management.MXBean
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[TYPE])
intf java.lang.annotation.Annotation
meth public abstract !hasdefault boolean value()

