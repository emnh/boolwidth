abstract class IBinNode {
    type T >: Null <: IBinNode
    var left : T = null
    var right : T = null
    var parent : T = null

    override def toString() : String = {
        return String.format("%s:(%s, %s, %s)\n", super.toString(), left, right, parent)
    }

    def nodeMethod() {
        printf("%s: %s\n", super.toString, "I'm a binnode")
        if (parent != null) {
            parent.nodeMethod()
        }
    }
}

class BinNode extends IBinNode {
    type T = BinNode
}

abstract class IDNode extends IBinNode {
    type T >: Null <: DNode

    override def nodeMethod() {
        super.nodeMethod()
        printf("%s: %s\n", super.toString, "I'm a dnode")
    }
}
class DNode extends IDNode {
    type T = DNode
}

class BinTree[TBinNode >: Null <: IBinNode] {

    var root : TBinNode = null

    def doRoot() {
        root.nodeMethod()
    }
}

object Nodes {
    def main(args : Array[String]) {
        val b = new BinNode
        b.left = new BinNode
        b.right = new BinNode
        b.parent = new BinNode

        val d = new DNode
        d.left = new DNode
        d.right = new DNode
        d.parent = new DNode

        println(b)
        println(d)

        val btree = new BinTree[BinNode]
        btree.root = b
        val dtree = new BinTree[DNode]
        dtree.root = d
        
        btree.doRoot()
        dtree.doRoot()
    }
}
