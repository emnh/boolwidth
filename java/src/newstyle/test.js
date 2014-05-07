var map = Array.prototype.map;
var names = ["john", "jerry", "bob"];
var a = map.call(names, function(name) { return name.length() });
print(a);