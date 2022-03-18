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

            currentTile = currentTile.tailingNode;
        } while(currentTile.tailingNode != null && currentTile.isActive());
    }

    public void move() {
        currentTile = getPath(currentTile);
        currentTile.createChildren();
    }

    public boolean tileOccupied(Position position) {
        return tileMap.containsKey(position);
    }

    public abstract boolean tileIsWall(Position tile);

    public void indicateTailSkip() {}

    public TileNode getPath(TileNode tile) {
        for(int i = 0; i < tile.childrenNode.length; i++) {
            TileNode tilePath = tile.childrenNode[i];
            if (tilePath != null) {
                tile.childrenNode[i] = null;
                tile.totalChildren--;
                if(!tile.isActive()) {
                    tilePath.tailingNode = currentTile.tailingNode;
                    indicateTailSkip();
                }
                return tilePath;
            }
        }
        throw new Error("Wrong execution sequence");
    }

    class TileNode {
        Position position;
        TileNode[] childrenNode;
        TileNode tailingNode;
        byte totalChildren = 0;

        TileNode(Position position, TileNode parentNode) {
            this.position = position;
            tailingNode = parentNode;
            tileMap.put(position, this);
        }

        public void createChildren() {
            childrenNode = new TileNode[4];

            Position newPosition = position.offset(0, -1);
            if(!tileOccupied(newPosition) && !tileIsWall(newPosition)) {
                totalChildren++;
                childrenNode[0] = new TileNode(newPosition, this);
            }
            newPosition = position.offset(1, 0);
            if(!tileOccupied(newPosition) && !tileIsWall(newPosition)) {
                totalChildren++;
                childrenNode[1] = new TileNode(newPosition, this);
            }
            newPosition = position.offset(-1, 0);
            if(!tileOccupied(newPosition) && !tileIsWall(newPosition)) {
                totalChildren++;
                childrenNode[2] = new TileNode(newPosition, this);
            }
            newPosition = position.offset(0, 1);
            if(!tileOccupied(newPosition) && !tileIsWall(newPosition)) {
                totalChildren++;
                childrenNode[3] = new TileNode(newPosition, this);
            }
        }

        public boolean isActive() {
            return totalChildren > 0;
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
        public long toHashCode() {
            long hash = x;
            hash <<= 32;
            hash |= y;
            return hash;
        }
    }

    class SimpleTileMap {
        //ArrayList<TileNode> Values = new ArrayList<>();
        ArrayList<Long> Keys = new ArrayList<>();

        public void put(Position key, TileNode value) {
            Keys.add(key.toHashCode());
            //Values.add(value);
        }

        public boolean containsKey(Position Key) {
            return Keys.contains(Key.toHashCode());
        }
    }
}