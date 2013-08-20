function pack_view(containerid, graphvis)
{
    var jq = jQuery;
    var nodedata;
    var packvis_obj = this;
    packvis_obj.graphvis = graphvis;

    function idstr(nodes)
    {
        return nodes.reduce(function(a, n) { return a + "," + n.id; }, "");
    }

    packvis_obj.markNodes = function() {};

    function nodesRepr(nodes)
    {
        return JSON.stringify(nodes.map(function(x) { return x.id; }))
    }

    function Splitter(postproc)
    {
        if (!postproc) 
        {
            postproc = function(x) { return x; };
        }

        function getLeafNodes(root)
        {
            var leaves = [];
            var flat = flattentree(root);
            for (var ni in flat)
            {
                var n = flat[ni];
                if (!n.children)
                {
                    leaves.push(n);
                }
            }
            return leaves;
        }

        function split(nodes, nodeid)
        {
            if (nodeid == null) nodeid = "";
            if (nodes.length > 1)
            {
                var mid = Math.floor(nodes.length / 2);
                var left = nodes.slice(0, mid);
                var right = nodes.slice(mid);
                var leftsplit = split(left, nodeid + "l");
                var rightsplit = split(right, nodeid + "r");
                nodecopy = nodes.slice(0);
                nodecopy.sort(function(a,b) { return a - b; });
                var tree =
                {
                    "name": "" + nodes.length,
                    "children": [leftsplit, rightsplit],
                    "id": nodeid 
                }
                tree.getLeafNodes = function()
                { 
                    var leaves = getLeafNodes(tree); 
                    return leaves;
                };
                return postproc(tree);
            }
            else if (nodes.length == 1)
            {
                var node = nodes[0];
                var tree =
                {
                    "name": "" + node.id,
                    "size": 1,
                    "id": node.id
                }
                tree.getLeafNodes = function()
                { 
                    var leaves = getLeafNodes(tree); 
                    return leaves;
                };
                return postproc(tree);
            }
            else 
            {
                return {};
            }
        }
        return {
            split: split
        };
    }

    function initpack()
    {
        var diameter = jq(containerid).width();

        var width = diameter,
            height = diameter;

        var pack = d3.layout.pack()
            .size([diameter - 4, diameter - 4])
            .value(function(d) { return d.size; })
            .sort(null); // tree traversal order

        var svg = d3.select(containerid).append("svg")
            .attr("width", diameter)
            .attr("height", diameter)
            .append("g")
            .attr("transform", "translate(2,2)");

        var background = svg.node();

        svg.append('svg:rect')
            .attr('width', width)
            .attr('height', height)
            .attr('fill', 'white');

        return {
            diameter: diameter,
            pack: pack,
            svg: svg,
            background: background
        };
    }

    function dragstart(d) {
        var state = packvis_obj.state;
        var circle = d3.select("#treenode" + d.id);
        var x = d.x + d.r + 5;
        var y = d.y;
        var items = state.pack.nodes(d).map(function(x) {
                return jq(d3.select("#treenode" + x.id).node()).clone();
            });
        // pack.nodes changes the tree, so reset the changes to x, y, r...
        // TODO: clone tree first instead
        state.pack.nodes(state.root);

        items[0].attr("translate");
        state.dragstart = {x: x, y: y};
        state.dragobj = d;
        jq(state.svg.append("g")
            .attr("id", "drag")
            .attr("opacity", "0.5")
            .node()).append(items);
    }
    function dragmove(d) {
        var state = packvis_obj.state;
        // cloned nodes have coordinates relative to svg, just like the mouse,
        // so translate drag group by distance the mouse moved from drag start
        state.svg.selectAll("circle").classed("dragtarget", false);
        d3.select(d3.event.sourceEvent.target).classed("dragtarget", true);
        var x = d3.mouse(state.background)[0] - state.dragstart.x;
        var y = d3.mouse(state.background)[1] - state.dragstart.y;
        d3.select("#drag")
            .attr("transform", "translate(" + x + "," + y + ")");
            
    }
    function dragend(d) {
        var state = packvis_obj.state;
        state.svg.selectAll("circle").classed("dragtarget", false);
        d3.select("#drag").remove();
        var flat = flattentree(state.root);
        var d1 = state.dragobj;
        var d2 = jq(d3.event.sourceEvent.target).data("dragtarget");
        // swap children
        for (var ni in flat)
        {
            var n = flat[ni];
            if (n.children === undefined) continue;
            if (n.children[0] == d1)
            {
                console.log("swap1");
                n.children[0] = d2;
            }
            else if (n.children[0] == d2)
            {
                console.log("swap2");
                n.children[0] = d1;
            }
            if (n.children[1] == d1)
            {
                console.log("swap3");
                n.children[1] = d2;
            }
            else if (n.children[1] == d2)
            {
                console.log("swap4");
                n.children[1] = d1;
            }
        }
        packvis_obj.refresh();
    }

    function pack()
    {
        var state = packvis_obj.state;
        var svg = state.svg;
        var pack = state.pack;
        var diameter = state.diameter;
        var format = d3.format(",d");
        var nodes = pack.nodes(state.root);

        treenodes = svg.selectAll(".treenode").data(nodes, function(x) { return x.id; });
        //var titles = svg.selectAll(".treenode title").data(nodes);

        //treenodes.attr("fill", "gray");
        var first = treenodes.empty();
        var newnodes = treenodes.enter().append("g");
        newnodes.attr("id", function(d) { return "treenode" + d.id.toString(); });
        if (!first)
        {
            treenodes.classed("entered", false);
            newnodes.classed("entered", true);
        }
        treenodes.order();

        var node_drag = d3.behavior.drag()
            .on("dragstart", dragstart) // using mousedown instead
            .on("drag", dragmove)
            .on("dragend", dragend);

        treenodes
            .classed("treenode", true)
            .classed("leaf", function(d) { return !d.children; })
            .call(node_drag)
            .on("mouseover",
                function(d)
                {
                    var ids = get_ids(d.getLeafNodes());
                    packvis_obj.markNodes(ids);
                })
            .on("mouseout",
                function(d)
                {
                    packvis_obj.markNodes([]);
                })
            .transition().duration(500)
            .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

        newnodes.append("title");
        treenodes.select("title")
            .text(function(d) { return d.title; });

        newnodes.append("circle");
        treenodes.select("circle")
            .transition().duration(500)
            .attr("r", function(d) { return d.r; })
            .each(function(d) { jq(this).data("dragtarget", d) });

        // show cutbool
        newnodes.filter(function(d) { return d.children; }).append("text");
        treenodes.filter(function(d) { return d.children; }).select("text")
            .transition().duration(500)
            .attr("dy", function(d) { return "-" + d.r / 1.5; })
            .style("text-anchor", "middle")
            .style("font-size", function(d) { return d.r / 3; })
            .text(function(d) { return d.name.substring(0, d.r / 3); });

        newnodes.filter(function(d) { return !d.children; }).append("text");
        treenodes.filter(function(d) { return !d.children; }).select("text")
            .transition().duration(500)
            .attr("dy", ".3em")
            .style("text-anchor", "middle")
            .style("font-size", function(d) { return d.r; })
            .text(function(d) { return d.name.substring(0, d.r / 3); });

        treenodes.exit()
          .transition().duration(500)
          .attr("r", 0)
          .remove();
        
        d3.select(self.frameElement).style("height", diameter + "px");
    }
    packvis_obj.pack = pack;

    function cutbool(simplegraph, leftnodes)
    {
        allnodes = get_ids(simplegraph.nodes);
        rightnodes = set.diff(allnodes, leftnodes);
        hoods = leftnodes.map(function(n) {
            var hood = set.intersect(simplegraph.nodes[n].neighbors, rightnodes);
            hood.sort(function(a,b){return a-b});
            return hood;
        });
        var emptyset = [];
        unions = {};
        unions[set.canonical(emptyset)] = 1;
        hoods.forEach(function(h) {
            var f = function(hood) {
                return function(u) { 
                    var oldhood = set.decanonical(u);
                    var newhood = set.union(oldhood, hood);
                    unions[set.canonical(newhood)] = 1;
                };
            };
            (Object.keys(unions)).forEach(f(h));
        });
        log = jq("#debug");
        for (u in unions)
        {
           log.append('<p>' + u + '</p>'); 
        }
        return unions;
    }

    function Cutter(simplegraph) 
    {
        function computeCut(tree)
        {
            var leftnodes = tree.getLeafNodes().map(function(n) { return n.id; });
            leftnodes.sort();
            cbnodes = JSON.stringify(leftnodes);
            // cache. take note on graph change if tree is not rebuilt.
            if (tree.cbnodes == cbnodes)
            {
                return tree;
            }
            var u = cutbool(simplegraph, leftnodes);
            var cb = Object.keys(u).length;
            tree.cutbool = cb;
            tree.size = cb;
            if (tree.children)
            {
                tree.name = "" + cb;
                tree.title = "" + tree.cbnodes;
            }
            return tree;
        }
        return computeCut;
    }

    // split again and redraw after graph changes
    function redraw_pack()
    {
        var graph = packvis_obj.graphvis.graph;
        var simplegraph = getSimpleGraph(graph);
        packvis_obj.cutter = Cutter(simplegraph);
        var root = Splitter(packvis_obj.cutter).split(graph.nodes);
        packvis_obj.state.root = root;
        packvis_obj.pack();
    }
    packvis_obj.redraw_pack = redraw_pack;

    function redraw_swap()
    {
        // recompute cutbool and redraw tree
        var flat = flattentree(packvis_obj.state.root);
        for (var ni in flat)
        {
            packvis_obj.cutter(flat[ni]); 
        }
        packvis_obj.pack();
    }

    function refresh()
    {
        for (var fi in packvis_obj.onchange)
        {
            packvis_obj.onchange[fi]();
        }
    }
    packvis_obj.refresh = refresh;

    packvis_obj.onchange = [redraw_swap];
    packvis_obj.state = initpack();
    packvis_obj.redraw_pack();

    return packvis_obj;
}
