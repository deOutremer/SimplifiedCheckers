public class Tile {

    private Color tileColor;
    private Color tileOccupancy;
    private Location locationOnBoard;


    public Tile (Color tileColor, Color tileOccupancy, Location locationOnBoard) {
        this.tileColor = tileColor;
        this.tileOccupancy = tileOccupancy;
        this.locationOnBoard = locationOnBoard;
    }

    public Color getPieceColor () {
        return tileOccupancy;
    }

    public void setPieceColor (Color pieceColor) {
        tileOccupancy = pieceColor;
    }

    public Color getTileColor () {
        return tileColor;
    }

    public Location getLocation() { return locationOnBoard;}

}
