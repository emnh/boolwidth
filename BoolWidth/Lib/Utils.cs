using System;
using System.Collections.Generic;

namespace BoolWidth.Lib
{
    static class Utils
    {
        public static TValue GetValueOrAddDefault<TKey, TValue>
            (this IDictionary<TKey, TValue> dictionary,
            TKey key,
            Func<TValue> defaultValueProvider)
        {
            TValue value;
            if (!(dictionary.TryGetValue(key, out value)))
            {
                value = defaultValueProvider();
                dictionary.Add(key, value);
            }
            return value;
        }
    }
}
