package Core;

import Core.DataStructures.Direction;
import Core.DataStructures.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class that deals with creating a field of view around the given object.
 * Ex. Player, light source, etc.
 */
public class FieldOfView {
    ArrayList<Point> visiblePoints = new ArrayList<>();
    Chunk floorGen;
    private final int radius;

    public FieldOfView(Chunk floorGen, int radius) {
        this.floorGen = floorGen;
        this.radius = radius;
    }

    public synchronized List<Point> computeFov(Point origin) {
        markVisible(origin);
        for (Direction dir : Direction.ORDINAL) {
            Quadrant quad = new Quadrant(dir, origin);
            Row firstRow = new Row(1, -1, 1);
            scan(quad, firstRow);
        }
        return this.visiblePoints;
    }




    public synchronized void scan(Quadrant quad, Row row) {
        if(row.depth >= this.radius) {
            return;
        }
        Point prevTile = null;
        for(Point tile : row) {
            if(isWall(quad, tile) || isSymmetric(row, tile)) {
                reveal(quad, tile);
            }
            if(isWall(quad, prevTile) && isFloor(quad, tile)) {
                row.startSlope = slope(tile);
            }
            if(isFloor(quad, prevTile) && isWall(quad, tile)) {
                Row nextRow = row.next();
                nextRow.endSlope = slope(tile);
                scan(quad, nextRow);
            }
            prevTile = tile;
        }
        if(isFloor(quad, prevTile)) {
            scan(quad, row.next());
        }
    }

    public synchronized void reveal(Quadrant quad, Point p) {
        int x = quad.transform(p).getX();
        int y = quad.transform(p).getY();
        markVisible(new Point(x, y));
    }

    public synchronized boolean isWall(Quadrant quad, Point p) {
        if (p == null) {
            return false;
        }
        int x = quad.transform(p).getX();
        int y = quad.transform(p).getY();
        return floorGen.isBlocking(new Point(x, y));
    }

    public synchronized boolean isFloor(Quadrant quad, Point p) {
        if (p == null) {
            return false;
        }
        int x = quad.transform(p).getX();
        int y = quad.transform(p).getY();
        return !floorGen.isBlocking(new Point(x, y));
    }

    public synchronized void markVisible(Point p) {
        visiblePoints.add(p);
    }



    private class Quadrant {
        Direction direction;
        Point origin;
        public Quadrant(Direction dir, Point origin) {
            this.direction = dir;
            this.origin = origin;
        }

        /**
         *
         * @param p A point relative to the origin of the quadrant
         */
        public Point transform(Point p) {
            int row = p.getX();
            int col = p.getY();
            int x;
            int y;
            if(direction.equals(Direction.UP)) {
                x = origin.getX() + col;
                y = origin.getY() - row;
            } else if(direction.equals(Direction.DOWN)) {
                x = origin.getX() + col;
                y = origin.getY() + row;
            } else if(direction.equals(Direction.RIGHT)) {
                x = origin.getX() + row;
                y = origin.getY() + col;
            } else {
                x = origin.getX() - row;
                y = origin.getY() + col;
            }
            if(x < 0) {
                x = 0;
            }else if( x >=  floorGen.getFloorArray().length) {
                x = floorGen.getFloorArray().length - 1;
            }

            if(y < 0) {
                y = 0;
            }else if( y >= floorGen.getFloorArray()[0].length) {
                y = floorGen.getFloorArray()[0].length - 1;
            }

            return new Point(x, y);
        }
    }


    private class Row implements Iterable<Point> {
        private int depth;
        private double startSlope;
        private double endSlope;


        public Row (int depth, double startSlope, double endSlope) {
            this.depth = depth;
            this.startSlope = startSlope;
            this.endSlope = endSlope;
        }


        public Row next() {
            return new Row(this.depth + 1, this.startSlope, this.endSlope);
        }

        @Override
        public Iterator<Point> iterator() {
            return new RowIterator(depth, startSlope, endSlope);
        }

        private class RowIterator implements Iterator<Point>{
            int minCol;
            int maxCol;
            int currCol;
            public RowIterator(int depth, double startSlope, double endSlope) {
                this.minCol = (int) roundTiesUp(depth * startSlope);
                this.maxCol = (int) roundTiesDown(depth * endSlope);
                this.currCol = minCol;
            }

            @Override
            public boolean hasNext() {
                return currCol <= maxCol + 1;
            }

            @Override
            public Point next() {
                Point p = new Point (depth, currCol);
                currCol++;
                return p;
            }
        }

    }

    public double slope (Point p) {
        int rowDepth = p.getX();
        int col = p.getY();
        return ((double) (2 * col - 2)) / (2 * rowDepth);
    }

    public boolean isSymmetric(Row row, Point tile) {
        int col = tile.getY();
        return (col >= row.depth * row.startSlope && col <= row.depth * row.endSlope);
    }

    public double roundTiesUp(double n) {
        return Math.floor(n + 0.5);
    }

    public double roundTiesDown(double n) {
        return Math.ceil(n - 0.5);
    }

}
