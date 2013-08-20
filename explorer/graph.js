function getSimpleGraph(graph)
{
    var simplenodes = {};
    graph.nodes.forEach(function(n) {
        simplenodes[n.id] = {
            id: n.id,
            neighbors: []
        };
    });
    var simplelinks =
        graph.links.map(function(l) {
            simplenodes[l.source.id].neighbors.push(l.target.id);
            simplenodes[l.target.id].neighbors.push(l.source.id);
            return [l.source.id, l.target.id]
        });
    var simplegraph = {
        nodes: simplenodes,
        links: simplelinks
    }
    return simplegraph;
};

function formatGraph(graph)
{
    var s = [];
    var g = getSimpleGraph(graph);
    s.push('# dgf format')
    s.push('# nodes: ' + graph.nodes.map(function(x) { return x.id; }));
    s.push('p edge ' + graph.nodes.length + ' ' + graph.links.length);
    g.links.sort(function(x, y) { return x[0] - y[0]; });
    for (var li in g.links)
    {
        var l = g.links[li];
        s.push('e ' + l[0] + ' ' + l[1]);
    }
    s = s.reduce(function(x, y) { return x + '\n' + y; } );
    return s;
}

function flattentree(root)
{
    var flat = [];
    var stack = [root];
    while (!(stack.length == 0))
    {
        root = stack.pop();
        flat.push(root);
        for (var c in root.children)
        {
            stack.push(root.children[c]);
        }
    }
    return flat;
}

function get_ids(nodes)
{
    // function works on maps as well as arrays, so the following won't work
    // return nodes.map(function(x) { return x.id; });
    var ids = [];
    for (var node in nodes)
    {
        ids.push(nodes[node].id);
    }
    return ids;
}

function formatTree(tree)
{
    var s = [];
    s.push('# format: <treenode> <leafnodes>');
    s.push('# name of <treenode> = (<r[ight]>|<l[eft]>)*');
    s.push('# e.g. rll for right left left. this unique tree node id format is arbitrary.');
    var flat = flattentree(tree);
    for (var fi in flat)
    {
        var f = flat[fi];
        var id = f.id == '' ? ' ' : f.id;
        if (f.children) s.push(id + ' ' + get_ids(f.getLeafNodes()));
    }
    s = s.reduce(function(x, y) { return x + '\n' + y; } );
    return s;
}
