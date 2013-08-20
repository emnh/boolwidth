function force_view(containerid)
{
    var jq = jQuery;
    if (jq === undefined)
    {
        throw "needs jquery";
    }

    var force_obj = this;
    
    var width = jq(containerid).width(),
        height = jq(containerid).height(),
        fill = d3.scale.category20();

    // mouse event vars
    var selected_node = null,
        selected_link = null,
        mousedown_link = null,
        mousedown_node = null,
        mouseup_node = null,
        node_target = null;

    // init svg
    var outer = d3.select(containerid)
      .attr("width", width + 5)
      .attr("height", height + 5)
      .append("svg:svg")
        .attr("width", width)
        .attr("height", height)
        .attr("pointer-events", "all");

    var vis = outer
      .append('svg:g')
        .call(d3.behavior.zoom().on("zoom", rescale))
        .on("dblclick.zoom", null)
      .append('svg:g')
        .on("mousemove", mousemove)
        .on("mousedown", mousedown)
        .on("mouseup", mouseup);

    vis.append('svg:rect')
        .attr('width', width)
        .attr('height', height)
        .attr('fill', 'white');

    var background = vis.node();

    // init force layout
    var force_layout; 
    var graphstr;
    try
    {
        graphstr = window.localStorage['graph'];
    }
    catch (e)
    {
        console.log("localStorage not supported. can't save graph.");
    }
    if (graphstr !== undefined && graphstr !== null)
    {
        var graph = JSON.retrocycle(JSON.parse(graphstr));
        //var graph = JSON.retrocycle(graphstr);
        //var i = 1;
        //graph.nodes = graph.nodes.map(function(n) { n.id = i; i += 1; return n; });
        force_layout = d3.layout.force()
            .size([width, height])
            .nodes(graph.nodes)
            .links(graph.links)
            .linkDistance(50)
            .charge(-200)
            .on("tick", tick);

    }
    else
    {
        force_layout = d3.layout.force()
            .size([width, height])
            .nodes([{id: 1}]) // initialize with a single node
            .linkDistance(50)
            .charge(-200)
            .on("tick", tick);
    }

    // line displayed when dragging new nodes
    var drag_line = vis.append("line")
        .attr("class", "drag_line")
        .attr("x1", 0)
        .attr("y1", 0)
        .attr("x2", 0)
        .attr("y2", 0);

    var dragTarget;

    // get layout properties
    force_obj.nodes = force_layout.nodes();
    force_obj.links = force_layout.links();
    force_obj.graph = {
        nodes: force_obj.nodes,
        links: force_obj.links
    };

    var
        node = vis.selectAll(".node"),
        link = vis.selectAll(".link");

    function dragstart(d, i)
    { 
      mousedown_node = d;
      if (mousedown_node == selected_node) selected_node = null;
      else selected_node = mousedown_node; 
      selected_link = null;

      // reposition drag line
      drag_line
          .attr("class", "link")
          .attr("x1", mousedown_node.x)
          .attr("y1", mousedown_node.y)
          .attr("x2", mousedown_node.x)
          .attr("y2", mousedown_node.y);

      redraw(); 
    }

    function dragmove(d, i)
    {
        point = d3.mouse(background);
        tick();
    }

    function dragend(d, i)
    {
        var target = d3.event.sourceEvent.target;
        target = jq(target).data("dragtarget");
        if (target)
        {
            mouseup_node = target;
            add_edge(mouseup_node, mouseup_node.index);
        }
        else
        {
            point = d3.mouse(background);
            add_node(point);
        }

        // hide drag line
        drag_line
          .attr("class", "drag_line_hidden")

        // clear mouse event vars
        resetMouseVars();
    }

    function add_edge(d, i)
    {
        console.log("add_edge: " + d);
        if (mousedown_node) 
        {
            mouseup_node = d; 
            if (mouseup_node == mousedown_node) 
            { 
                console.log("self-loop");
                resetMouseVars();
                return;
            }

            // add link
            var link = {source: mousedown_node, target: mouseup_node};
            force_obj.links.push(link);

            // select new link
            selected_link = link;
            selected_node = null;

            // enable zoom
            //vis.call(d3.behavior.zoom().on("zoom"), rescale);
            redraw();
        }
        //mouseup();
    }


    var node_drag = d3.behavior.drag()
            .on("dragstart", dragstart) // using mousedown instead
            .on("drag", dragmove)
            .on("dragend", dragend);

    function mousedown()
    {
      console.log("svg mousedown");
      if (!mousedown_node && !mousedown_link)
      {
        // allow panning if nothing is selected
        //vis.call(d3.behavior.zoom().on("zoom"), rescale);
        return;
      }
    }

    function mousemove() 
    {
      if (!mousedown_node) return;
      
      //console.log("svg mousemove");

      // update drag line
      drag_line
          .attr("x1", mousedown_node.x)
          .attr("y1", mousedown_node.y)
          .attr("x2", d3.mouse(background)[0])
          .attr("y2", d3.mouse(background)[1]);
    }

    function mouseup()
    {
      console.log("svg mouseup");
      point = d3.mouse(background);
      add_node(point);
    }

    function add_node(point)
    {
      if (mousedown_node) {
        // hide drag line
        drag_line
          .attr("class", "drag_line_hidden")

        if (!mouseup_node) {
          // add node

          var node = {
            x: point[0],
            y: point[1],
            id: d3.max(force_obj.nodes, function(d) { return d.id; } ) + 1
          };
          n = force_obj.nodes.push(node);

          // select new node
          selected_node = node;
          selected_link = null;
          
          // add link to mousedown node
          force_obj.links.push({source: mousedown_node, target: node});
        }

        redraw();
      }
    }

    function resetMouseVars() 
    {
      mousedown_node = null;
      mouseup_node = null;
      mousedown_link = null;
    }

    function tick(e) 
    {
        link.attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });

        function crossing(n)
        {
            // determine if node has neighbor on the other side of bipartition
            force_obj.marked_nodes[o.id];
            return links.any(function(link) {
                var s = link.source;
                var t = link.target;
                if (s == n || s == t)
                {
                    var same_side = ids[s.id] == ids[t.id];
                    return !same_side;
                }
                else 
                {
                    return false;
                }
            });
        }

        if (force_obj.marked_nodes !== undefined && e !== undefined)
        {
          var k = 10 * e.alpha;
          force_obj.nodes.forEach(function(o, i) {
              //o.x += force_obj.marked_nodes[o.id] ? k : -k;
              //o.y += i & 1 ? k : -k;
              //o.x = force_obj.marked_nodes[o.id] ? 100 : o.x;
          });
        }

        node.attr("transform", function(d) { return 'translate(' + d.x + ',' + d.y + ')'; })
    }

    // rescale g
    function rescale() {
      trans=d3.event.translate;
      scale=d3.event.scale;

      vis.attr("transform",
          "translate(" + trans + ")"
          + " scale(" + scale + ")");
    }

    function getDragFunction() {
        var dragfunction;
        var im = d3.select("#interaction_mode");
        var value = im.property('value');
        if (value == "move")
        {
            dragfunction = force_layout.drag;
        }
        else
        {
            dragfunction = node_drag;
        }
        return dragfunction;
    }

    // redraw force layout
    function redraw() {

        function drawNodes()
        {

            node = node.data(force_obj.nodes, function(d) { return d.id; } );

            var g = node.enter().append("svg:g").attr("class", "node");

            g.append("svg:circle")
                .attr("class", "node")
                .attr("r", 5)
                .transition()
                    .duration(750)
                    .ease("elastic")
                    .attr("r", 10);

            g.append('svg:text')
                  .attr('x', 0)
                  .attr('y', 4)
                  .attr('class', 'id')
                  .text(function(d, i) { return d.id; });

            // drag on g and each child of .node svg:g
            g.call(getDragFunction())
            g.each(function(d, i) {
                var d3this = d3.select(this);
                d3this.call(getDragFunction());
                d3this.selectAll("*").each(function f(o) {
                        jq(this).data("dragtarget", d);
                    });
                });

            node.exit()
              .transition()
              .duration(500)
              .attr("r", 0)
              .remove();

            node
                .classed("node_selected", function(d) { return d === selected_node; });
            node
                .classed("node_target", function(d) { return d === node_target; });
        }

        function drawLinks()
        {
            link = link.data(force_obj.links);

            link.enter().insert("line", ".node")
              .attr("class", "link")
              .on("mousedown", 
                function(d) { 
                  mousedown_link = d; 
                  if (mousedown_link == selected_link) selected_link = null;
                  else selected_link = mousedown_link; 
                  selected_node = null; 
                  redraw(); 
                })

            link.exit().remove();

            link
            .classed("link_selected", function(d) { return d === selected_link; });
        }

        function saveData()
        {
            var graph = {
                nodes: force_obj.nodes,
                links: force_obj.links
            };

            var graphstr = JSON.stringify(JSON.decycle(graph));
            try
            {
                window.localStorage['graph'] = graphstr;
            }
            catch (e)
            {
                console.log("localStorage not supported. can't save graph.");
            }
            //var graphstr = JSON.decycle(graph);
            //$.totalStorage('graph', graphstr);
        }

        drawNodes();
        drawLinks();
        saveData();

        console.log("redraw"); 

        force_layout.start();

        if (force_obj.nodes && force_obj.links)
        {
            force_obj.redraw_callbacks.forEach(function(f) { f(); });
        }
    }
    force_obj.redraw_callbacks = [];

    function spliceLinksForNode(node) {
      toSplice = force_obj.links.filter(
        function(l) { 
          return (l.source === node) || (l.target === node); });
      toSplice.map(
        function(l) {
          force_obj.links.splice(force_obj.links.indexOf(l), 1); });
    }

    function keydown() {
      if (!selected_node && !selected_link) return;
      switch (d3.event.keyCode) {
        case 8: // backspace
        case 46: { // delete
          if (selected_node) {
            force_obj.nodes.splice(force_obj.nodes.indexOf(selected_node), 1);
            spliceLinksForNode(selected_node);
          }
          else if (selected_link) {
            force_obj.links.splice(force_obj.links.indexOf(selected_link), 1);
          }
          selected_link = null;
          selected_node = null;
          redraw();
          break;
        }
      }
    }

    function markNodes(node_ids)
    {
        var ids = {};
        for (var i in node_ids) 
        {
            ids[node_ids[i]] = 1;
        }
        node.classed("node_marked", function(d) { return ids[parseInt(d.id)]; });
        force_obj.marked_nodes = ids;
        //console.log(force_layout.linkStrength);
        force_layout.linkStrength(
            function(link)
            {
                var s = link.source;
                var t = link.target;
                var same_side = ids[s.id] == ids[t.id];
                return same_side ? 1 : 0;
            });
        force_layout.start();
    }
    this.markNodes = markNodes;

    /// INIT CODE

    // add keyboard callback
    d3.select(window).on("keydown", keydown);

    redraw();

    // focus on svg
    // vis.node().focus();

    d3.select("#interaction_mode").on("change", 
        function(i) {
           console.log("change"); 
           d3.selectAll(".node").call(getDragFunction());
        });

    return force_obj;
}
