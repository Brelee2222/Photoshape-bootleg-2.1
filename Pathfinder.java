import java.util.*;

//This is meant to be like a pathfinder more than Bucket for photoshop.

public abstract class Pathfinder {

    boolean debug = true;

    TileNode currentTile;

    SimpleTileMap tileMap = new SimpleTileMap();

    public void findPath(int x, int y) {
        currentTile = new TileNode(new Position(x, y), null);
        currentTile.createChildren();

        do {
            while (currentTile.isActive())
                move();
            //System.out.println(tileOccupied(currentTile.position));
            indicateTailSkip();
        } while(currentTile != null);
    }

    public void move() {
        currentTile = getPath(currentTile);
        currentTile.createChildren();
    }

    public boolean tileOccupied(Position position) {
        return tileMap.containsKey(position);
    }

    public abstract boolean tileIsWall(Position tile);

    public TileNode getPath(TileNode tile) {
//        if(!tile.isActive())
//            return tile.tailingNode;
        TileNode tilePath = tile.childrenNode[tile.totalChildren];
        tile.totalChildren--;
        if(!tile.isActive())
            tilePath.tailingNode = currentTile.tailingNode;
        return tilePath;
    }

    public void indicateTailSkip() {
        currentTile = currentTile.tailingNode;
    }

    class TileNode {
        Position position;
        TileNode[] childrenNode;
        TileNode tailingNode;
        byte totalChildren = -1;

        TileNode(Position position, TileNode parentNode) {
            this.position = position;
            tailingNode = parentNode;
            tileMap.put(position);
        }

        public void createChildren() {
            childrenNode = new TileNode[3];

            Position newPosition = position.offset(0, -1);
            if(!tileIsWall(newPosition) && !tileOccupied(newPosition))
                createChild(newPosition);
            newPosition = position.offset(-1, 0);
            if(!tileIsWall(newPosition) && !tileOccupied(newPosition))
                createChild(newPosition);
            newPosition = position.offset(0, 1);
            if(!tileIsWall(newPosition) && !tileOccupied(newPosition)) {
                createChild(newPosition);
                if(totalChildren == 2)
                    return;
            }
            newPosition = position.offset(1, 0);
            if(!tileIsWall(newPosition) && !tileOccupied(newPosition))
                createChild(newPosition);

        }

        public void createChildren(int limit) {
            limit--;
            childrenNode = new TileNode[4];

            Position newPosition = position.offset(0, -1);
            if(!tileIsWall(newPosition) && !tileOccupied(newPosition)) {
                createChild(newPosition);
                if(limit == totalChildren)
                    return;
            }
            newPosition = position.offset(-1, 0);
            if(!tileIsWall(newPosition) && !tileOccupied(newPosition)) {
                createChild(newPosition);
                if (limit == totalChildren)
                    return;
            }
            newPosition = position.offset(0, 1);
            if(!tileIsWall(newPosition) && !tileOccupied(newPosition)) {
                createChild(newPosition);
                if (limit == totalChildren)
                    return;
            }
            newPosition = position.offset(1, 0);
            if(!tileIsWall(newPosition) && !tileOccupied(newPosition)) {
                createChild(newPosition);
                if (limit == totalChildren)
                    return;
            }

        }

        public boolean isActive() {
            return totalChildren >= 0;
        }

        public void createChild(Position newPosition) {
            totalChildren++;
            childrenNode[totalChildren] = new TileNode(newPosition, this);
        }

    }

    class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position offset(int x, int y) {
            return new Position(this.x + x, this.y + y);
        }

        public String toString() {
            return "(" + "x : " + x + ", y : " + y + ")";
        }

        //I'm using a fake hash code because there are int*int possibilities
        public boolean equals(Position value) {
            return value.x == x && value.y == y;
        }
    }

    class SimpleTileMap {
        //ArrayList<TileNode> Values = new ArrayList<>();
//        Map Keys = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Position> Keys = new ArrayList<>();

        //will be key, value
        public void put(Position key) {
            Keys.add(key);
        }

        public boolean containsKey(Position Key) {
            for(Position key : Keys)
                if(key.equals(Key))
                    return true;
            return false;
        }
    }
}