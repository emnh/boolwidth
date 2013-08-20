var set =
{
    canonical: function(a)
    {
        a.sort();
        return JSON.stringify(a);
    },
    decanonical: function(a)
    {
        return JSON.parse(a);
    },
    diff: function(a, b)
    {
        bmap = {};
        ret = [];
        b.forEach(function(n) { bmap[n] = true}); 
        a.forEach(function(n) { if (!bmap[n]) ret.push(n); }); 
        ret.sort();
        return ret;
    },
    union: function(a, b)
    {
        bmap = {};
        ret = [];
        b.forEach(function(n) { ret.push(n); bmap[n] = true}); 
        a.forEach(function(n) { if (!bmap[n]) ret.push(n); }); 
        ret.sort();
        return ret;
    },
    intersect: function(a, b)
    {
        bmap = {};
        ret = [];
        b.forEach(function(n) { bmap[n] = true}); 
        a.forEach(function(n) { if (bmap[n]) ret.push(n); }); 
        ret.sort();
        return ret;
    }
};
